package net.prosavage.factions.command

import net.prosavage.factionsx.command.engine.CommandRequirementsBuilder
import net.prosavage.factionsx.command.engine.FCommand
import javax.activation.CommandInfo


class CmdPrinter : FCommand() {

    init {
        aliases.add("printer")

        optionalArgs.add(Argument("toggle (enabled/disabled)", 0, BooleanArgument()))

        commandRequirements = CommandRequirementsBuilder().withRawPermission(PrinterConfig.printerCommandPermission)
            .asPlayer(true)
            .asFactionMember(true)
            .build()
    }


    override fun execute(info: CommandInfo): Boolean {

    }

    override fun getHelpInfo(): String {
        return \
    }
}