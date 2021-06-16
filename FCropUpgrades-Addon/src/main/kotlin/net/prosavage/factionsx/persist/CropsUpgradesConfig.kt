package net.prosavage.factionsx.persist

import com.cryptomorin.xseries.XMaterial
import net.prosavage.factionsx.addonframework.Addon
import net.prosavage.factionsx.upgrade.ConfigurableUpgrade
import net.prosavage.factionsx.upgrade.LevelInfo
import net.prosavage.factionsx.upgrade.UpgradeScope
import net.prosavage.factionsx.util.SerializableItem
import java.io.File

object CropsUpgradesConfig {

    @Transient
    private val instance = this


    var cropUpgrades = setOf<ConfigurableUpgrade>(
            ConfigurableUpgrade(
                    true,
                    "BEETROOT",
                    UpgradeScope.TERRITORY,
                    mapOf(
                            1 to LevelInfo(.25, 10000.0),
                            2 to LevelInfo(.40, 50000.0),
                            3 to LevelInfo(.50, 100000.0),
                            4 to LevelInfo(.80, 500000.0),
                            5 to LevelInfo(1.0, 1000000.0)
                    ),
                    SerializableItem(
                            XMaterial.BEETROOT,
                            "&2Beetroot Growth",
                            listOf("&7Chance of growing &2instantly&7.", "&7Level: &2&l{level}", "&7Upgrade Cost: &2&l\${cost}"),
                            1
                    ),
                    listOf("&7Chance of growing &2instantly&7.", "&c&lMAX LEVEL REACHED"),
                    XMaterial.BEETROOTS.name

            ),
            ConfigurableUpgrade(
                    true,
                    "CARROTS",
                    UpgradeScope.TERRITORY,
                    mapOf(
                            1 to LevelInfo(.25, 10000.0),
                            2 to LevelInfo(.40, 50000.0),
                            3 to LevelInfo(.50, 100000.0),
                            4 to LevelInfo(.80, 500000.0),
                            5 to LevelInfo(1.0, 1000000.0)
                    ),
                    SerializableItem(
                            XMaterial.CARROT,
                            "&6Carrot Growth",
                            listOf("&7Chance of growing &6instantly&7.", "&7Level: &6&l{level}", "&7Upgrade Cost: &6&l\${cost}"),
                            1
                    ),
                    listOf("&7Chance of growing &6instantly&7.", "&c&lMAX LEVEL REACHED"),
                    XMaterial.CARROTS.name
            ),
            ConfigurableUpgrade(
                    true,
                    "POTATOES",
                    UpgradeScope.TERRITORY,
                    mapOf(
                            1 to LevelInfo(.25, 10000.0),
                            2 to LevelInfo(.40, 50000.0),
                            3 to LevelInfo(.50, 100000.0),
                            4 to LevelInfo(.80, 500000.0),
                            5 to LevelInfo(1.0, 1000000.0)
                    ),
                    SerializableItem(
                            XMaterial.POTATO,
                            "&ePotato Growth",
                            listOf("&7Chance of growing &einstantly&7.", "&7Level: &e&l{level}", "&7Upgrade Cost: &e&l\${cost}"),
                            1
                    ),
                    listOf("&7Chance of growing &einstantly&7.", "&c&lMAX LEVEL REACHED"),
                    XMaterial.POTATOES.name
            )
    )

    fun save(addon: Addon) {
        addon.configSerializer.save(instance, File(addon.addonDataFolder, "config.json"))
    }

    fun load(addon: Addon) {
        addon.configSerializer.load(instance, CropsUpgradesConfig::class.java, File(addon.addonDataFolder, "config.json"))
    }
}