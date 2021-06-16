package net.prosavage.factionsx.cmd

import net.prosavage.factionsx.command.engine.CommandInfo
import net.prosavage.factionsx.command.engine.CommandRequirementsBuilder
import net.prosavage.factionsx.command.engine.FCommand
import net.prosavage.factionsx.gui.ShopMenu
import net.prosavage.factionsx.persist.ShopConfig

class CmdShop : FCommand() {

    init {
        aliases.add("shop")


        commandRequirements = CommandRequirementsBuilder().asPlayer(true).asFactionMember(true).build()
    }

    override fun execute(info: CommandInfo): Boolean {
        ShopMenu.getInv(info.faction!!)?.open(info.player!!)
        return true
    }

    override fun getHelpInfo(): String {
        return ShopConfig.cmdShopHelp
    }

}