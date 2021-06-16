package net.prosavage.factionsx.core

import com.cryptomorin.xseries.XMaterial
import net.prosavage.factionsx.util.MemberAction
import net.prosavage.factionsx.util.PlayerAction
import net.prosavage.factionsx.util.SpecialAction


class CustomRole(var chatTag: String,
                 var roleTag: String,
                 var allowedPlayerActions: MutableList<PlayerAction>,
                 var allowedMemberActions: MutableList<MemberAction>,
                 var specialActions: MutableMap<String, Boolean>,
                 var iconMaterial: XMaterial) {

    fun canDoMemberAction(memberAction: MemberAction): Boolean {
        if (allowedMemberActions == null) allowedMemberActions = mutableListOf()
        return allowedMemberActions.contains(memberAction)
    }

    fun canDoPlayerAction(playerAction: PlayerAction): Boolean {
        if (allowedPlayerActions == null) allowedPlayerActions = mutableListOf()
        return allowedPlayerActions.contains(playerAction)
    }

    fun canDoSpecialAction(specialAction: SpecialAction): Boolean {
        if (specialActions == null) specialActions = mutableMapOf()
        return specialActions.getOrPut(specialAction.name) { false }
    }

}


class FactionRoles(var roleHierarchy: HashMap<Int, CustomRole>) {

    fun getRoleFromString(name: String?): CustomRole? {
        if (name == null) return null
        return roleHierarchy.values.find { role -> role.roleTag.toLowerCase() == name.toLowerCase() }
    }

    fun addRole(role: CustomRole) {
        val newRoleMap = hashMapOf<Int, CustomRole>()
        roleHierarchy.keys.forEach {
            val currentValue = roleHierarchy[it]!!
            newRoleMap[it + 1] = currentValue
        }
        newRoleMap[0] = role
        roleHierarchy = newRoleMap
    }

    fun removeRole(role: CustomRole) {
        roleHierarchy.values.remove(role)
        // organize the remaining roles to prevent issues with indexes
        this.organize()
    }


    fun getAllRoles(): List<CustomRole> {
        return roleHierarchy.values.toList()
    }

    fun replaceRoles(role: CustomRole, roleToReplace: CustomRole) {
        val indexOfFirst =
                roleHierarchy.entries.find { it.value == role }!!
        val indexOfReplaced =
                roleHierarchy.entries.find { it.value == roleToReplace }!!

        roleHierarchy[indexOfFirst.key] = roleToReplace
        roleHierarchy[indexOfReplaced.key] = role
    }


    fun hasHigherRank(first: FPlayer, second: FPlayer): Boolean {
        return isHigherRole(first = first.role, second = second.role)
    }

    fun isHigherRole(first: CustomRole, second: CustomRole): Boolean {
        val firstRole = roleHierarchy.filterValues { customRole -> customRole == first }.keys.firstOrNull()
                ?: return false
        val fplayerRole = roleHierarchy.filterValues { customRole -> customRole == second }.keys.firstOrNull()
                ?: return false
        return firstRole > fplayerRole
    }


    fun getPromotionRole(recipient: FPlayer): CustomRole {
        if (recipient.role == this.getApexRole()) return recipient.role
        val currentRoleIndex = roleHierarchy.filterValues { customRole -> customRole == recipient.role }.keys.firstOrNull()
                ?: return recipient.role
        return roleHierarchy[currentRoleIndex + 1] ?: return recipient.role
    }


    fun getDemotionRole(recipient: FPlayer): CustomRole {
        if (recipient.role == this.getMinimumRole()) return this.getMinimumRole()
        val currentRoleIndex = roleHierarchy.filterValues { customRole -> customRole == recipient.role }.keys.firstOrNull()
                ?: return recipient.role
        return roleHierarchy[currentRoleIndex - 1] ?: return this.getMinimumRole()
    }


    fun getMaximumRole(): CustomRole {
        return this.getApexRole()
    }

    fun getApexRole(): CustomRole {
        return roleHierarchy.getValue(roleHierarchy.keys.max()!!)
    }

    fun getMinimumRole(): CustomRole {
        return roleHierarchy.getValue(roleHierarchy.keys.min()!!)
    }

    private fun organize() {
        val previous = roleHierarchy.toSortedMap(compareBy { it })
        roleHierarchy.clear()

        var nextRank = 0
        previous.mapKeysTo(roleHierarchy) { nextRank++ }
    }
}