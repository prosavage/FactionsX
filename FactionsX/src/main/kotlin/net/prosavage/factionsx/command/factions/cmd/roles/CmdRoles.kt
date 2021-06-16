package net.prosavage.factionsx.command.factions.cmd.roles

import net.prosavage.factionsx.command.engine.CommandInfo
import net.prosavage.factionsx.command.engine.CommandRequirementsBuilder
import net.prosavage.factionsx.command.engine.FCommand
import net.prosavage.factionsx.persist.Message

class CmdRoles : FCommand() {

    init {
        aliases.add("roles")
        aliases.add("role")


        subCommands.add(CmdRolesList())
        subCommands.add(CmdRolesInfo())
        subCommands.add(CmdRolesEdit())
        subCommands.add(CmdRolesRemove())
        subCommands.add(CmdRolesAdd())
        subCommands.add(CmdRolesMove())

        commandRequirements = CommandRequirementsBuilder().asLeader(true).build()
    }

    override fun execute(info: CommandInfo): Boolean {
        generateHelp(1, info.commandSender, info.args)
        return true
    }

    override fun getHelpInfo(): String {
        return Message.commandRolesHelp
    }
}