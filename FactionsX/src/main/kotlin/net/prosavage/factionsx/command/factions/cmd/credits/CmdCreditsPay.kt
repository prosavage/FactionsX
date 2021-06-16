package net.prosavage.factionsx.command.factions.cmd.credits

import net.prosavage.factionsx.command.engine.CommandInfo
import net.prosavage.factionsx.command.engine.CommandRequirementsBuilder
import net.prosavage.factionsx.command.engine.FCommand
import net.prosavage.factionsx.core.Permission
import net.prosavage.factionsx.manager.CreditManager
import net.prosavage.factionsx.persist.Message
import net.prosavage.factionsx.persist.config.Config

class CmdCreditsPay : FCommand() {
    init {
        aliases.add("pay")

        requiredArgs.add(Argument("target", 0, PlayerArgument()))
        requiredArgs.add(Argument("credits", 1, IntArgument()))

        commandRequirements = CommandRequirementsBuilder()
                .asPlayer(true)
                .withPermission(Permission.PAY)
                .build()
    }

    override fun execute(info: CommandInfo): Boolean {
        val target = info.getArgAsFPlayer(0) ?: return false
        val amount = info.getArgAsDouble(1) ?: return false

        val minimum = Config.creditSettings.minimumCreditsPay
        if (amount < minimum) {
            info.message(Message.commandCreditsPayMinAmount, minimum.toString())
            return false
        }

        val fPlayer = info.fPlayer ?: return false
        if (!CreditManager.has(fPlayer, amount)) {
            info.message(Message.commandCreditsNotEnough, fPlayer.credits.toString())
            return false
        }

        CreditManager.pay(fPlayer, target, amount).let { overflow ->
            val preciseAmount = (amount - overflow).toString()
            target.message(Message.commandCreditsPayNotify, preciseAmount, fPlayer.name)
            fPlayer.message(Message.commandCreditsPay, target.name, preciseAmount)
        }
        return true
    }

    override fun getHelpInfo(): String {
        return Message.commandCreditsPayHelp
    }
}