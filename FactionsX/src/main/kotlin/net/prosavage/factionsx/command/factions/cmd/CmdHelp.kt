package net.prosavage.factionsx.command.factions.cmd

import net.prosavage.factionsx.FactionsX
import net.prosavage.factionsx.command.engine.CommandInfo
import net.prosavage.factionsx.command.engine.CommandRequirementsBuilder
import net.prosavage.factionsx.command.engine.FCommand
import net.prosavage.factionsx.persist.Message

class CmdHelp : FCommand() {
    init {
        aliases.add("help")

        optionalArgs.add(Argument("page-number", 0, IntArgument()))

        commandRequirements = CommandRequirementsBuilder()
                .build()
    }

    override fun execute(info: CommandInfo): Boolean {
        info.message(Message.commandHelpTitle)

        val page = info.getArgAsInt(0, false) ?: run {
            FactionsX.baseCommand.generateHelp(1, info.commandSender, info.args)
            return false
        }

        FactionsX.baseCommand.generateHelp(page, info.commandSender, info.args)
        return true
    }

    override fun getHelpInfo(): String {
        return Message.commandHelpHelp
    }
}