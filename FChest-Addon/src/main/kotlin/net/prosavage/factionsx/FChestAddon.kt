package net.prosavage.factionsx

import net.prosavage.factionsx.FactionsX.Companion.baseCommand
import net.prosavage.factionsx.addonframework.AddonPlugin
import net.prosavage.factionsx.addonframework.StartupResponse
import net.prosavage.factionsx.cmd.CmdChest
import net.prosavage.factionsx.command.engine.FCommand
import net.prosavage.factionsx.manager.UpgradeManager
import net.prosavage.factionsx.persist.ChestConfig
import net.prosavage.factionsx.persist.ChestData
import net.prosavage.factionsx.upgrade.ChestUpgrade
import net.prosavage.factionsx.upgrade.Upgrade

import net.prosavage.factionsx.FactionsX.Companion.instance as FXInstance

class FChestAddon : AddonPlugin(true) {
    lateinit var chestCommand: FCommand

    companion object {
        lateinit var upgrade: Upgrade
    }

    override fun onStart(): StartupResponse {
        logColored("Registering commands...")
        chestCommand = CmdChest()
        baseCommand.addSubCommand(chestCommand)

        logColored("Loading config...")
        ChestConfig.load(this)

        logColored("Loading data...")
        ChestData.load(this)

        logColored("Registering upgrade.")
        upgrade = ChestUpgrade(
                ChestConfig.chestUpgrade.name,
                ChestConfig.chestUpgrade.upgradeItem,
                ChestConfig.chestUpgrade.upgradeMaxLevelLore,
                ChestConfig.chestUpgrade.costLevel
        )
        UpgradeManager.registerUpgrade(ChestConfig.chestUpgrade.scope, upgrade)
        return StartupResponse.ok()
    }

    override fun onTerminate() {
        // remove the command to make sure it doesn't get repeated in /f help etc...
        logColored("Unregistering commands...")
        baseCommand.removeSubCommand(chestCommand)

        logColored("Saving config...")
        ChestConfig.load(this)
        ChestConfig.save(this)

        logColored("Saving data...")
        logColored("${ChestData.chests.size} chests in cache.")
        ChestData.save(this)

        UpgradeManager.deRegisterUpgrade(FXInstance, upgrade)
    }
}