package com.massivecraft.factions.proxy

import com.massivecraft.factions.FPlayer
import com.massivecraft.factions.Faction
import net.prosavage.factionsx.util.Relation
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import kotlin.math.roundToInt

class ProxyFPlayer(override val fplayer: net.prosavage.factionsx.core.FPlayer) : FPlayer {
    // We dont need to do anything here.
    override fun login() {

    }

    // We dont need to do anything here.
    override fun logout() {

    }

    override fun getFaction(): Faction? {
        return Faction(fplayer.getFaction())
    }

    override fun getFactionId(): String? {
        return Faction(fplayer.getFaction()).getId()
    }

    override fun hasFaction(): Boolean {
        return fplayer.hasFaction()
    }

    override fun setFaction(faction: Faction?) {
        faction?.faction?.addMember(fplayer, faction.faction.factionRoles.getMinimumRole())
    }

    override fun isVanished(): Boolean {
        return fplayer.isVanished()
    }

    override fun isAdminBypassing(): Boolean {
        return fplayer.inBypass
    }

    override fun setIsAdminBypassing(`val`: Boolean) {
        fplayer.inBypass = `val`
    }

    override fun getLastLoginTime(): Long {
        return fplayer.timeAtLastLogin
    }

    override fun setLastLoginTime(lastLoginTime: Long) {
        fplayer.timeAtLastLogin = lastLoginTime
    }

    override fun isMapAutoUpdating(): Boolean {
        return fplayer.mapToggle
    }

    override fun setMapAutoUpdating(mapAutoUpdating: Boolean) {
        fplayer.mapToggle = mapAutoUpdating
    }

    override fun getTitle(): String? {
        return fplayer.prefix
    }

    override fun setTitle(sender: CommandSender?, title: String?) {
        fplayer.prefix = title ?: ""
    }

    override fun getName(): String? {
        return fplayer.name
    }

    override fun getTag(): String? {
        return Faction(fplayer.getFaction()).getTag()
    }

    override fun getChatTag(): String? {
        return fplayer.prefix
    }

    override fun getPower(): Double {
        return fplayer.power()
    }

    override fun alterPower(delta: Double) {
        fplayer.changePowerBy(delta)
    }

    override fun getPowerMax(): Double {
        return fplayer.getMaxPower()
    }

    override fun getPowerMin(): Double {
        return fplayer.getMinPower()
    }

    override fun getPowerRounded(): Int {
        return fplayer.power().roundToInt()
    }

    override fun getPowerMaxRounded(): Int {
        return fplayer.getMaxPower().roundToInt()
    }

    override fun getPowerMinRounded(): Int {
        return fplayer.getMinPower().roundToInt()
    }

    override fun updatePower() {
        fplayer.updatePower()
    }

    override fun isInOwnTerritory(): Boolean {
        return fplayer.getFaction() == fplayer.getFactionAt()
    }

    override fun isInOthersTerritory(): Boolean {
        return !isInOwnTerritory()
    }

    override fun isInAllyTerritory(): Boolean {
        return fplayer.getFaction().getRelationTo(fplayer.getFactionAt()) == Relation.ALLY
    }

    override fun isInNeutralTerritory(): Boolean {
        return fplayer.getFaction().getRelationTo(fplayer.getFactionAt()) == Relation.NEUTRAL
    }

    override fun isInEnemyTerritory(): Boolean {
        return fplayer.getFaction().getRelationTo(fplayer.getFactionAt()) == Relation.ENEMY
    }

    override fun leave(makePay: Boolean) {
        fplayer.getFaction().removeMember(fplayer)
    }

    override fun getId(): String? {
        return fplayer.uuid.toString()
    }

    override fun getPlayer(): Player? {
        return fplayer.getPlayer()
    }

    override fun isOnline(): Boolean {
        return fplayer.isOnline()
    }

    override fun sendMessage(message: String?) {
        if (message != null) {
            fplayer.message(message)
        }
    }

    override fun sendMessage(messages: List<String?>?) {
        messages?.forEach { message ->
            if (message != null) {
                fplayer.message(message)
            }
        }
    }

    override fun getMapHeight(): Int {
        return 5

    }


    override fun isOffline(): Boolean {
        return isOnline().not()
    }

    override fun isFlying(): Boolean {
        return fplayer.isFFlying
    }

    override fun setFlying(fly: Boolean) {
        fplayer.setFly(fly)
    }

    override fun setFlying(fly: Boolean, damage: Boolean) {
        fplayer.setFly(fly)
    }

    override fun isSeeingChunk(): Boolean {
        return fplayer.showChunkBorders
    }

    override fun setSeeingChunk(seeingChunk: Boolean) {
        fplayer.showChunkBorders = seeingChunk
    }


}