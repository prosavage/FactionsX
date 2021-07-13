package net.prosavage.factionsx.persist

import net.prosavage.factionsx.addonframework.AddonPlugin
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

    fun save(addon: AddonPlugin) {
        addon.configSerializer.save(instance, File(addon.dataFolder, "printer-data.json"))
    }

    fun load(addon: AddonPlugin) {
        addon.configSerializer.load(instance, PrinterData::class.java, File(addon.dataFolder, "printer-data.json"))
    }
}