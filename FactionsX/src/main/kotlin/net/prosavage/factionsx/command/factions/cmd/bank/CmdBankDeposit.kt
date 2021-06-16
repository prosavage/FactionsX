package net.prosavage.factionsx.command.factions.cmd.bank

import net.prosavage.factionsx.command.engine.CommandInfo
import net.prosavage.factionsx.command.engine.CommandRequirementsBuilder
import net.prosavage.factionsx.command.engine.FCommand
import net.prosavage.factionsx.core.Permission
import net.prosavage.factionsx.hook.vault.VaultHook
import net.prosavage.factionsx.persist.Message
import net.prosavage.factionsx.persist.config.Config
import net.prosavage.factionsx.util.MemberAction

class CmdBankDeposit : FCommand() {
    init {
        aliases += "deposit"

        requiredArgs += Argument("amount", 0, IntArgument())

        commandRequirements = CommandRequirementsBuilder()
                .asFactionMember(true)
                .withPermission(Permission.BANK_DEPOSIT)
                .withMemberAction(MemberAction.BANK_DEPOSIT)
                .build()
    }

    override fun execute(info: CommandInfo): Boolean {
        if (!CmdBank.economyCheck(info)) {
            return false
        }

        val faction = info.faction ?: return false
        val player = info.fPlayer ?: return false
        val amount = info.getArgAsDouble(0) ?: return false

        if (!VaultHook.hasEnough(player, amount) || amount <= 0) {
            info.message(Message.commandBankDepositNotEnough, amount.toString())
            return false
        }

        VaultHook.takeFrom(player, amount)

        faction.bank.deposit(info.fPlayer!!, amount)

        info.message(Message.commandBankDepositSuccessful, Config.numberFormat.format(amount), Config.numberFormat.format(faction.bank.amount))
        return true
    }

    override fun getHelpInfo(): String = Message.commandBankDepositHelp
}