package net.prosavage.factionsx.persist

import net.prosavage.factionsx.addonframework.AddonPlugin
import net.prosavage.factionsx.core.CustomRole
import net.prosavage.factionsx.core.FPlayer
import net.prosavage.factionsx.core.Faction
import net.prosavage.factionsx.manager.PlayerManager
import java.io.File
import java.util.*
import kotlin.collections.HashMap
import kotlin.collections.HashSet

object RosterData {

    @Transient
    private val instance = this

    var rosters = HashMap<Long, Roster>()
        get() {
            // gson is serializes empty collections to null.
            if (field == null) {
                field = HashMap()
            }
            return field
        }

    fun save(addon: AddonPlugin) {
        addon.configSerializer.save(instance, File(addon.dataFolder, "data.json"))
    }

    fun load(addon: AddonPlugin) {
        addon.configSerializer.load(instance, RosterData::class.java, File(addon.dataFolder, "data.json"))
    }
}


data class Roster(val id: Long, val rosterMembers: HashSet<RosterMember>) {

    fun canAddMember(): Boolean {
        return rosterMembers.size < RosterConfig.maxRosterMembers
    }

    fun hasMember(member: FPlayer): Boolean {
        return getMember(member) != null
    }

    fun getMember(member: FPlayer): RosterMember? {
        return rosterMembers.find { rosterMember -> rosterMember.uuid == member.uuid }
    }

    fun addMember(member: FPlayer, role: CustomRole) {
        if (!canAddMember()) return
        rosterMembers.add(RosterMember(member.uuid, role.roleTag))
    }

    fun removeMember(member: FPlayer) {
        val rosterMember = rosterMembers.find { rosterMember -> rosterMember.uuid == member.uuid }
        rosterMembers.remove(rosterMember)
    }

    fun getMembers(): HashSet<RosterMember> {
        return rosterMembers
    }

}

// role tags dont change once a faction is made.
data class RosterMember(val uuid: UUID, val roleTag: String) {
    fun getFPlayer(): FPlayer {
        return PlayerManager.getFPlayer(uuid)!!
    }

    fun getRole(faction: Faction): CustomRole? {
        return faction.factionRoles.roleHierarchy.values.find { role -> role.roleTag == roleTag }
    }
}


fun Faction.getRoster(): Roster {
    return RosterData.rosters.getOrPut(id, { Roster(id, HashSet()) })
}