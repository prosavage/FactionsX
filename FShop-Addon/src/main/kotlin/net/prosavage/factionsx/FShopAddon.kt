package net.prosavage.factionsx

import net.prosavage.factionsx.addonframework.AddonPlugin
import net.prosavage.factionsx.addonframework.StartupResponse
import net.prosavage.factionsx.cmd.CmdShop
import net.prosavage.factionsx.command.engine.FCommand
import net.prosavage.factionsx.persist.ShopConfig

class FShopAddon : AddonPlugin(true) {
    lateinit var shopCommand: FCommand

    override fun onStart(): StartupResponse {
        ShopConfig.load(this)
        logColored("Loaded shop config.")

        shopCommand = CmdShop()
        FactionsX.baseCommand.addSubCommand(shopCommand)

        return StartupResponse.ok()
    }

    override fun onTerminate() {
        // remove the command to make sure it doesn't get repeated in /f help etc...
        FactionsX.baseCommand.removeSubCommand(shopCommand)

        ShopConfig.load(this)
        ShopConfig.save(this)
    }
}