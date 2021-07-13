package net.prosavage.factionsx.inventory

import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent

object InventoryListener : Listener {
    @EventHandler
    private fun InventoryClickEvent.onHolder() = (this.clickedInventory?.holder as? ChestHolder)
        ?.onClick(this.whoClicked as Player, this.slot, this.rawSlot)
}