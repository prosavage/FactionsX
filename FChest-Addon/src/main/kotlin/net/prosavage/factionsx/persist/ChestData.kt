package net.prosavage.factionsx.persist

import net.prosavage.factionsx.addonframework.AddonPlugin
import net.prosavage.factionsx.core.Faction
import org.bukkit.Bukkit
import org.bukkit.inventory.Inventory
import java.io.File

object ChestData {
    @Transient
    private val instance = this

    var chests = HashMap<Long, Inventory>()

    fun save(addon: AddonPlugin) {
        addon.configSerializer.save(
            instance,
            File(addon.dataFolder, "chest-data.json")
        )
    }

    fun load(addon: AddonPlugin) {
        addon.configSerializer.load(
            instance,
            ChestData::class.java,
            File(addon.dataFolder, "chest-data.json")
        )
    }
}

fun Faction.getChest(): Inventory = ChestData.chests.getOrPut(id) {
    Bukkit.createInventory(null, 9 * ChestConfig.defaultChestRows, tag)
}