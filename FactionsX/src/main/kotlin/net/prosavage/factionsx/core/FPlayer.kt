package net.prosavage.factionsx.core


import com.cryptomorin.xseries.XMaterial
import fr.mrmicky.fastboard.FastBoard
import fr.mrmicky.fastparticles.ParticleType
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import me.rayzr522.jsonmessage.JSONMessage
import net.prosavage.factionsx.FactionsX
import net.prosavage.factionsx.command.engine.ConfirmAction
import net.prosavage.factionsx.command.engine.ConfirmData
import net.prosavage.factionsx.event.FPlayerFlyToggleEvent
import net.prosavage.factionsx.event.FPlayerPreFlyCheckEvent
import net.prosavage.factionsx.hook.EssentialsHook
import net.prosavage.factionsx.hook.vault.VaultHook
import net.prosavage.factionsx.hook.vault.VaultHook.format
import net.prosavage.factionsx.listener.PlayerListener
import net.prosavage.factionsx.manager.FactionManager
import net.prosavage.factionsx.manager.GridManager
import net.prosavage.factionsx.manager.PlaceholderManager
import net.prosavage.factionsx.persist.Message
import net.prosavage.factionsx.persist.config.*
import net.prosavage.factionsx.persist.config.FlyConfig.smokeOffSetX
import net.prosavage.factionsx.persist.config.FlyConfig.smokeOffSetY
import net.prosavage.factionsx.persist.config.FlyConfig.smokeOffSetZ
import net.prosavage.factionsx.persist.config.FlyConfig.smokeParticleCount
import net.prosavage.factionsx.persist.data.FLocation
import net.prosavage.factionsx.persist.data.getFLocation
import net.prosavage.factionsx.util.*
import org.apache.commons.lang.WordUtils.capitalizeFully
import org.bukkit.*
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.util.*
import kotlin.collections.HashMap


data class FPlayer(val uuid: UUID, var name: String) {
    var alt = false
        internal set

    @Transient
    var isFalling = false
    var chatChannel = ChatChannel.PUBLIC

    var ignoredChatChannels = mutableSetOf<ChatChannel>()

    var enabledChunkMessage = true

    @Transient
    var internalBoard: FastBoard? = null
        get() {
            if (field == null || field?.isDeleted == true)
                field = FastBoard(this.getPlayer())
            return field
        }

    @Transient
    var scoreboardActive = false

    @Transient
    var namingClaim: FLocation? = null

    fun isNamingClaim(): Boolean {
        return namingClaim != null
    }

    fun nameClaim(name: String) {
        namingClaim?.name = name
    }

    @Transient
    var assigningClaimIcon: FLocation? = null

    fun isAssigningClaimIcon(): Boolean {
        return assigningClaimIcon != null
    }

    fun assignIcon(material: Material) {
        assigningClaimIcon?.icon = XMaterial.matchXMaterial(material)
    }

    @Transient
    var isAutoClaiming = false

    @Transient
    var isChatSpy = false

    @Transient
    var autoClaimTargetFaction: Faction? = null

    fun ignoreChannel(chatChannel: ChatChannel) {
        if (ignoredChatChannels == null) ignoredChatChannels = mutableSetOf()
        ignoredChatChannels.add(chatChannel)
    }

    fun isIgnoringChannel(chatChannel: ChatChannel): Boolean {
        return ignoredChatChannels != null && ignoredChatChannels.contains(chatChannel)
    }

    fun unignoreChannel(chatChannel: ChatChannel) {
        if (ignoredChatChannels != null) ignoredChatChannels.remove(chatChannel)
    }

    fun getFactionAt(): Faction {
        return GridManager.getFactionAt(lastLocation?.chunk ?: return FactionManager.getWilderness())
    }

    fun runFlyChecks(notify: Boolean, preCheckNotification: Boolean, ignoreFlyStatus: Boolean = false) {
        runFlyChecks(
            getPlayer()
                ?: return, getFactionAt(), notify, preCheckNotification, ignoreFlyStatus = ignoreFlyStatus
        )
    }

    fun runFlyChecks(
        player: Player,
        factionAt: Faction,
        preCheckNotification: Boolean,
        notify: Boolean,
        ignoreFlyStatus: Boolean = false
    ) {
        val faction = getFaction()

        val preEvent = FPlayerPreFlyCheckEvent(this, faction, factionAt)
        Bukkit.getPluginManager().callEvent(preEvent)
        if (preEvent.isCancelled) return

        // On purpose.
        @Suppress("NAME_SHADOWING") var notify = notify
        if (!FlyConfig.factionsFly) {
            if (preCheckNotification) message(FlyConfig.factionsFlyDisabled)
            return
        }

        if (!isInFactionsFlyAffectedGameMode()) {
            if (preCheckNotification) message(FlyConfig.factionsFlyIgnoredGamemode, player.gameMode.name)
            return
        }

        val worldIsBlacklisted = Config.worldsNoFlyHandling.contains(player.world.name)
        if ((Config.turnBlacklistWorldsToWhitelist && !worldIsBlacklisted) || (!Config.turnBlacklistWorldsToWhitelist && worldIsBlacklisted)) {
            if (preCheckNotification) message(FlyConfig.factionsFlyDisabledWorld)
            return
        }

        if (player.allowFlight && !this.hasFaction() && !inBypass) {
            setFly(false)
            return
        }

        if (player.hasPermission(Permission.FLY.getFullPermissionNode()).not()) {
            setFly(false)
            return
        }

        if (!noEnemiesNearby(FlyConfig.enemyNearByCheckDistance)) {
            if (preCheckNotification) message(FlyConfig.factionsFlyEnemyNearby)
            return
        }

        val flyStatus = (hasFaction()) && (factionAt == faction || hasFlySystemFactionBypassCheck(factionAt, player))
        if (!player.allowFlight && preCheckNotification && !flyStatus) {
            message(FlyConfig.factionsFlyNotHere)
            notify = false
        }

        if (ignoreFlyStatus || player.allowFlight != flyStatus) {
            if (!flyStatus && !isFFlying) return
            // Just in case.
            val fPlayerFlyToggleEvent = FPlayerFlyToggleEvent(this, faction, factionAt, flyStatus)
            Bukkit.getPluginManager().callEvent(fPlayerFlyToggleEvent)
            if (fPlayerFlyToggleEvent.isCancelled) return
            if (notify) sendFlyUpdateMessage(flyStatus)
            setFly(flyStatus)
        }

    }

    private fun hasFlySystemFactionBypassCheck(factionAt: Faction, player: Player): Boolean {
        return (factionAt.isWilderness() && player.hasPermission(Permission.FLY_WILDERNESS))
                || (factionAt.isWarzone() && player.hasPermission(Permission.FLY_WARZONE))
                || (factionAt.isSafezone() && player.hasPermission(Permission.FLY_SAFEZONE))
    }

    fun sendFlyUpdateMessage(flyStatus: Boolean) {
        this.message(
            FlyConfig.factionsFlyAutoEnableMessage,
            if (flyStatus) FlyConfig.factionsFlyAutoEnableMessageEnable
            else FlyConfig.factionsFlyAutoEnableMessageDisable
        )
    }


    fun canBuildAt(location: Location): Boolean {
        if (isOffline()) return false
        return PlayerListener.processBlockChangeInFactionLocation(
            GridManager.getFactionAt(location.chunk),
            getPlayer()!!,
            PlayerListener.BlockChangeAction.PLACE,
            location.block.type,
            false, location
        )
    }

    fun canBreakAt(location: Location): Boolean {
        if (isOffline()) return false
        return PlayerListener.processBlockChangeInFactionLocation(
            GridManager.getFactionAt(location.chunk),
            getPlayer()!!,
            PlayerListener.BlockChangeAction.BREAK,
            location.block.type,
            false, location
        )
    }

    fun getAmountOfMaterialInPlayerInv(material: Material): Int {
        val inv = getPlayer()?.inventory ?: return 0
        var amt = 0
        for (item in inv.contents) {
            if (item?.type == material) amt += item.amount
        }
        return amt
    }

    fun takeAmountOfMaterialFromPlayerInv(material: Material, amount: Int): Int {
        val player = getPlayer() ?: return 0
        var total = 0

        val inventory = player.inventory
        for (index in (inventory.size - 1) downTo 0) {
            if (total >= amount) {
                break
            }

            val item = inventory.getItem(index) ?: continue
            if (item.type != material) {
                continue
            }

            val nextAmount = item.amount
            if (total + nextAmount <= amount) {
                total += nextAmount
                inventory.setItem(index, null)
                continue
            }

            val toRemove = amount - total
            item.amount = nextAmount - toRemove
            total += toRemove
            inventory.setItem(index, item)
        }

        /*val failed = player.inventory.removeItem(ItemStack(material, amount))
        player.updateInventory()
        var total = 0
        for (fail in failed.values) {
            total += fail?.amount ?: continue
        }*/
        player.updateInventory()
        return total
    }

    fun addToInventory(material: Material, amount: Int): Int {
        val player = getPlayer() ?: return 0
        val failed = player.inventory.addItem(ItemStack(material, amount))
        player.updateInventory()
        var total = 0
        for (fail in failed.values) {
            total += fail?.amount ?: continue
        }
        return total
    }

    fun hasSpaceInInventory(): Boolean {
        return getPlayer()?.inventory?.firstEmpty() != -1
    }


    fun canDoPlayerAction(playerAction: PlayerAction): Boolean {
        return role.canDoPlayerAction(playerAction)
    }


    fun canDoSpecialAction(specialAction: SpecialAction): Boolean {
        return role.canDoSpecialAction(specialAction)
    }

    var isFFlying = false

    fun setFly(status: Boolean) {
        val player = getPlayer()
        player?.allowFlight = status
        player?.isFlying = status
        isFFlying = status
        val particleLocation = player?.location
        if (FlyConfig.showSmokeWhenFlyTurnsOff && !status && player != null) {
            if (isVanished()) {
                ParticleType.of("SMOKE_LARGE").spawn(
                    player,
                    particleLocation,
                    smokeParticleCount,
                    smokeOffSetX,
                    smokeOffSetY,
                    smokeOffSetZ,
                    0.0
                )
            } else {
                ParticleType.of("SMOKE_LARGE").spawn(
                    player.world,
                    particleLocation,
                    smokeParticleCount,
                    smokeOffSetX,
                    smokeOffSetY,
                    smokeOffSetZ,
                    0.0
                )
            }
        }

        if (!status) processFall(this)
    }

    fun isInFactionsFlyAffectedGameMode(): Boolean {
        val gameMode = getPlayer()?.gameMode ?: return false
        return gameMode == GameMode.SURVIVAL || gameMode == GameMode.ADVENTURE
    }

    fun showFlyParticle() {
        val player = getPlayer()
        showFlyParticle(player ?: return, player.location)
    }

    fun showFlyParticle(player: Player, location: Location) {
        if (FlyConfig.factionsFlyParticles && hasFaction() && player.isFlying && !player.isOnGround && !player.isDead && isFFlying) {
            var particleCount = FlyConfig.particleCount
            var particleOffSetX = FlyConfig.particleParticleOffSetX
            var particleOffSetY = FlyConfig.particleParticleOffSetY
            var particleOffSetZ = FlyConfig.particleParticleOffSetZ
            if (FlyConfig.changeParticlesWhenLookingDown && player.location.pitch > FlyConfig.changeParticleAtPitchHigherThan) {
                particleCount = FlyConfig.lookingDownParticleCount
                particleOffSetX = FlyConfig.lookingDownParticleOffSetX
                particleOffSetY = FlyConfig.lookingDownParticleOffSetY
                particleOffSetZ = FlyConfig.lookingDownParticleOffSetZ
            }
            val particleLocation = location.add(0.0, -0.5, 0.0)
            if (isVanished(player)) {
                ParticleType.of("CLOUD").spawn(
                    player, particleLocation,
                    particleCount, particleOffSetX,
                    particleOffSetY, particleOffSetZ, 0.0
                )
            } else {
                ParticleType.of("CLOUD").spawn(
                    player.world, particleLocation,
                    particleCount, particleOffSetX,
                    particleOffSetY, particleOffSetZ, 0.0
                )
            }
        }
    }


    fun runFlyEnemyNearByCheck(instance: FactionsX): Boolean {
        return runFlyEnemyNearByCheck(instance, getPlayer() ?: return false)
    }

    fun runFlyEnemyNearByCheck(instance: FactionsX, player: Player): Boolean {
        if (!inBypass && player.isFlying && isInFactionsFlyAffectedGameMode() && !noEnemiesNearby(FlyConfig.enemyNearByCheckDistance)) {
            message(FlyConfig.factionsFlyEnemyNearby)
            setFly(false)
            return true
        }
        return false
    }

    fun getEnemiesNearby(distanceToEnemy: Double): List<FPlayer> {
        val player = getPlayer() ?: return emptyList()
        if (!FlyConfig.enemyNearByCheck) return emptyList()
        val onlineEnemyPlayers = player.world.players.map { plyr -> plyr.getFPlayer() }
            .filter { fPlayer ->
                fPlayer.hasFaction()
                        && fPlayer.getFaction().getRelationTo(getFaction()) == Relation.ENEMY
            }

        val enemyList = arrayListOf<FPlayer>()
        for (onlineEnemyPlayer in onlineEnemyPlayers) {
            val location = player.location
            // Secondary due to teleports?
            if (location.world != onlineEnemyPlayer.lastLocation?.world) continue
            val distance = onlineEnemyPlayer.lastLocation?.distance(location)
            if (distance != null && distance < distanceToEnemy && onlineEnemyPlayer.getPlayer()!!.isDead.not()) {
                enemyList.add(onlineEnemyPlayer)
            }
        }
        return enemyList
    }

    fun noEnemiesNearby(distanceToEnemy: Double): Boolean {
        return getEnemiesNearby(distanceToEnemy).isEmpty()
    }

    private fun processFall(fPlayer: FPlayer) {
        fPlayer.isFalling = true
        Bukkit.getScheduler().runTaskLater(FactionsX.instance, Runnable {
            fPlayer.isFalling = false
        }, 200L)
    }

    fun isVanished(): Boolean {
        return isVanished(getPlayer() ?: return false)
    }

    fun isVanished(player: Player): Boolean {
        return (hasVanishedMetadata(player) || EssentialsHook.playerIsVanished(player))
    }


    fun isVanished(player: Player, target: Player): Boolean {
        return if (isVanished(target)) true else !player.canSee(target)
    }

    private fun hasVanishedMetadata(player: Player): Boolean {
        if (!player.hasMetadata("vanished")) {
            return false
        }
        for (meta in player.getMetadata("vanished")) {
            if (meta == null) continue
            if (meta.asBoolean()) {
                return true
            }
        }
        return false
    }


    var confirmAction = ConfirmData(false, ConfirmAction.NONE)

    var prefix = ""

    @Transient
    var mapToggle = false


    fun sendMap() {
        // They're offline if null.
        val player = getPlayer() ?: return
        val faction = getFaction()
        val here = getFLocation(player.location)
        val mapRowsToUse = MapConfig.mapRows / 2
        val mapWidthToUse = MapConfig.mapWidth / 2
        val start = FLocation(here.x - mapWidthToUse, here.z + mapRowsToUse, here.world)
        val end = FLocation(here.x + mapWidthToUse, here.z - mapRowsToUse, here.world)
        val bar = color(
            MapConfig.mapBarTitle.replace(
                "{DIRECTION}",
                capitalizeFully(player.getDirection().name.toLowerCase().replace("_", " "))
            )
        )
        player.sendMessage(bar)
        val xStart = if (start.x > end.x) end.x else start.x
        val zStart = if (start.z > end.z) end.z else start.z
        val xEnd = if (start.x < end.x) end.x else start.x
        val zEnd = if (start.z < end.z) end.z else start.z

        val fPlayer = this

        data class MapInfo(val tooltip: String, val charIndex: Int)
        GlobalScope.launch {
            val factionToolTips = hashMapOf<Faction, MapInfo>()
            var nextChar = 0
            val lines = mutableListOf<JSONMessage>()

            for (z in zStart..zEnd) {
                val line = JSONMessage.create()
                for (x in xStart..xEnd) {
                    val fLocation = FLocation(x, z, here.world)
                    if (fLocation.outsideBorder(Config.claimWorldBorderBuffer)) {
                        line.then(color(MapConfig.mapWorldBorderProtected))
                            .tooltip(color(MapConfig.mapWorldBorderProtectedTooltip))
                        continue
                    }
                    val facAt = fLocation.getFaction()
                    if (!factionToolTips.containsKey(facAt)) {

                        val jsonTooltip =
                            (if (facAt.isSystemFaction()) MapConfig.systemFactionMapToolTip else if (facAt == faction) MapConfig.ownFactionMapToolTip else MapConfig.mapTooltip)
                                .joinToString("\n") { tooltipLine ->
                                    color(
                                        PlaceholderManager.processPlaceholders(
                                            fPlayer,
                                            facAt,
                                            tooltipLine
                                        )
                                    )
                                }
                        factionToolTips[facAt] = MapInfo(jsonTooltip, nextChar)
                        nextChar++
                    }
                    if (x == here.x && z == here.z) line.then(color(MapConfig.mapYouAreHere))
                    else line.then(
                        color(
                            if (facAt.isWilderness()) "${Config.relationWildernessColor}${MapConfig.mapWildernessChar}"
                            else "${
                                PlaceholderManager.getRelationPrefix(
                                    faction,
                                    facAt
                                )
                            }${
                                MapConfig.mapChars[factionToolTips[facAt]?.charIndex
                                    ?: 0]
                            }"
                        )
                    )
                    line.tooltip(color(factionToolTips[facAt]?.tooltip ?: MapConfig.mapToolTipError))
                        .runCommand("/f ${if (facAt == faction) "unclaimat" else "claimat"} $x $z")
                }
                lines.add(line)
            }
            lines.forEach { line -> line.send(player) }
            val filteredTooltips = factionToolTips.keys.filterNot(Faction::isWilderness)
            if (filteredTooltips.isNotEmpty()) {
                message(filteredTooltips.joinToString(MapConfig.mapLegendDelimiter) { fac ->
                    PlaceholderManager.getRelationPrefix(faction, fac) +
                            "${MapConfig.mapChars[factionToolTips[fac]?.charIndex ?: 0]} ${fac.tag}"
                })
            } else {
                message(MapConfig.mapLegendNoFactions)
            }
        }
    }


    @Transient
    var lastLocation: Location? = null

    fun startConfirmProcess(
        instance: FactionsX,
        confirmAction: ConfirmAction
    ) {
        this.also {
            it.confirmAction.startConfirmation(confirmAction)
        }
            .also {
                Bukkit.getScheduler().runTaskLater(instance, Runnable {
                    if (it.confirmAction.action != ConfirmAction.NONE) it.message(
                        Message.genericActionHasTimedOut
                    )
                    it.confirmAction.clearConfirmation()
                }, (20 * Config.confirmTimeOutSeconds).toLong())
            }
    }

    fun isLeader(): Boolean {
        return !getFaction().isSystemFaction() && getFaction().getLeader() == this
    }

    var role = RoleConfig.defaultWildernessRole
        get() {
            val fac = getFaction()
            if (fac.isWilderness()) return field
            else return fac.factionRoles.roleHierarchy.values.find { role -> role.roleTag == field.roleTag } ?: run {
                if (isLeader()) return fac.factionRoles.getApexRole()
                println("Could not find role in faction roles, resetting to minimum role.")
                field = fac.factionRoles.getMinimumRole()
                return field
            }
        }

    var timeAtLastLogin = System.currentTimeMillis()
    var lastPowerUpdate = System.currentTimeMillis()

    var teleportToSpawnOnLogin = false

    var credits: Double = Config.creditSettings.startingCredits

    private var power: Double = Config.powerSettings.startingPower
    var powerBoost = 0.0
    var maxPowerBoost = 0.0


    fun setPower(power: Double) {
        this.power = power
    }

    fun getMaxPower(): Double {
        return Config.powerSettings.maxPlayerPower + this.maxPowerBoost + getFaction().maxPowerBoost
    }

    fun getMinPower(): Double {
        return Config.powerSettings.minPlayerPower
    }

    fun power(): Double {
        updatePower()
        return this.power + this.powerBoost
    }

    fun changePowerBy(delta: Double) {
        /*if (delta < 0) {
            val fPlayerPowerLossEvent = FPlayerPowerLossEvent(this, delta)
            Bukkit.getPluginManager().callEvent(fPlayerPowerLossEvent)
            // Makes power loss cancellable.
            if (fPlayerPowerLossEvent.isCancelled) return
        }*/
        this.power += delta
        val maxPlayerPower = getMaxPower()
        val minPlayerPower = getMinPower()
        when {
            this.power > maxPlayerPower -> {
                this.power = maxPlayerPower
            }
            this.power < minPlayerPower -> {
                this.power = minPlayerPower
            }
        }
    }

    fun handleOfflinePowerLoss() {
        if (isOnline()) return
        if (!(Config.powerSettings.powerOfflineLossPerDay > 0 && power > Config.powerSettings.powerOfflineLossLimit)) {
            return
        }

        val timeNow = System.currentTimeMillis()
        val timePassed = timeNow - lastPowerUpdate
        lastPowerUpdate = timeNow

        var loss: Double =
            timePassed * Config.powerSettings.powerOfflineLossPerDay / (24 * 60 * 60 * 1000) // the last expression is a day.
        if (power - loss < Config.powerSettings.powerOfflineLossLimit) {
            loss = power
        }
        changePowerBy(-loss)
    }

    fun actionbar(message: String, vararg args: String) {
        actionbar(String.format(message, *args))
    }

    fun actionbar(message: String) {
        JSONMessage.actionbar(color(message), getPlayer())
    }

    fun message(message: String, noPrefix: Boolean = false) {
        if (message.isEmpty()) return
        getPlayer()?.sendMessage(color((if (noPrefix) "" else Message.messagePrefix) + message))
    }

    fun message(message: String, vararg args: String) {
        if (message.isEmpty()) return
        message(String.format(message, *args))
    }

    fun messageWithTooltipAndCommand(message: String, vararg messageArgs: String, tooltip: String, command: String) {
        JSONMessage.create(color(String.format(message, *messageArgs))).tooltip(color(tooltip)).runCommand(command)
            .send(getPlayer())
    }

    fun getPlayer(): Player? {
        return Bukkit.getPlayer(uuid)
    }

    fun takeMoneyFromFactionBank(amount: Double, type: Faction.BankLogType): TransactionResponse {
        if (type == Faction.BankLogType.PAY) {
            throw IllegalArgumentException("cannot use BankLogType.PAY in this function.")
        }
        var amountToTake = amount;
        if (!EconConfig.economyEnabled) return TransactionResponse.econDisabledResponse(amount)
        val factionBank = getFaction().bank
        var total = factionBank.amount
        if (EconConfig.useVaultDirectlyIfNoMoneyInBank) {
            total += VaultHook.getBalance(this)
        }
        if (amountToTake > total) {
            return TransactionResponse.notEnoughResponse(amountToTake, total)
        }

        if (EconConfig.useVaultDirectlyIfNoMoneyInBank) {
            // here we have overflow that we need to account for.
            val overflow = amountToTake - factionBank.amount
            if (overflow > 0) {
                // take overflow from player
                val response = takeMoney(overflow, notifySuccess = false)
                if (!response) {
                    return TransactionResponse.notEnoughResponse(amountToTake, total)
                }
                amountToTake -= overflow
                // since overflow is not accounted for lkike this, we can just continue.
            }

        }

        // log
        when (type) {
            Faction.BankLogType.WITHDRAW -> factionBank.widthdraw(this, amountToTake)
            Faction.BankLogType.DEPOSIT -> factionBank.deposit(this, amountToTake)
            Faction.BankLogType.COMMAND -> factionBank.command(this, amountToTake)
            else -> {}
        }

        return TransactionResponse(amount, factionBank.amount, TransactionResponse.ResponseType.SUCCESS, "")
    }


    fun takeMoney(amount: Double, notifySuccess: Boolean = true): Boolean = VaultHook.takeFrom(this, amount).let {
        if (it == null) {
            message(Message.genericNoEconomyProvider)
            return false
        }

        if (!it.transactionSuccess()) {
            message(Message.genericTransactionTakeError, it.errorMessage ?: "not enough money")
            return false
        }

        if (notifySuccess) message(Message.genericTransactionSuccessTake, format(amount), format(VaultHook.getBalance(this)))
        true
    }

    fun takeCredits(amount: Double): Boolean {
        if (credits < amount) {
            message(Message.genericTransactionTakeError, "not enough credits in balance.")
            return false
        }

        credits -= amount
        message(Message.genericTransactionSuccessTake, format(amount), format(credits))
        return true
    }

    fun getOfflinePlayer(): OfflinePlayer {
        return Bukkit.getOfflinePlayer(uuid)
    }

    fun isOnline(): Boolean {
        return getPlayer() != null
    }

    fun isOffline(): Boolean {
        return !isOnline()
    }

    fun updatePower(firstLogin: Boolean = false) {
        handleOfflinePowerLoss()
        if (!Config.powerSettings.powerRegenOffline && (isOffline() || firstLogin)) return
        val now = System.currentTimeMillis()
        val timePassedBetweenUpdate: Long = now - this.lastPowerUpdate
        this.lastPowerUpdate = now
        // Prevents power loss while dead ( not clicking respawn )
        if (isOnline() && getPlayer()!!.isDead) return
        val milisInMinute = 60 * 1000
        changePowerBy((timePassedBetweenUpdate * Config.powerSettings.powerRegenPerMinute / milisInMinute))
    }

    fun handleDeath() {
        updatePower()
        changePowerBy(-Config.powerSettings.powerPerDeath)
    }

    var currentFactionID = -1L

    @Transient
    var inBypass = false
        set(value) {
            if (isOnline()) message(
                Message.commandFactionsBypassUpdate,
                if (value) Message.commandFactionsBypassTrue else Message.commandFactionsBypassFalse
            )
            field = value
        }

    var factionsInvitedTo = HashMap<Long, Boolean>()

    fun isInvitedToFaction(faction: Faction): Boolean {
        return factionsInvitedTo != null && factionsInvitedTo.contains(faction.id)
    }

    fun inviteToFaction(faction: Faction, isAlt: Boolean = false) {
        if (factionsInvitedTo == null) factionsInvitedTo = hashMapOf()
        factionsInvitedTo[faction.id] = isAlt
    }

    fun deInviteFromFaction(faction: Faction) {
        if (factionsInvitedTo == null) factionsInvitedTo = hashMapOf()
        factionsInvitedTo.remove(faction.id)
    }


    fun hasFaction(): Boolean {
        return currentFactionID != -1L
    }

    fun getFaction(): Faction {
        return FactionManager.getFaction(currentFactionID)
    }

    fun assignFaction(faction: Faction, role: CustomRole) {
        this.role = role
        currentFactionID = faction.id
    }

    fun unassignFaction(isAlt: Boolean = false) {
        prefix = ""
        role = RoleConfig.defaultWildernessRole
        currentFactionID = -1
        if (isAlt) alt = false
    }

    fun canDoMemberAction(memberAction: MemberAction): Boolean {
        return !getFaction().isWilderness() && role.canDoMemberAction(memberAction)
    }


    var showChunkBorders = false
    var chunkBorderColor: Config.ColorData? = null

    fun showChunkBorder(player: Player, location: Location) {
        if (!showChunkBorders) return
        val cornerX = location.chunk.x.times(16)
        val cornerZ = location.chunk.z.times(16)

        for (y in (location.y - 5).toInt() until (location.y + 5).toInt()) {
            for (x in cornerX until cornerX + 16 step 2) {
                ParticleType.of("REDSTONE").spawn(
                    player,
                    Location(location.world, x.toDouble(), y.toDouble(), cornerZ.toDouble()),
                    1,
                    chunkBorderColor?.toColor()
                )
                ParticleType.of("REDSTONE").spawn(
                    player,
                    Location(location.world, x.toDouble(), y.toDouble(), cornerZ.toDouble() + 16),
                    1,
                    chunkBorderColor?.toColor()
                )
            }

            for (z in cornerZ until cornerZ + 16 step 2) {
                ParticleType.of("REDSTONE").spawn(
                    player,
                    Location(location.world, cornerX.toDouble(), y.toDouble(), z.toDouble()),
                    1,
                    chunkBorderColor?.toColor()
                )
                ParticleType.of("REDSTONE").spawn(
                    player,
                    Location(location.world, cornerX.toDouble() + 16, y.toDouble(), z.toDouble()),
                    1,
                    chunkBorderColor?.toColor()
                )
            }
        }


    }


}