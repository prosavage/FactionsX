package net.prosavage.factionsx.cmd.tnt.bank

import net.prosavage.factionsx.command.engine.CommandInfo
import net.prosavage.factionsx.command.engine.CommandRequirementsBuilder
import net.prosavage.factionsx.command.engine.FCommand
import net.prosavage.factionsx.core.TNTBankAction
import net.prosavage.factionsx.persist.TNTConfig

class CmdTntBank(parent: FCommand) : FCommand() {

    init {
        aliases.add("tntbank")

        prefix = parent.prefix

        addSubCommand(CmdTntBankAdd())
        addSubCommand(CmdTntBankRemove())
        addSubCommand(CmdTntBankBal())

        commandRequirements = CommandRequirementsBuilder()
                .asFactionMember(true)
                .withRawPermission(TNTConfig.tntBankCommandPermission)
                .withSpecialAction(TNTBankAction())
                .build()
    }

    override fun execute(info: CommandInfo): Boolean {
        generateHelp(1, info.commandSender, info.args)
        return true
    }

    override fun getHelpInfo(): String {
        return TNTConfig.commandTntBankHelp
    }
}