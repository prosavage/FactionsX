package net.prosavage.factionsx.command.factions.cmd.bank

import net.prosavage.factionsx.command.engine.CommandInfo
import net.prosavage.factionsx.command.engine.CommandRequirementsBuilder
import net.prosavage.factionsx.command.engine.FCommand
import net.prosavage.factionsx.core.Permission
import net.prosavage.factionsx.persist.Message
import net.prosavage.factionsx.util.MemberAction

class CmdBankPay : FCommand() {
    init {
        aliases += "pay"

        requiredArgs += Argument("target", 0, FactionArgument())
        requiredArgs += Argument("amount", 1, IntArgument())

        commandRequirements = CommandRequirementsBuilder()
                .asFactionMember(true)
                .withPermission(Permission.BANK_PAY)
                .withMemberAction(MemberAction.BANK_PAY)
                .build()
    }

    override fun execute(info: CommandInfo): Boolean {
        if (!CmdBank.economyCheck(info)) {
            return false
        }

        val faction = info.faction ?: return false
        val target = info.getArgAsFaction(0) ?: return false
        val amount = info.getArgAsDouble(1) ?: return false

        if (faction.bank.amount < amount || amount <= 0) {
            info.message(Message.commandBankPayNotEnough, amount.toString(), target.tag)
            return false
        }


        info.faction!!.bank.pay(info.fPlayer!!, amount, target)

        info.message(Message.commandBankPaySuccessful, amount.toString(), target.tag)
        target.message(Message.commandBankPaySuccessfulOther, amount.toString(), faction.tag)
        return true
    }

    override fun getHelpInfo(): String = Message.commandBankPayHelp
}