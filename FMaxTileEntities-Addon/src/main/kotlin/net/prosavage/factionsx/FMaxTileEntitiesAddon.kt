package net.prosavage.factionsx

import net.prosavage.factionsx.addonframework.AddonPlugin
import net.prosavage.factionsx.addonframework.StartupResponse
import net.prosavage.factionsx.manager.UpgradeManager
import net.prosavage.factionsx.persist.MaxTileEntitiesConfig
import net.prosavage.factionsx.upgrade.MaxHopperUpgrade

class FMaxTileEntitiesAddon : AddonPlugin(true) {
    companion object {
        lateinit var maxHopperUpgrade: MaxHopperUpgrade
    }

    override fun onStart(): StartupResponse {
        // load
        MaxTileEntitiesConfig.load(this)

        // if enabled, register
        if (MaxTileEntitiesConfig.maxHopperUpgrade.enabled) {
            maxHopperUpgrade = MaxHopperUpgrade(
                    MaxTileEntitiesConfig.maxHopperUpgrade.name,
                    MaxTileEntitiesConfig.maxHopperUpgrade.upgradeItem,
                    MaxTileEntitiesConfig.maxHopperUpgrade.upgradeMaxLevelLore,
                    MaxTileEntitiesConfig.maxHopperUpgrade.costLevel
            )
            UpgradeManager.registerUpgrade(MaxTileEntitiesConfig.maxHopperUpgrade.scope, maxHopperUpgrade)
            logColored("Registered Hopper Upgrade.")
        }

        // success
        return StartupResponse.ok()
    }

    override fun onTerminate() {
        MaxTileEntitiesConfig.load(this)
        MaxTileEntitiesConfig.save(this)
        UpgradeManager.deRegisterUpgrade(FactionsX.instance, maxHopperUpgrade)
    }
}