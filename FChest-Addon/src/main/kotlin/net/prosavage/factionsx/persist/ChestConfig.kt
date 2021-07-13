package net.prosavage.factionsx.persist

import com.cryptomorin.xseries.XMaterial
import net.prosavage.factionsx.addonframework.AddonPlugin
import net.prosavage.factionsx.upgrade.ConfigurableUpgrade
import net.prosavage.factionsx.upgrade.LevelInfo
import net.prosavage.factionsx.upgrade.UpgradeScope
import net.prosavage.factionsx.util.SerializableItem
import java.io.File

object ChestConfig {

    @Transient
    private val instance = this


    var cmdChestPermission = "factionsx.player.chest"
    var cmdChestMessage = "&7Opening faction chest..."
    var cmdChestHelp = "open the faction chest."

    var defaultChestRows = 1

    var chestUpgrade: ConfigurableUpgrade = ConfigurableUpgrade(
            true,
            "CHEST",
            UpgradeScope.GLOBAL,
            mapOf(
                    1 to LevelInfo(2.0, 10000.0),
                    2 to LevelInfo(3.0, 20000.0),
                    3 to LevelInfo(4.0, 20000.0),
                    4 to LevelInfo(5.0, 20000.0)
            ),
            SerializableItem(
                    XMaterial.CHEST,
                    "&bChest Upgrade",
                    listOf("&7Add more rows to faction chest.", "&7Level: &b&l{level}", "&7Upgrade Cost: &b&l\${cost}"),
                    1
            ),
            listOf("&7Maxed out factions chest.", "&c&lMAX LEVEL REACHED"),
            XMaterial.CHEST.name
    )

    fun save(addon: AddonPlugin) {
        addon.configSerializer.save(instance, File(addon.dataFolder, "config.json"))
    }

    fun load(addon: AddonPlugin) {
        addon.configSerializer.load(instance, ChestConfig::class.java, File(addon.dataFolder, "config.json"))
    }
}