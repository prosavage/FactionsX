package net.prosavage.factionsx.persist

import com.cryptomorin.xseries.XMaterial
import net.prosavage.factionsx.addonframework.Addon
import net.prosavage.factionsx.upgrade.ConfigurableUpgrade
import net.prosavage.factionsx.upgrade.LevelInfo
import net.prosavage.factionsx.upgrade.UpgradeScope
import net.prosavage.factionsx.util.SerializableItem
import java.io.File

object MaxTileEntitiesConfig {
    @Transient
    private val instance = this

    var maxHopperUpgrade = ConfigurableUpgrade(
            true,
            "MAX_HOPPER",
            UpgradeScope.TERRITORY,
            mapOf(
                    1 to LevelInfo(2.0, 10000.0),
                    2 to LevelInfo(3.0, 50000.0),
                    3 to LevelInfo(5.0, 100000.0),
                    4 to LevelInfo(8.0, 500000.0),
                    5 to LevelInfo(10.0, 1000000.0)
            ),
            SerializableItem(
                    XMaterial.HOPPER,
                    "&fMax Hoppers",
                    listOf("&fMax Hoppers In Chunk.&7.", "&7Level: &f&l{level}", "&7Upgrade Cost: &f&l\${cost}"),
                    1
            ),
            listOf("&fMax Hoppers In Chunk.&7.", "&c&lMAX LEVEL REACHED"),
            "HOPPER"
    )


    fun save(addon: Addon) {
        addon.configSerializer.save(instance, File(addon.addonDataFolder, "config.json"))
    }

    fun load(addon: Addon) {
        addon.configSerializer.load(instance, MaxTileEntitiesConfig::class.java, File(addon.addonDataFolder, "config.json"))
    }

}