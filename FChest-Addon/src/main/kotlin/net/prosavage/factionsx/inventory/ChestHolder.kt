package net.prosavage.factionsx.inventory

import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryHolder

object ChestHolder : InventoryHolder {
    // dumb hack
    private val DUMMY = Bukkit.createInventory(null, 9, "Dummy")

    fun onClick(player: Player, slot: Int, rawSlot: Int) {
        player.sendMessage("$slot : $rawSlot")
    }

    // dumb hack
    override fun getInventory(): Inventory = DUMMY
}