package net.prosavage.factionsx

import com.cryptomorin.xseries.XMaterial
import net.prosavage.factionsx.addonframework.Addon
import net.prosavage.factionsx.command.CmdPrinter
import net.prosavage.factionsx.core.FPlayer
import net.prosavage.factionsx.listener.PrinterExploitListener
import net.prosavage.factionsx.listener.PrinterListener
import net.prosavage.factionsx.persist.PricingConfig
import net.prosavage.factionsx.persist.PrinterConfig
import net.prosavage.factionsx.persist.PrinterData
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.HandlerList
import org.bukkit.event.inventory.InventoryType
import org.bukkit.inventory.ItemStack

class FPrinterAddon : Addon() {

    lateinit var printerListener: PrinterListener
    lateinit var printerExploitListener: PrinterExploitListener

    companion object {
        lateinit var addon: FPrinterAddon
    }


    override fun onEnable() {
        if (XMaterial.isNewVersion()) {
            logColored("This addon is compatible with 1.12 or lower server software only.")
            logColored("This addon will not be loaded.")
            return
        }
        logColored("Enabling FPrinter-Addon!")
        addon = this
        FactionsX.baseCommand.addSubCommand(CmdPrinter())
        logColored("Injected Printer Command")
        loadFiles()
        logColored("Loaded Configuration & Data Files.")
        logColored("Loaded Inventories and Printer Data of ${PrinterData.inPrinter.size} player(s).")
        printerListener = PrinterListener()
        printerExploitListener = PrinterExploitListener()
        Bukkit.getPluginManager().registerEvents(printerListener, factionsXInstance)
        Bukkit.getPluginManager().registerEvents(printerExploitListener, factionsXInstance)
        logColored("Registered Listeners.")
        loadPlaceholderAPIHook()
    }

    private fun loadPlaceholderAPIHook() {
        if (factionsXInstance.server.pluginManager.isPluginEnabled("PlaceholderAPI")) {
            PrinterPlaceholderAPI().register()
            logColored("Hooked into PlaceholderAPI.")
        }
    }

    private fun loadFiles() {
        PrinterConfig.load(this)
        PricingConfig.load(this)
        PrinterData.load(this)
    }

    override fun onDisable() {
        if (XMaterial.isNewVersion()) {
            return
        }
        logColored("Disabling FPrinter-Addon!")
        FactionsX.baseCommand.removeSubCommand(CmdPrinter())
        logColored("Unregistered Printer Command")
        logColored("Saving ${PrinterData.inPrinter.size} Player's Inventories and Printer Data.")
        saveFiles()
        logColored("Saved Configuration & Data Files.")
        HandlerList.unregisterAll(printerListener)
        HandlerList.unregisterAll(printerExploitListener)
        logColored("Unregister listeners")
    }

    private fun saveFiles() {
        PrinterConfig.load(this)
        PrinterConfig.save(this)
        PricingConfig.load(this)
        PricingConfig.save(this)
        PrinterData.save(this)
    }
}


fun FPlayer.isInPrinter(): Boolean {
    return PrinterData.inPrinter.containsKey(this.uuid)
}

fun FPlayer.enablePrinter() {
    val player = getPlayer() ?: return
    PrinterData.inPrinter[this.uuid] = run {
        val storedInventory = Bukkit.createInventory(null, InventoryType.PLAYER)
        storedInventory.contents = player.inventory.contents
        PrinterData.PlayerPrintingData(storedInventory)
    }
    player.inventory.clear()
    player.closeInventory()
    player.clearArmorSlots()
    player.gameMode = GameMode.CREATIVE
    PrinterConfig.printerCommandsToRunOnEnable.forEach { command ->
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.replace("{player}", player.name))
    }
    PrinterData.save(FPrinterAddon.addon)
}

fun FPlayer.getPrinterData(): PrinterData.PlayerPrintingData? {
    return PrinterData.inPrinter[this.uuid]
}

fun FPlayer.disablePrinter() {
    val player = getPlayer()
    player?.gameMode = GameMode.SURVIVAL
    val inventory = player?.inventory ?: return
    inventory.clear()
    player.clearArmorSlots()
    val data = PrinterData.inPrinter[this.uuid] ?: return
    if (data.cachedInventory != null) inventory.contents = data.cachedInventory.contents
    PrinterConfig.commandPrinterSummary
            .forEach { line -> message(line.replace("{blocks-placed}", data.blocksPlaced?.size.toString()).replace("{spent}", data.spent.toString())) }
    PrinterData.inPrinter.remove(this.uuid)
    PrinterConfig.printerCommandsToRunOnDisable.forEach { command ->
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.replace("{player}", player.name))
    }
    PrinterData.save(FPrinterAddon.addon)
}


fun Player.clearArmorSlots() {
    val inventory = this.inventory
    val air = ItemStack(Material.AIR)
    inventory.let {
        it.boots = air
        it.leggings = air
        it.chestplate = air
        it.helmet = air
    }
}