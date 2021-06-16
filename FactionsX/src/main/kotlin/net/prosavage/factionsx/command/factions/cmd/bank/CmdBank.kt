package net.prosavage.factionsx.command.factions.cmd.bank

import net.prosavage.factionsx.command.engine.CommandInfo
import net.prosavage.factionsx.command.engine.CommandRequirementsBuilder
import net.prosavage.factionsx.command.engine.FCommand
import net.prosavage.factionsx.core.Permission
import net.prosavage.factionsx.hook.vault.VaultHook
import net.prosavage.factionsx.persist.Message
import net.prosavage.factionsx.persist.config.EconConfig

class CmdBank : FCommand() {
    init {
        aliases += "bank"

        commandRequirements = CommandRequirementsBuilder()
                .asFactionMember(true)
                .withPermission(Permission.BANK)
                .build()

        addSubCommand(CmdBankBalance())
        addSubCommand(CmdBankDeposit())
        addSubCommand(CmdBankWithdraw())
        addSubCommand(CmdBankPay())
        addSubCommand(CmdBankLogs())
    }

    override fun execute(info: CommandInfo): Boolean {
        if (!economyCheck(info)) {
            return false
        }

        generateHelp(1, info.commandSender, info.args)
        return true
    }

    override fun getHelpInfo(): String = Message.commandBankHelp

    companion object {
        fun economyCheck(info: CommandInfo): Boolean {
            if (!EconConfig.economyEnabled || !VaultHook.isHooked()) {
                info.message(Message.commandBankUnavailable)
                return false
            }
            return true
        }
    }
}