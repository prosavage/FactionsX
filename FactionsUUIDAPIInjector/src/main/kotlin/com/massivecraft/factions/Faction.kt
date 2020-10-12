package com.massivecraft.factions

import net.prosavage.factionsx.core.FPlayer
import net.prosavage.factionsx.persist.data.FLocation
import net.prosavage.factionsx.persist.data.wrappers.DataLocation
import org.bukkit.Location
import javax.management.relation.Role
import kotlin.math.acos
import kotlin.math.roundToInt


class Faction(val faction: net.prosavage.factionsx.core.Faction) {
    fun getAnnouncements(): Map<String?, List<String?>?>? {
        // we dont have announcements :)
        return emptyMap()
    }



    fun getId(): String? {
        return faction.id.toString()
    }

    fun invite(fplayer: com.massivecraft.factions.FPlayer?) {
        fplayer?.fplayer?.let { faction.inviteMember(it) }
    }

    fun deinvite(fplayer: com.massivecraft.factions.FPlayer?) {
        fplayer?.fplayer?.deInviteFromFaction(faction)
    }

    fun isInvited(fplayer: com.massivecraft.factions.FPlayer?): Boolean {
        return fplayer?.fplayer?.factionsInvitedTo?.contains(faction.id) ?: false
    }

    fun getOpen(): Boolean {
        return faction.isOpen()
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

    fun getTag(otherFplayer: com.massivecraft.factions.FPlayer?): String? {
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

    fun addFPlayer(fplayer: com.massivecraft.factions.FPlayer?): Boolean {
        faction.addMember(fplayer?.fplayer)
    }

    fun removeFPlayer(fplayer: FPlayer?): Boolean

    fun getSize(): Int

    fun getFPlayers(): Set<FPlayer?>?

    fun getFPlayersWhereOnline(online: Boolean): Set<FPlayer?>?

    fun getFPlayersWhereOnline(online: Boolean, viewer: FPlayer?): Set<FPlayer?>?

    fun getFPlayerAdmin(): FPlayer?

    fun getFPlayersWhereRole(role: Role?): List<FPlayer?>?

    fun getOnlinePlayers(): List<Player?>?

    // slightly faster check than getOnlinePlayers() if you just want to see if
    // there are any players online
    fun hasPlayersOnline(): Boolean

    fun memberLoggedOff()

    // used when current leader is about to be removed from the faction;
    // promotes new leader, or disbands faction if no other members left
    fun promoteNewLeader()

    fun getDefaultRole(): Role?

    fun setDefaultRole(role: Role?)

    fun sendMessage(message: String?)

    fun sendMessage(messages: List<String?>?)

    // ----------------------------------------------//
    // Ownership of specific claims
    // ----------------------------------------------//

    // ----------------------------------------------//
    // Ownership of specific claims
    // ----------------------------------------------//
    fun getClaimOwnership(): Map<FLocation?, Set<String?>?>?

    fun clearAllClaimOwnership()

    fun clearClaimOwnership(loc: FLocation?)

    fun clearClaimOwnership(player: FPlayer?)

    fun getCountOfClaimsWithOwners(): Int

    fun doesLocationHaveOwnersSet(loc: FLocation?): Boolean

    fun isPlayerInOwnerList(player: FPlayer?, loc: FLocation?): Boolean

    fun setPlayerAsOwner(player: FPlayer?, loc: FLocation?)

    fun removePlayerAsOwner(player: FPlayer?, loc: FLocation?)

    fun getOwnerList(loc: FLocation?): Set<String?>?

    fun getOwnerListString(loc: FLocation?): String?

    fun playerHasOwnershipRights(fplayer: FPlayer?, loc: FLocation?): Boolean

    // ----------------------------------------------//
    // Persistance and entity management
    // ----------------------------------------------//
    fun remove()

    fun getAllClaims(): Set<FLocation?>?

    fun setId(id: String?)

    fun getOfflinePlayer(): OfflinePlayer?

}