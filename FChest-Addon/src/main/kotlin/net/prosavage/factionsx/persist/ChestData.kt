package net.prosavage.factionsx.persist

import net.prosavage.factionsx.addonframework.Addon
import net.prosavage.factionsx.core.Faction
import org.bukkit.Bukkit
import org.bukkit.inventory.Inventory
import java.io.File

object ChestData {

    @Transient
    private val instance = this

    var chests = HashMap<Long, Inventory>()


    fun save(addon: Addon) {
        addon.configSerializer.save(instance, File(addon.addonDataFolder, "chest-data.json"))
    }

    fun load(addon: Addon) {
        addon.configSerializer.load(instance, ChestData::class.java, File(addon.addonDataFolder, "chest-data.json"))
    }

}

fun Faction.getChest(): Inventory {
    return ChestData.chests.getOrPut(id, { Bukkit.createInventory(null, 9 * ChestConfig.defaultChestRows, tag) })
}



