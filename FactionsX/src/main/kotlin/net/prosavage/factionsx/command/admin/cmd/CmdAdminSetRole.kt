package net.prosavage.factionsx.command.admin.cmd

import net.prosavage.factionsx.command.engine.CommandInfo
import net.prosavage.factionsx.command.engine.CommandRequirementsBuilder
import net.prosavage.factionsx.command.engine.FCommand
import net.prosavage.factionsx.core.Permission
import net.prosavage.factionsx.persist.Message

class CmdAdminSetRole : FCommand() {
    init {
        aliases.add("setrole")
        aliases.add("set-role")

        requiredArgs.add(Argument("player", 0, PlayerArgument()))
        requiredArgs.add(Argument("faction-role", 1, StringArgument()))

        commandRequirements = CommandRequirementsBuilder()
                .withPermission(Permission.ADMIN_SETROLE)
                .build()
    }

    override fun execute(info: CommandInfo): Boolean {
        val target = info.getArgAsFPlayer(0, cannotReferenceYourSelf = false, offline = true) ?: return false
        val targetFaction = target.getFaction()

        if (targetFaction.isWilderness()) {
            info.message(Message.commandAdminSetRoleFactionRequired, target.name)
            return false
        }

        val factionRoles = targetFaction.factionRoles

        val role = factionRoles.getRoleFromString(info.args.getOrNull(1)) ?: run {
            info.message(Message.commandAdminSetRoleInvalidRole)
            info.message(factionRoles.roleHierarchy.values.joinToString(", ") { role -> role.roleTag })
            return false
        }

        if (target.isLeader() && targetFaction.getMembers().size == 1) {
            info.message(Message.commandAdminLeaderOnly)
            return false
        }

        if (factionRoles.getApexRole() == role) {
            val leader = targetFaction.getLeader()
            // Non a system faction, so we can assert leader as nonnull.
            leader?.role = factionRoles.getDemotionRole(leader!!)
            targetFaction.setLeader(target)
            leader.message(Message.commandAdminSetRoleLeaderNotify)
        }

        target.role = role
        target.message(Message.commandAdminSetRoleNotif, role.roleTag)
        info.message(Message.commandAdminSetRoleSuccess, target.name, role.roleTag)
        return true
    }

    override fun getHelpInfo(): String {
        return Message.commandAdminSetRoleHelp
    }
}