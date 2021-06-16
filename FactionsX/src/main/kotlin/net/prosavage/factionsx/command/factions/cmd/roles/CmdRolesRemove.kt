package net.prosavage.factionsx.command.factions.cmd.roles

import net.prosavage.factionsx.command.engine.CommandInfo
import net.prosavage.factionsx.command.engine.CommandRequirementsBuilder
import net.prosavage.factionsx.command.engine.FCommand
import net.prosavage.factionsx.persist.Message

class CmdRolesRemove : FCommand() {

    init {
        aliases.add("remove")

        requiredArgs.add(Argument("role", 0, RolesArgument()))
        optionalArgs.add(Argument("list-roles", 1, BooleanArgument()))

        commandRequirements = CommandRequirementsBuilder().asLeader(true).build()
    }

    override fun execute(info: CommandInfo): Boolean {
        val role = info.getArgAsRole(0) ?: return false
        val showList = info.getArgAsBoolean(1, false) ?: false

        val factionRoles = info.faction!!.factionRoles

        if (factionRoles.getApexRole() == role) {
            info.message(Message.commandRolesRemoveApexRole)
            return false
        }

        if (factionRoles.getAllRoles().size <= 2) {
            info.message(Message.commandRolesRemoveNotEnough)
            return false
        }

        factionRoles.removeRole(role)
        info.message(Message.commandRolesRemoveSuccess, role.roleTag)

        if (showList) {
            CmdRolesList().execute(info)
        }

        return true
    }

    override fun getHelpInfo(): String {
        return Message.commandRolesRemoveHelp
    }
}