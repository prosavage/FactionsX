package net.prosavage.factionsx.core

import com.cryptomorin.xseries.XMaterial
import net.prosavage.factionsx.event.FPlayerFactionLeaveEvent
import net.prosavage.factionsx.event.FPlayerFactionPreLeaveEvent
import net.prosavage.factionsx.manager.*
import net.prosavage.factionsx.manager.FactionManager.forceRelation
import net.prosavage.factionsx.persist.Message
import net.prosavage.factionsx.persist.Message.shieldEnded
import net.prosavage.factionsx.persist.Message.shieldStarted
import net.prosavage.factionsx.persist.config.Config
import net.prosavage.factionsx.persist.config.Config.teleportFromClaimOnLeave
import net.prosavage.factionsx.persist.config.Config.teleportFromClaimOnLeaveLocation
import net.prosavage.factionsx.persist.config.EconConfig
import net.prosavage.factionsx.persist.config.gui.UpgradesGUIConfig
import net.prosavage.factionsx.persist.data.FLocation
import net.prosavage.factionsx.persist.data.Players
import net.prosavage.factionsx.persist.data.getFLocation
import net.prosavage.factionsx.persist.data.wrappers.DataLocation
import net.prosavage.factionsx.persist.data.wrappers.Warp
import net.prosavage.factionsx.upgrade.Upgrade
import net.prosavage.factionsx.util.*
import org.apache.commons.lang.WordUtils
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import java.time.LocalTime
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.set

data class Faction(val id: Long, var tag: String, val factionRoles: FactionRoles, val relationPerms: RelationPerms, var ownerId: UUID?) {

    var creationDate = System.currentTimeMillis()
    val factionMembers = mutableSetOf<UUID>()

    var factionAlts = mutableSetOf<UUID>()
        get() {
            if (field == null) field = mutableSetOf()
            return field
        }

    var altsOpen = false

    data class RelationEntry(val relation: Relation, val whoRequested: Long)
    var relations = hashMapOf<Long, RelationEntry>()
    var relationRequests = hashMapOf<Long, Relation>()

    var warps = hashMapOf<String, Warp>()
    var description = Config.defaultFactionDescription
    var maxPowerBoost: Double = 0.0
    var globalUpgrades = HashMap<String, Int>()
        get() {
            if (field == null) field = HashMap()
            return field
        }

    var strikes: MutableList<String> = LinkedList()
        private set
        get() {
            if (field == null) field = LinkedList()
            return field
        }

    var warpBoost = 0
    var allyBoost = 0
    var enemyBoost = 0
    var truceBoost = 0
    var memberBoost = 0
    var maxClaimBoost = 0


    class FLocationMetadata(var name: String, var icon: XMaterial, var upgrades: HashMap<String, Int>) {
        var access: FLocationAccessPoint = FLocationAccessPoint(hashMapOf(), hashMapOf())
            get() {
                if (field == null) field = FLocationAccessPoint(hashMapOf(), hashMapOf())
                return field
            }

        fun reset() {
            name = UpgradesGUIConfig.defaultClaimName
            icon = UpgradesGUIConfig.defaultClaimIcon
            upgrades.clear()
            with (access) {
                factions.clear()
                players.clear()
            }
        }

        fun setUpgrade(upgrade: Upgrade, level: Int) {
            upgrades[upgrade.name] = level
        }

        fun getUpgrade(upgrade: Upgrade): Int {
            return upgrades[upgrade.name] ?: 0
        }

        fun isUpgraded(upgrade: Upgrade): Boolean {
            return upgrades.containsKey(upgrade.name)
        }
    }

    data class FLocationAccessPoint(internal var factions: HashMap<Long, EnumSet<PlayerAction>>, internal var players: HashMap<UUID, EnumSet<PlayerAction>>) {
        fun provide(faction: Faction, action: PlayerAction): Boolean = this.modify(Type.PROVIDE, faction.id, action)
        fun provide(player: FPlayer, action: PlayerAction): Boolean = this.modify(Type.PROVIDE, player.uuid, action)
        fun revoke(faction: Faction, action: PlayerAction): Boolean = this.modify(Type.REVOKE, faction.id, action)
        fun revoke(player: FPlayer, action: PlayerAction): Boolean = this.modify(Type.REVOKE, player.uuid, action)
        fun opposite(faction: Faction, action: PlayerAction): Type = this.opposite(faction, true, action)
        fun opposite(player: FPlayer, action: PlayerAction): Type = this.opposite(player, false, action)
        fun canPerform(faction: Faction, action: PlayerAction): Boolean = this.factions[faction.id]?.contains(action) == true
        fun canPerform(player: FPlayer, action: PlayerAction): Boolean = this.players[player.uuid]?.contains(action) == true

        private fun opposite(key: Any, isFaction: Boolean, action: PlayerAction): Type {
            return if (isFaction) {
                key as Faction
                val wasRemoved = revoke(key, action)

                if (wasRemoved) return Type.REVOKE
                provide(key, action); Type.PROVIDE
            } else {
                key as FPlayer
                val wasRemoved = revoke(key, action)

                if (wasRemoved) return Type.REVOKE
                provide(key, action); Type.PROVIDE
            }
        }

        private fun modify(type: Type, key: Any, action: PlayerAction): Boolean {
            val old = (if (key is Long) factions[key] else players[key as UUID]) ?: EnumSet.noneOf(PlayerAction::class.java)
            val wasSuccessful = when (type) {
                Type.PROVIDE -> old.add(action)
                Type.REVOKE  -> old.remove(action)
            }

            if (wasSuccessful) when (key) {
                is Long -> factions[key] = old
                else -> players[key as UUID] = old
            }
            return wasSuccessful
        }

        enum class Type {
            PROVIDE,
            REVOKE
        }
    }

    var shielded = false
    var shieldTimeStart: LocalTime? = null
    var shieldEndDate: Date? = null

    fun isShielded(): Boolean = shielded

    fun getShieldTime(): Long? = TimerManager.getTimeTask("$id-shield-end")?.getTimeLeft()

    fun getNextShield(): Long? = TimerManager.getTimeTask("$id-shield-manage")?.getTimeLeft()

    fun getNextShieldFormatted(): String? = getNextShield()?.let { formatMillis(it) }

    fun setShield() {
        var difference = shieldTimeStart?.toDate()?.time?.minus(Date().time) ?: return
        // Set shield for tomorrow.
        if (difference < 0) difference = (shieldTimeStart!!.toDate().time + TimeUnit.DAYS.toMillis(1)) - Date().time
        setShield(Date(Date().time + difference))
    }

    fun setShield(startDate: Date) {
        val shieldID = "$id-shield-manage"
        if (TimerManager.getTimeTask(shieldID) != null) {
            // Should not happen but just in case API call etc.
            logColored("Tried to set shield but already have one registered or on cooldown.")
            return
        }
        TimerManager.registerTimeTask(shieldID, TimeTask(startDate, Runnable {
            shielded = true
            setShield()
            message(shieldStarted)
            shieldEndDate = Date(startDate.time + (Config.factionShield.durationInSeconds * 1000))
            registerShieldEndTimer(Date(startDate.time + (Config.factionShield.durationInSeconds * 1000)))
        }))
    }

    fun disableShield() {
        shielded = false
        shieldEndDate = null
        message(shieldEnded)
    }

    fun registerShieldEndTimer(date: Date) {
        //println("Registering End Timer.")
        TimerManager.registerTimeTask("$id-shield-end",
                TimeTask(date,
                        Runnable {
                            disableShield()
                        }))
    }

    fun checkPermissionForFaction(faction: Faction, playerAction: PlayerAction): Boolean {
        return relationPerms.getPermForRelation(getRelationTo(faction), playerAction)
    }

    fun addStrike(striker: CommandSender, reason: String) {
        strikes.add(reason)

        if (Config.factionStrikeAlerts) {
            this.message(Message.commandStrikesGiveAlert, reason, striker.name)
        }

        Config.factionStrikeActions[strikes.size]?.forEach { action ->
            PlaceholderAction.trigger(this, action)
        }
    }

    fun removeStrike(striker: CommandSender, id: Int) {
        if (id <= 0 || strikes.size < id) return
        strikes.removeAt(id - 1)

        if (Config.factionStrikeAlerts) {
            this.message(Message.commandStrikesRemoveAlert, id.toString(), striker.name)
        }
    }

    fun editStrike(editor: CommandSender, id: Int, modification: String) {
        if (id <= 0 || strikes.size < id) return
        strikes[id - 1] = modification

        if (Config.factionStrikeAlerts) {
            this.message(Message.commandStrikesEditAlert, id.toString(), modification, editor.name)
        }
    }

    fun clearStrikes(striker: CommandSender) {
        if (strikes.isEmpty()) return
        strikes.clear()

        if (Config.factionStrikeAlerts) {
            this.message(Message.commandStrikesClearAlert, striker.name)
        }
    }


    var claimUpgrades = HashMap<FLocation, FLocationMetadata>()
        get() {
            if (field == null) field = HashMap()
            return field
        }

    fun getFLocationMetadata(fLocation: FLocation): FLocationMetadata {
        return claimUpgrades.getOrPut(fLocation) { FLocationMetadata(UpgradesGUIConfig.defaultClaimName, UpgradesGUIConfig.defaultClaimIcon, HashMap()) }
    }

    fun setUpgrade(upgrade: Upgrade, level: Int) {
        globalUpgrades[upgrade.name] = level
    }

    fun getUpgrade(upgrade: Upgrade): Int {
        return globalUpgrades[upgrade.name] ?: 0
    }

    fun isUpgraded(upgrade: Upgrade): Boolean {
        return globalUpgrades.containsKey(upgrade.name)
    }


    var home: DataLocation? = null


    init {
        if (ownerId != null) factionMembers.add(ownerId!!)
    }

    var claimAmt = 0

    var unconnectedClaimAmt = 0

    fun resetAllWarps() {
        warps.clear()
    }

    fun setWarp(name: String, password: String?, dataLocation: DataLocation) {
        warps[name] = Warp(name.toLowerCase(), password, dataLocation)
    }

    fun removeWarp(name: String) {
        // Lowercase home cause all homes are lowercase.
        warps.remove(name.toLowerCase())
    }

    fun getMaxWarps(): Int {
        return Config.warpLimit + warpBoost
    }

    fun getAllWarps(): List<Warp> {
        return warps.values.toList()
    }

    fun getWarp(name: String): Warp? {
        return warps[name.toLowerCase()]
    }

    fun hasWarp(name: String): Boolean {
        return warps.containsKey(name.toLowerCase())
    }


    fun isWilderness(): Boolean {
        return id == FactionManager.WILDERNESS_ID
    }

    fun isWarzone(): Boolean {
        return id == FactionManager.WARZONE_ID
    }

    fun isSafezone(): Boolean {
        return id == FactionManager.SAFEZONE_ID
    }

    fun isSystemFaction(): Boolean {
        return isWilderness() || isWarzone() || isSafezone()
    }

    fun getLeader(): FPlayer? {
        return PlayerManager.getFPlayer(ownerId)
    }

    fun setLeader(fPlayer: FPlayer) {
        ownerId = fPlayer.uuid
    }

    fun inviteMember(fPlayer: FPlayer, isAlt: Boolean = false) {
        if (getMaxMembers() <= factionMembers.size) return
        fPlayer.inviteToFaction(this, isAlt)
    }

    fun deinviteMember(fPlayer: FPlayer) {
        fPlayer.deInviteFromFaction(this)
    }

    fun getMembers(): Set<FPlayer> {
        val members = factionMembers.mapNotNull { uuid -> PlayerManager.getFPlayer(uuid) }.toHashSet()
        val leader = getLeader()
        if (leader != null) {
            members.add(leader)
        }
        return members
    }

    fun getAlts(): Set<FPlayer> = factionAlts.mapNotNull(PlayerManager::getFPlayer).toHashSet()

    fun sendFactionInfo(fPlayer: FPlayer) {
        val bar = buildBar(Message.commandShowBarElement, Config.barLength)
        fPlayer.message(bar, noPrefix = Config.sendPrefixForWhoCommand.not())
        for (line in if (isWilderness()) Config.whoFormatWilderness else Config.whoFormat) {
            fPlayer.message(PlaceholderManager.processPlaceholders(fPlayer, this, line), noPrefix = Config.sendPrefixForWhoCommand.not())
        }
        fPlayer.message(bar, noPrefix = Config.sendPrefixForWhoCommand.not())
    }


    fun getOnlineMembers(): Set<FPlayer> {
        return getMembers().filter(FPlayer::isOnline).toSet()
    }

    fun getOfflineMembers(): Set<FPlayer> {
        return getMembers().filter(FPlayer::isOffline).toSet()
    }

    fun getOnlineAlts(): Set<FPlayer> = getAlts().filter(FPlayer::isOnline).toSet()
    fun getOfflineAlts(): Set<FPlayer> = getAlts().filter(FPlayer::isOffline).toSet()

    fun getPower(): Double {
        return getMembers().plus(getAlts()).map(FPlayer::power).sum()
    }

    fun getMaxPower(): Double {
        return getMembers().plus(getAlts()).map(FPlayer::getMaxPower).sum()
    }

    fun getMaxAllies(): Int {
        return Config.allyLimit + allyBoost
    }

    fun getMaxEnemies(): Int {
        return Config.enemyLimit + enemyBoost
    }

    fun getMaxTruces(): Int {
        return Config.truceLimit + truceBoost
    }

    fun getMaxClaims(): Int {
        return Config.maxClaimLimit + maxClaimBoost
    }

    fun isInvited(fplayer: FPlayer): Boolean {
        return fplayer.factionsInvitedTo.contains(id)
    }

    fun getInvite(fPlayer: FPlayer): Boolean? = fPlayer.factionsInvitedTo[this.id]

    fun isInvitedAsMember(fPlayer: FPlayer): Boolean? = fPlayer.factionsInvitedTo[this.id]?.not()

    fun isInvitedAsAlt(fPlayer: FPlayer): Boolean? = isInvitedAsMember(fPlayer)?.not()

    fun getAltInvites(): Set<FPlayer> = Players.fplayers.values.filter { getInvite(it) == true }.toSet()

    fun getMaxMembers(): Int {
        return Config.factionMemberLimit + memberBoost
    }

    fun addMember(fPlayer: FPlayer, role: CustomRole, asAdmin: Boolean = false, silently: Boolean = true) {
        if (!asAdmin && getMaxMembers() <= factionMembers.size) return
        if (!factionMembers.contains(fPlayer.uuid)) {
            factionMembers.add(fPlayer.uuid)
        }
        fPlayer.assignFaction(this, role)
    }

    fun removeMember(fPlayer: FPlayer, isAdminKick: Boolean = false) {
        FPlayerFactionPreLeaveEvent(fPlayer, this, isAdminKick).let {
            Bukkit.getPluginManager().callEvent(it)
            if (it.isCancelled) return
        }

        factionMembers.remove(fPlayer.uuid)
        fPlayer.unassignFaction()
        if (factionMembers != null && factionMembers.size == 1) {
            ownerId = factionMembers.first()
            getMembers().first().role = factionRoles.getApexRole()
        }

        if (teleportFromClaimOnLeave && teleportFromClaimOnLeaveLocation != null) {
            if (fPlayer.isOnline()) {
                val player = fPlayer.getPlayer()!!
                val factionAt = getFLocation(player.location).getFaction()
                if (factionAt == this) player.teleport(teleportFromClaimOnLeaveLocation ?: player.world.spawnLocation)
            } else {
                val lastLocation = fPlayer.lastLocation
                if (lastLocation != null) {
                    val factionAt = getFLocation(lastLocation).getFaction()
                    if (factionAt == this) fPlayer.teleportToSpawnOnLogin = true
                }
            }
        }

        Bukkit.getPluginManager().callEvent(FPlayerFactionLeaveEvent(fPlayer, this, isAdminKick))
    }

    fun addAlt(fPlayer: FPlayer) {
        val uuid = fPlayer.uuid
        if (uuid !in factionAlts) factionAlts.add(uuid)

        fPlayer.assignFaction(this, factionRoles.getMinimumRole())
        fPlayer.alt = true
    }

    fun removeAlt(fPlayer: FPlayer) {
        factionAlts.remove(fPlayer.uuid)
        fPlayer.unassignFaction(true)

        if (fPlayer.isOnline()) {
            val player = fPlayer.getPlayer()!!
            val factionAt = getFLocation(player.location).getFaction()
            if (factionAt == this) player.teleport(player.world.spawnLocation)
        } else fPlayer.teleportToSpawnOnLogin = true

        Bukkit.getPluginManager().callEvent(FPlayerFactionLeaveEvent(fPlayer, this, true))
    }

    fun message(line: String, excludeFPlayers: List<FPlayer> = emptyList()) {
        getMembers().plus(getAlts()).forEach { member -> if (!excludeFPlayers.contains(member)) member.message(line) }
    }

    fun message(line: String, vararg args: String, excludeFPlayers: List<FPlayer?> = emptyList()) {
        getMembers().plus(getAlts()).forEach { member -> if (!excludeFPlayers.contains(member)) member.message(line, *args) }
    }

    private fun sendRelationUpdateMessage(toFaction: Faction, relation: Relation) {
        val tagReplacement = relation.tagReplacement
        val relationPrefix = PlaceholderManager.getRelationPrefix(relation)

        this.message(Message.relationUpdate, toFaction.tag, relationPrefix, tagReplacement)
        toFaction.message(Message.relationUpdate, this.tag, relationPrefix, tagReplacement)
    }

    internal fun handleRelation(faction: Faction, relation: Relation, fPlayer: FPlayer) {
        val tagReplacement = relation.tagReplacement
        if (relation in Config.disabledRelations) {
            fPlayer.message(Message.relationIsDisabled, tagReplacement)
            return
        }

        val currentEntry = relations[faction.id]
        val currentRelation = currentEntry?.relation ?: Relation.NEUTRAL
        val toFactionTag = faction.tag

        if (currentRelation === relation) {
            fPlayer.message(Message.relationAlreadySet, tagReplacement, toFactionTag)
            return
        }

        if (relation === Relation.ENEMY) {
            forceRelation(this, faction, RelationEntry(relation, this.id))
            this.sendRelationUpdateMessage(faction, relation)
            return
        }

        if (relation === Relation.NEUTRAL && (currentRelation === Relation.ENEMY && !Config.forceBothFactionsToRequestNeutral && currentEntry?.whoRequested == this.id || currentRelation !== Relation.ENEMY)) {
            forceRelation(this, faction, RelationEntry(Relation.NEUTRAL, this.id))
            this.sendRelationUpdateMessage(faction, relation)
            return
        }

        val fromRequest = faction.relationRequests[this.id]
        if (fromRequest === relation) {
            forceRelation(this, faction, RelationEntry(relation, this.id))
            this.sendRelationUpdateMessage(faction, relation)
            return
        }

        val relationPrefix = PlaceholderManager.getRelationPrefix(relation)
        if (this.sendRelationRequest(faction, relation) === relation) {
            fPlayer.message(Message.relationAlreadyRequested, relationPrefix, tagReplacement, toFactionTag)
            return
        }

        this.message(Message.relationNotifOrigin, toFactionTag, relationPrefix, tagReplacement)
        faction.message(Message.relationNotif, this.tag, relationPrefix, tagReplacement)
    }

    internal fun sendRelationRequest(faction: Faction, relation: Relation): Relation? {
        return relationRequests.putIfAbsent(faction.id, relation)
    }

    var openStatus: Boolean = false

    fun isOpen(): Boolean = openStatus

    fun isClosed(): Boolean = !isOpen()


    var discord: String = "N/A"
    var paypal: String = "N/A"

    fun getRelationTo(faction: Faction): Relation = if (relations == null) Relation.NEUTRAL else (relations[faction.id]?.relation ?: Relation.NEUTRAL)

    fun getRelationalFactions(relation: Relation): Collection<Faction> {
        return if (relations == null) emptyList() else relations
                .filter { (_, rel) -> rel.relation === relation }
                .map { FactionManager.getFaction(it.key) }
    }

    fun getFormattedCreationDate(): String {
        return WordUtils.capitalizeFully(Config.dateCreationFormat.format(creationDate))
    }

    enum class BankLogType { WITHDRAW, DEPOSIT, PAY, COMMAND }

    data class BankLog(val uuid: UUID, val target: String?, val type: BankLogType, val amount: Double, val id: Int, val eventAt: Long = System.currentTimeMillis())
    data class Bank(var amount: Double, var nextId: Int, val logs: ArrayList<BankLog>) {

        fun command(fPlayer: FPlayer, delta: Double) {
            amount -= delta
            logs += BankLog(fPlayer.uuid, null, BankLogType.COMMAND, delta, nextId++)
        }

        fun pay(fPlayer: FPlayer, delta: Double, factionTo: Faction) {
            amount -= delta
            factionTo.bank.amount += delta
            logs += BankLog(fPlayer.uuid, factionTo.tag, BankLogType.PAY, delta, nextId++)
        }

        fun deposit(fPlayer: FPlayer, delta: Double) {
            amount += delta
            logs += BankLog(fPlayer.uuid, null, BankLogType.DEPOSIT, delta, nextId++)
        }

        fun widthdraw(fPlayer: FPlayer, delta: Double) {
            amount -= delta
            logs += BankLog(fPlayer.uuid, null, BankLogType.WITHDRAW, delta, nextId++)
        }


    }

    var bank: Bank = Bank(0.0, 1, ArrayList())

    fun getSortedBankLogPages(): Map<Int, Array<BankLog>> {
        val map = mutableMapOf<Int, Array<BankLog>>()

        val limit = EconConfig.bankLogLimitPerPage
        if (limit <= 0) return map

        var currentPage = 1
        var currentAmount = 0

        bank.logs.sortedByDescending(BankLog::eventAt).forEach { log ->
            if (currentAmount++ >= limit) {
                currentPage++
                currentAmount = 0
            }

            map.compute(currentPage) { _, array -> array?.plusElement(log) ?: arrayOf(log) }
        }

        return map
    }
}