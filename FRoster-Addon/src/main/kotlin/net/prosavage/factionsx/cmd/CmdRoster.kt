package net.prosavage.factionsx.cmd

import net.prosavage.factionsx.command.engine.CommandInfo
import net.prosavage.factionsx.command.engine.CommandRequirementsBuilder
import net.prosavage.factionsx.command.engine.FCommand
import net.prosavage.factionsx.persist.RosterConfig

class CmdRoster(parent: FCommand) : FCommand() {
    init {
        aliases.add("roster")
        prefix = parent.prefix
        subCommands.add(CmdRosterAdd())
        subCommands.add(CmdRosterList())
        subCommands.add(CmdRosterKick())
        subCommands.add(CmdRosterJoin())

        commandRequirements = CommandRequirementsBuilder()
                .asPlayer(true)
                .build()

        initializeSubCommandData()
    }

    override fun execute(info: CommandInfo): Boolean {
        generateHelp(1, info.commandSender, info.args)
        return true
    }

    override fun getHelpInfo(): String {
        return RosterConfig.rosterHelp
    }
}