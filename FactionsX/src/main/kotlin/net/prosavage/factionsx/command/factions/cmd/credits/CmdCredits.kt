package net.prosavage.factionsx.command.factions.cmd.credits

import net.prosavage.factionsx.command.engine.CommandInfo
import net.prosavage.factionsx.command.engine.CommandRequirementsBuilder
import net.prosavage.factionsx.command.engine.FCommand
import net.prosavage.factionsx.core.Permission
import net.prosavage.factionsx.persist.Message

class CmdCredits : FCommand() {
    init {
        aliases.add("credits")

        commandRequirements = CommandRequirementsBuilder()
                .asFactionMember(true)
                .withPermission(Permission.CREDITS)
                .build()

        this.addSubCommand(CmdCreditsBalance())
        this.addSubCommand(CmdCreditsPay())
    }

    override fun execute(info: CommandInfo): Boolean {
        generateHelp(1, info.commandSender, info.args)
        return true
    }

    override fun getHelpInfo(): String {
        return Message.commandCreditsHelp
    }
}