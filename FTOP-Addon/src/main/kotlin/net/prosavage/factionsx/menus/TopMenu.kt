package net.prosavage.factionsx.menus

import fr.minuskube.inv.ClickableItem
import fr.minuskube.inv.InventoryListener
import fr.minuskube.inv.content.InventoryContents
import fr.minuskube.inv.content.InventoryProvider
import net.prosavage.factionsx.FTOPAddon
import net.prosavage.factionsx.FTOPAddon.Companion.latestCalc
import net.prosavage.factionsx.FTOPAddon.Companion.task
import net.prosavage.factionsx.FactionsX
import net.prosavage.factionsx.manager.formatMillis
import net.prosavage.factionsx.persist.MenuConfig
import net.prosavage.factionsx.persist.MenuConfig.calculationLatestNextFormat
import net.prosavage.factionsx.persist.MenuConfig.calculationLatestNextNever
import net.prosavage.factionsx.persist.color
import net.prosavage.factionsx.util.SerializableItem
import net.prosavage.factionsx.util.buildMenu
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent

class TopMenu : InventoryProvider {
    /**
     * Initialize content.
     */
    override fun init(player: Player, contents: InventoryContents) {
        MenuConfig.general.forEach { general ->
            val item = general.displayItem
            val (row, column) = general.coordinate
            contents.set(row, column, ClickableItem.of(item.buildItem()) {})
        }

        MenuConfig.items.forEach { item ->
            val entry = FTOPAddon.factionValues.entries.find { it.key == item.index }
            val preciseItem = if (entry != null) MenuConfig.successTopItem.build(entry) else MenuConfig.failedTopItem.buildItem()
            contents.set(item.row, item.column, ClickableItem.of(preciseItem) {})
        }

        this.updateLatestCalculation(contents)
    }

    /**
     * Update content.
     */
    override fun update(player: Player, contents: InventoryContents) = this.updateLatestCalculation(contents)

    /**
     * Update the latest calculation form.
     */
    private fun updateLatestCalculation(contents: InventoryContents) {
        MenuConfig.calculationLatestNextItem.run {
            if (this.hide) return@run
            val (row, column) = this.coordinate

            val latestTime = if (latestCalc > 0) {
                formatMillis(System.currentTimeMillis() - latestCalc, calculationLatestNextFormat)
            } else calculationLatestNextNever

            val nextTime = if (task != null) {
                formatMillis(task!!.getTimeLeft(), calculationLatestNextFormat)
            } else calculationLatestNextNever

            val newItem = with(displayItem) {
                SerializableItem(
                        this.material,
                        this.name.replace("{latest}", latestTime).replace("{next}", nextTime),
                        this.lore.map { it.replace("{latest}", latestTime).replace("{next}", nextTime) },
                        this.amt
                )
            }

            contents.set(row, column, ClickableItem.of(newItem.buildItem()) {})
        }
    }

    /**
     * Global stuff.
     */
    companion object {
        val INVENTORY = buildMenu {
            id("ftop")
            manager(FactionsX.inventoryManager)
            provider(TopMenu())
            size(MenuConfig.rows, 9)
            title(color(MenuConfig.title))
            listener(InventoryListener(InventoryClickEvent::class.java) { event ->
                event.isCancelled = true
            })
        }
    }
}