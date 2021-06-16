package net.prosavage.factionsx.command.factions.cmd.social

import net.prosavage.factionsx.command.engine.CommandInfo
import net.prosavage.factionsx.command.engine.CommandRequirementsBuilder
import net.prosavage.factionsx.command.engine.FCommand
import net.prosavage.factionsx.command.factions.cmd.social.paypal.CmdPaypalSet
import net.prosavage.factionsx.command.factions.cmd.social.paypal.CmdPaypalView
import net.prosavage.factionsx.core.Permission
import net.prosavage.factionsx.persist.Message

class CmdPaypal : FCommand() {
    init {
        aliases.add("paypal")

        commandRequirements = CommandRequirementsBuilder()
                .asPlayer(true)
                .withPermission(Permission.PAYPAL_HELP)
                .build()

        this.addSubCommand(CmdPaypalSet())
        this.addSubCommand(CmdPaypalView())
    }

    override fun execute(info: CommandInfo): Boolean {
        generateHelp(1, info.commandSender, info.args)
        return true
    }

    override fun getHelpInfo(): String {
        return Message.commandPaypalHelp
    }
}