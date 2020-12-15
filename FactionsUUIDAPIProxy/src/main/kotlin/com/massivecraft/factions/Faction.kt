package com.massivecraft.factions

import com.massivecraft.factions.proxy.ProxyFPlayer
import net.prosavage.factionsx.persist.data.wrappers.DataLocation
import org.bukkit.Location
import java.util.*
import kotlin.math.roundToInt


class Faction(val faction: net.prosavage.factionsx.core.Faction) {
    fun getAnnouncements(): Map<String?, List<String?>?>? {
        // we dont have announcements :)
        return emptyMap()
    }


    fun getId(): String? {
        return faction.id.toString()
    }

    fun invite(fplayer: FPlayer?) {
        fplayer?.fplayer?.let { faction.inviteMember(it) }
    }

    fun deinvite(fplayer: FPlayer?) {
        fplayer?.fplayer?.deInviteFromFaction(faction)
    }

    fun isInvited(fplayer: FPlayer?): Boolean {
        return fplayer?.fplayer?.factionsInvitedTo?.contains(faction.id) ?: false
    }

    fun getOpen(): Boolean {
        return faction.isClosed()
    }

    fun setOpen(isOpen: Boolean) {
        faction.openStatus = isOpen
    }

    fun getTag(): String? {
        return faction.tag
    }

    fun getTag(prefix: String?): String? {
        return "$prefix ${faction.tag}"
    }

    fun getTag(otherFaction: Faction?): String? {
        return otherFaction?.getTag()
    }

    fun getTag(otherFplayer: FPlayer?): String? {
        return otherFplayer?.getTag()
    }

    fun setTag(str: String?) {
        if (str != null) {
            faction.tag = str
        }
    }

    fun getComparisonTag(): String? {
        return faction.tag
    }

    fun getDescription(): String? {
        return faction.description
    }

    fun setDescription(value: String?) {
        if (value != null) {
            faction.description = value
        }
    }

    fun setHome(home: Location?) {
        if (home != null) {
            faction.home = DataLocation(home.world!!.name, home.x, home.y, home.z)
        }
    }

    fun isNormal(): Boolean {
        return !faction.isSystemFaction()
    }

    fun isWilderness(): Boolean {
        return faction.isWilderness()
    }

    fun isSafeZone(): Boolean {
        return faction.isSafezone()
    }

    fun isWarZone(): Boolean {
        return faction.isWarzone()
    }

    fun getPower(): Double {
        return faction.getPower()
    }

    fun getPowerMax(): Double {
        return faction.getMaxPower()
    }

    fun getPowerRounded(): Int {
        return faction.getPower().roundToInt()
    }

    fun getPowerMaxRounded(): Int {
        return faction.getMaxPower().roundToInt()
    }

    fun addFPlayer(fplayer: FPlayer?): Boolean {
        fplayer?.fplayer?.let {
            faction.addMember(it, faction.factionRoles.getMinimumRole())
            return true
        }
        return false
    }

    fun removeFPlayer(fplayer: FPlayer?): Boolean {
        fplayer?.fplayer?.let {
            faction.removeMember(it)
            return true
        }
        return false
    }

    fun getSize(): Int {
        return faction.getMaxMembers()
    }

    fun getFPlayers(): Set<FPlayer?>? {
        return faction.getMembers().map { member -> ProxyFPlayer(member) }.toSet()
    }

    fun getFPlayersWhereOnline(online: Boolean): Set<FPlayer?>? {
        return faction.getOnlineMembers().map { ProxyFPlayer(it) }.toSet()
    }

    fun getFPlayersWhereOnline(online: Boolean, viewer: FPlayer?): Set<FPlayer?>? {
        return getFPlayersWhereOnline(online)
    }

    fun getFPlayerAdmin(): FPlayer? {
        return faction.getLeader()?.let { ProxyFPlayer(it) }
    }

    fun sendMessage(message: String?) {
        if (message != null) {
            faction.message(message)
        }
    }

    fun sendMessage(messages: List<String?>?) {
        messages?.forEach {
            if (it != null) {
                faction.message(it)
            }
        }
    }

    // ----------------------------------------------//
    // Ownership of specific claims
    // ----------------------------------------------//

    // ----------------------------------------------//
    // Ownership of specific claims
    // ----------------------------------------------//

    fun setId(id: String?) {
        faction.ownerId = UUID.fromString(id)
    }


}