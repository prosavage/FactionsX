package net.prosavage.factionsx.cmd.tnt.bank

import net.prosavage.factionsx.cmd.argument.TNTInBankArgument
import net.prosavage.factionsx.command.engine.CommandInfo
import net.prosavage.factionsx.command.engine.CommandRequirementsBuilder
import net.prosavage.factionsx.command.engine.FCommand
import net.prosavage.factionsx.persist.TNTAddonData
import net.prosavage.factionsx.persist.TNTConfig
import org.bukkit.Material

class CmdTntBankRemove : FCommand() {

    init {
        aliases.add("remove")
        aliases.add("take")

        requiredArgs.add(Argument("amount", 0, TNTInBankArgument()))

        commandRequirements = CommandRequirementsBuilder()
                .asFactionMember(true)
                .build()
    }


    override fun execute(info: CommandInfo): Boolean {
        val amt = info.getArgAsInt(0) ?: return false
        if (amt <= 0) {
            info.message(TNTConfig.commandTntCannotBeNegaitve)
            return true
        }
        val tntData = TNTAddonData.tntData.getTNTData(info.faction!!)
        if (amt > tntData.tntAmt) {
            info.message(TNTConfig.commandTntBankRemoveNotEnough, tntData.tntAmt.toString())
            return false
        }

        val failed = info.fPlayer!!.addToInventory(Material.TNT, amt)
        tntData.tntAmt -= (amt - failed)
        info.message(TNTConfig.commandTntBankRemoveTaken, (amt - failed).toString())
        return true
    }

    override fun getHelpInfo(): String {
        return TNTConfig.commandTntBankRemoveHelp
    }
}