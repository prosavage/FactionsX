package net.prosavage.factionsx.command.factions.cmd.roles

import net.prosavage.factionsx.command.engine.CommandInfo
import net.prosavage.factionsx.command.engine.CommandRequirementsBuilder
import net.prosavage.factionsx.command.engine.FCommand
import net.prosavage.factionsx.persist.Message

class CmdRolesMove : FCommand() {

    init {
        aliases.add("move")

        requiredArgs.add(Argument("role", 0, RolesArgument()))
        requiredArgs.add(Argument("role-to-replace", 1, RolesArgument()))
        optionalArgs.add(Argument("show-list", 2, BooleanArgument()))

        commandRequirements = CommandRequirementsBuilder().asLeader(true).build()
    }

    override fun execute(info: CommandInfo): Boolean {
        val role = info.getArgAsRole(0) ?: return false
        val roleToReplace = info.getArgAsRole(1) ?: return false
        val showList = info.getArgAsBoolean(2, false) ?: false

        if (role == roleToReplace) {
            info.message(Message.commandRolesMoveSameRole)
            return false
        }

        info.faction!!.factionRoles.replaceRoles(role, roleToReplace)
        info.message(Message.commandRolesMoveSuccess)

        if (showList) {
            CmdRolesList().execute(info)
        }

        return true
    }

    override fun getHelpInfo(): String {
        return Message.commandRolesMoveHelp
    }
}