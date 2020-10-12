package com.massivecraft.factions

import net.prosavage.factionsx.manager.GridManager
import net.prosavage.factionsx.persist.data.FLocation
import net.prosavage.factionsx.util.Relation
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.util.*

import javax.management.relation.Role
import kotlin.math.roundToInt


class FPlayer(val fplayer: net.prosavage.factionsx.core.FPlayer) {
    // We dont need to do anything here.
    fun login() {

    }

    // We dont need to do anything here.
    fun logout() {

    }

    fun getFaction(): Faction? {
        return Faction(fplayer.getFaction())
    }

    fun getFactionId(): String? {
        return Faction(fplayer.getFaction()).getId()
    }

    fun hasFaction(): Boolean {
        return fplayer.hasFaction()
    }

    fun setFaction(faction: Faction?) {
        faction?.faction?.addMember(fplayer, faction.faction.factionRoles.getMinimumRole())
    }

    fun isVanished(): Boolean {
        return fplayer.isVanished()
    }

    fun isAdminBypassing(): Boolean {
        return fplayer.inBypass
    }

    fun setIsAdminBypassing(`val`: Boolean) {
        fplayer.inBypass = `val`
    }

    fun getLastLoginTime(): Long {
        return fplayer.timeAtLastLogin
    }

    fun setLastLoginTime(lastLoginTime: Long) {
        fplayer.timeAtLastLogin = lastLoginTime
    }

    fun isMapAutoUpdating(): Boolean {
        return fplayer.mapToggle
    }

    fun setMapAutoUpdating(mapAutoUpdating: Boolean) {
        fplayer.mapToggle = mapAutoUpdating
    }

    fun getTitle(): String? {
        return fplayer.prefix
    }

    fun setTitle(sender: CommandSender?, title: String?) {
        fplayer.prefix = title ?: ""
    }

    fun getName(): String? {
        return fplayer.name
    }

    fun getTag(): String? {
        return Faction(fplayer.getFaction()).getTag()
    }

    fun getChatTag(): String? {
        return fplayer.prefix
    }

    fun getPower(): Double {
        return fplayer.power()
    }

    fun alterPower(delta: Double) {
        fplayer.changePowerBy(delta)
    }

    fun getPowerMax(): Double {
        return fplayer.getMaxPower()
    }

    fun getPowerMin(): Double {
        return fplayer.getMinPower()
    }

    fun getPowerRounded(): Int {
        return fplayer.power().roundToInt()
    }

    fun getPowerMaxRounded(): Int {
        return fplayer.getMaxPower().roundToInt()
    }

    fun getPowerMinRounded(): Int {
        return fplayer.getMinPower().roundToInt()
    }

    fun updatePower() {
        fplayer.updatePower()
    }

    fun isInOwnTerritory(): Boolean {
        return fplayer.getFaction() == fplayer.getFactionAt()
    }

    fun isInOthersTerritory(): Boolean {
        return !isInOwnTerritory()
    }

    fun isInAllyTerritory(): Boolean {
        return fplayer.getFaction().getRelationTo(fplayer.getFactionAt()) == Relation.ALLY
    }

    fun isInNeutralTerritory(): Boolean {
        return fplayer.getFaction().getRelationTo(fplayer.getFactionAt()) == Relation.NEUTRAL
    }

    fun isInEnemyTerritory(): Boolean {
        return fplayer.getFaction().getRelationTo(fplayer.getFactionAt()) == Relation.ENEMY
    }

    fun leave(makePay: Boolean) {
        fplayer.getFaction().removeMember(fplayer)
    }

    fun getId(): String? {
        return fplayer.uuid.toString()
    }

    fun getPlayer(): Player? {
        return fplayer.getPlayer()
    }

    fun isOnline(): Boolean {
        return fplayer.isOnline()
    }

    fun sendMessage(message: String?) {
        if (message != null) {
            fplayer.message(message)
        }
    }

    fun sendMessage(messages: List<String?>?) {
        messages?.forEach { message ->
            if (message != null) {
                fplayer.message(message)
            }
        }
    }

    fun getMapHeight(): Int {
        return 5

    }


    fun isOffline(): Boolean {
        return isOnline().not()
    }

    fun isFlying(): Boolean {
        return fplayer.isFFlying
    }

    fun setFlying(fly: Boolean) {
        fplayer.setFly(fly)
    }

    fun setFlying(fly: Boolean, damage: Boolean) {
        fplayer.setFly(fly)
    }

    fun isSeeingChunk(): Boolean {
        return fplayer.showChunkBorders
    }

    fun setSeeingChunk(seeingChunk: Boolean) {
        fplayer.showChunkBorders = seeingChunk
    }


}