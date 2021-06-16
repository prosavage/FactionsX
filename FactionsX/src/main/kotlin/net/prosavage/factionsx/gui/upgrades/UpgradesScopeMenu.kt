package net.prosavage.factionsx.gui.upgrades

import fr.minuskube.inv.ClickableItem
import fr.minuskube.inv.SmartInventory
import fr.minuskube.inv.content.InventoryContents
import fr.minuskube.inv.content.InventoryProvider
import net.prosavage.factionsx.FactionsX
import net.prosavage.factionsx.core.Faction
import net.prosavage.factionsx.persist.config.gui.UpgradesGUIConfig
import net.prosavage.factionsx.upgrade.UpgradeScope
import net.prosavage.factionsx.util.color
import org.bukkit.entity.Player

class UpgradesScopeMenu(val forFaction: Faction) : InventoryProvider {

    companion object {
        fun getInv(forFaction: Faction): SmartInventory? {
            return SmartInventory.builder()
                    .manager(FactionsX.inventoryManager)
                    .id("scope")
                    .provider(UpgradesScopeMenu(forFaction))
                    .size(UpgradesGUIConfig.scopesMenuRows, 9)
                    .title(color(UpgradesGUIConfig.scopesMenuTitle))
                    .build()
        }
    }


    override fun init(player: Player, contents: InventoryContents) {
        contents.fill(ClickableItem.empty(UpgradesGUIConfig.scopesMenuBackgroundItem.buildItem()))

        UpgradesGUIConfig.scopesMenuItems.forEach { entry ->
            contents.set(entry.value.coordinate.row, entry.value.coordinate.column, ClickableItem.of(entry.value.displayItem.buildItem()) {
                when (entry.key) {
                    UpgradeScope.GLOBAL -> UpgradesGlobalMenu.getInv(forFaction).open(player)
                    UpgradeScope.TERRITORY -> UpgradesTerritoryChooseMenu.getInv(forFaction)?.open(player)
                }
            })
        }
    }


}