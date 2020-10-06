package net.prosavage.factionsx

import net.prosavage.factionsx.addonframework.Addon
import net.prosavage.factionsx.manager.UpgradeManager
import net.prosavage.factionsx.persist.MaxTileEntitiesConfig
import net.prosavage.factionsx.upgrade.MaxHopperUpgrade

class FMaxTileEntitiesAddon : Addon() {

    companion object {
        lateinit var maxHopperUpgrade: MaxHopperUpgrade
    }


    override fun onEnable() {
        MaxTileEntitiesConfig.load(this)
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

    }

    override fun onDisable() {
        MaxTileEntitiesConfig.load(this)
        MaxTileEntitiesConfig.save(this)
        UpgradeManager.deRegisterUpgrade(FactionsX.instance, maxHopperUpgrade)
    }


}