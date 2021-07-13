package net.prosavage.factionsx

import com.cryptomorin.xseries.XMaterial
import net.prosavage.factionsx.addonframework.AddonPlugin
import net.prosavage.factionsx.addonframework.StartupResponse
import net.prosavage.factionsx.manager.UpgradeManager
import net.prosavage.factionsx.persist.CropsUpgradesConfig
import net.prosavage.factionsx.upgrade.CropUpgrade

class FCropUpgradesAddon : AddonPlugin(true) {
    override fun onStart(): StartupResponse {
        if (XMaterial.isNewVersion().not()) {
            return StartupResponse.error("This addon is compatible with 1.13 or higher server software only.")
        }

        // load all crop upgrades
        CropsUpgradesConfig.load(this)

        // loop and register if enabled
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

        return StartupResponse.ok()
    }

    override fun onTerminate() {
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