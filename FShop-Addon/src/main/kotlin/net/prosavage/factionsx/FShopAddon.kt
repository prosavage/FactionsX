package net.prosavage.factionsx

import net.prosavage.factionsx.addonframework.Addon
import net.prosavage.factionsx.cmd.CmdShop
import net.prosavage.factionsx.command.engine.FCommand
import net.prosavage.factionsx.persist.ShopConfig

class FShopAddon : Addon() {
    lateinit var shopCommand: FCommand

    override fun onEnable() {
        ShopConfig.load(this)
        logColored("Loaded shop config.")

        shopCommand = CmdShop()
        FactionsX.baseCommand.addSubCommand(shopCommand)
    }

    override fun onDisable() {
        // remove the command to make sure it doesn't get repeated in /f help etc...
        FactionsX.baseCommand.removeSubCommand(shopCommand)

        ShopConfig.load(this)
        ShopConfig.save(this)
    }
}