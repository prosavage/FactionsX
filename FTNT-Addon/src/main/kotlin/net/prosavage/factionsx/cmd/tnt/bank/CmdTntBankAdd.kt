package net.prosavage.factionsx.cmd.tnt.bank

import com.cryptomorin.xseries.XMaterial
import net.prosavage.factionsx.cmd.argument.TNTInInventoryArgument
import net.prosavage.factionsx.command.engine.CommandInfo
import net.prosavage.factionsx.command.engine.CommandRequirementsBuilder
import net.prosavage.factionsx.command.engine.FCommand
import net.prosavage.factionsx.persist.TNTAddonData
import net.prosavage.factionsx.persist.TNTConfig

class CmdTntBankAdd : FCommand() {

    init {
        aliases.add("add")

        requiredArgs.add(Argument("amount", 0, TNTInInventoryArgument()))

        commandRequirements = CommandRequirementsBuilder()
                .asFactionMember(true)
                .build()
    }


    override fun execute(info: CommandInfo): Boolean {
        val amt = info.getArgAsInt(0) ?: return false
        if (amt <= 0) {
            info.message(TNTConfig.commandTntCannotBeNegaitve)
            return false
        }
        val amountOfMaterialInPlayerInv = info.fPlayer!!.getAmountOfMaterialInPlayerInv(XMaterial.TNT)
        if (amountOfMaterialInPlayerInv < amt) {
            info.message(TNTConfig.commandTntbankAddNotEnoughTnt, amountOfMaterialInPlayerInv.toString())
            return false
        }
        val tntData = TNTAddonData.tntData.getTNTData(info.faction!!)
        val leftover = tntData.addTnt(amt)
        info.fPlayer!!.takeAmountOfMaterialFromPlayerInv(XMaterial.TNT, amt - leftover)
        info.message(TNTConfig.commandTntbankAddSuccess, amt.toString(), tntData.tntAmt.toString(), tntData.limit.toString())
        return true
    }

    override fun getHelpInfo(): String {
        return TNTConfig.commandTntBankAddHelp
    }
}