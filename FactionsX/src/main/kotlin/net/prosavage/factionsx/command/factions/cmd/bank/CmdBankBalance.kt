package net.prosavage.factionsx.command.factions.cmd.bank

import net.prosavage.factionsx.command.engine.CommandInfo
import net.prosavage.factionsx.command.engine.CommandRequirementsBuilder
import net.prosavage.factionsx.command.engine.FCommand
import net.prosavage.factionsx.core.Permission
import net.prosavage.factionsx.persist.Message

class CmdBankBalance : FCommand() {
    init {
        aliases += "balance"
        aliases += "bal"

        commandRequirements = CommandRequirementsBuilder()
                .asFactionMember(true)
                .withPermission(Permission.BANK_BALANCE)
                .build()
    }

    override fun execute(info: CommandInfo): Boolean {
        if (!CmdBank.economyCheck(info)) {
            return false
        }

        val faction = info.faction ?: return false
        info.message(Message.commandBankBalanceSuccess, faction.bank.amount.toString())
        return true
    }

    override fun getHelpInfo(): String = Message.commandBankBalanceHelp
}