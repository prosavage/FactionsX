package net.prosavage.factionsx.persist

import net.prosavage.factionsx.addonframework.Addon
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.inventory.Inventory
import java.io.File
import java.util.*

object PrinterData {
    @Transient
    private val instance = this

    data class PlayerPrintingData(var cachedInventory: Inventory) {
        var blocksPlaced: HashMap<Location, Material>? = hashMapOf()
        var spent = 0.0
    }

    var inPrinter: HashMap<UUID, PlayerPrintingData> = hashMapOf()

    fun save(addon: Addon) {
        addon.configSerializer.save(instance, File(addon.addonDataFolder, "printer-data.json"))
    }

    fun load(addon: Addon) {
        addon.configSerializer.load(instance, PrinterData::class.java, File(addon.addonDataFolder, "printer-data.json"))
    }
}