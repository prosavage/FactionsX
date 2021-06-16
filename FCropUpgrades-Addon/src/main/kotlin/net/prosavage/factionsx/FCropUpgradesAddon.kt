package net.prosavage.factionsx

import com.cryptomorin.xseries.XMaterial
import net.prosavage.factionsx.addonframework.Addon
import net.prosavage.factionsx.manager.UpgradeManager
import net.prosavage.factionsx.persist.CropsUpgradesConfig
import net.prosavage.factionsx.upgrade.CropUpgrade

class FCropUpgradesAddon : Addon() {


    override fun onEnable() {
        if (XMaterial.isNewVersion().not()) {
            logColored("This addon is compatible with 1.13 or higher server software only.")
            logColored("Upgrades, and configuration will not be loaded.")
            return
        }
        CropsUpgradesConfig.load(this)
        CropsUpgradesConfig.cropUpgrades.forEach { cropUpgrade ->
            if (cropUpgrade.enabled) {
                UpgradeManager.registerUpgrade(cropUpgrade.scope, CropUpgrade(
                        cropUpgrade.name,
                        cropUpgrade.upgradeItem,
                        cropUpgrade.upgradeMaxLevelLore,
                        cropUpgrade.costLevel,
                        XMaterial.valueOf(cropUpgrade.upgradeParam)
                ))
                logColored("Registered Upgrade: ${cropUpgrade.name}")
            }
        }
    }

    override fun onDisable() {
        if (XMaterial.isNewVersion().not()) {
            return
        }
        // for reloading on shutdown.
        CropsUpgradesConfig.load(this)
        CropsUpgradesConfig.cropUpgrades.forEach { cropUpgrade ->
            UpgradeManager.getUpgradeByName(cropUpgrade.name).apply {
                UpgradeManager.deRegisterUpgrade(FactionsX.instance, this!!)
                logColored("Deregistered Upgrade: ${this.name}")
            }
        }
    }
}