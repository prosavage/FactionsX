package net.prosavage.factionsx.cmd.tnt.bank

import net.prosavage.factionsx.api.SimpleAPIService
import net.prosavage.factionsx.command.engine.CommandInfo
import net.prosavage.factionsx.command.engine.CommandRequirementsBuilder
import net.prosavage.factionsx.command.engine.FCommand
import net.prosavage.factionsx.persist.TNTAddonData
import net.prosavage.factionsx.persist.TNTConfig

class CmdTntBankBal : FCommand() {
    init {
        aliases.add("bal")

        commandRequirements = CommandRequirementsBuilder()
                .asFactionMember(true)
                .build()
    }

    override fun execute(info: CommandInfo): Boolean {
        val tntData = SimpleAPIService.of(info.faction!!)
        info.message(TNTConfig.commandTntBankBalMessage, tntData.tntAmt.toString())
        return true
    }

    override fun getHelpInfo(): String {
        return TNTConfig.commandTntBankBalHelp
    }
}