package net.prosavage.factionsx.gui.upgrades

import fr.minuskube.inv.ClickableItem
import fr.minuskube.inv.SmartInventory
import fr.minuskube.inv.content.InventoryContents
import fr.minuskube.inv.content.InventoryProvider
import fr.minuskube.inv.content.SlotIterator
import net.prosavage.baseplugin.ItemBuilder
import net.prosavage.factionsx.FactionsX
import net.prosavage.factionsx.core.Faction
import net.prosavage.factionsx.manager.GridManager
import net.prosavage.factionsx.manager.PlayerManager
import net.prosavage.factionsx.persist.Message
import net.prosavage.factionsx.persist.config.gui.UpgradesGUIConfig
import net.prosavage.factionsx.persist.data.FLocation
import net.prosavage.factionsx.persist.data.getFLocation
import net.prosavage.factionsx.util.color
import org.bukkit.entity.Player

class UpgradesTerritoryChooseMenu(val forFaction: Faction) : InventoryProvider {
    companion object {
        fun getInv(forFaction: Faction): SmartInventory? {
            return SmartInventory.builder()
                    .manager(FactionsX.inventoryManager)
                    .id("choose-menu")
                    .provider(UpgradesTerritoryChooseMenu(forFaction))
                    .size(UpgradesGUIConfig.territoryChooseMenuRows, 9)
                    .title(color(UpgradesGUIConfig.territoryChooseMenuTitle))
                    .build()
        }
    }


    override fun init(player: Player, contents: InventoryContents) {
        contents.fill(ClickableItem.empty(UpgradesGUIConfig.territoryChooseMenuBackgroundItem.buildItem()))
        val fPlayer = PlayerManager.getFPlayer(player)

        val territoryChooseMenuChunkHereItem = UpgradesGUIConfig.territoryChooseMenuChunkHereItem
        val hereFloc = getFLocation(player.location)
        contents.set(territoryChooseMenuChunkHereItem.coordinate.row, territoryChooseMenuChunkHereItem.coordinate.column,
                ClickableItem.of(
                        ItemBuilder(territoryChooseMenuChunkHereItem.displayItem.buildItem())
                                .lore(formatLore(hereFloc, territoryChooseMenuChunkHereItem.displayItem.lore))
                                .build()) {
                    val factionAt = hereFloc.getFaction()
                    if (factionAt != forFaction) {
                        fPlayer.message(Message.commandUpgradeTerritoryNotYours)
                        player.closeInventory()
                    } else {
                        UpgradesTerritoryManageMenu.getInv(forFaction, hereFloc)?.open(player)
                    }

                })

        val pagination = contents.pagination()
        val allClaimItems = mutableListOf<ClickableItem>()
        val territoryItemFormat = UpgradesGUIConfig.territoryItemFormat

        val allClaims = GridManager.getAllClaims(forFaction)
        allClaims.forEach { claim ->
            allClaimItems.add(ClickableItem.of(ItemBuilder(claim.icon.parseItem()).name(claim.name).amount(territoryItemFormat.amt).lore(formatLore(claim, territoryItemFormat.lore)).build()) {
                UpgradesTerritoryManageMenu.getInv(forFaction, claim)?.open(player)
            })
        }

        pagination.setItems(*allClaimItems.toTypedArray())
        pagination.setItemsPerPage(UpgradesGUIConfig.territoryBrowserItemsPerPage)
        pagination.addToIterator(contents.newIterator(SlotIterator.Type.HORIZONTAL, UpgradesGUIConfig.territoryBrowserStartCoordinate.row, UpgradesGUIConfig.territoryBrowserStartCoordinate.column))


        val nextPageButtonItem = UpgradesGUIConfig.territoryBrowserNextPageItem
        contents.set(nextPageButtonItem.coordinate.row, nextPageButtonItem.coordinate.column,
                ClickableItem.of(nextPageButtonItem.displayItem.buildItem()) {
                    getInv(forFaction)?.open(player, pagination.next().page)
                })

        val previousPageButtonItem = UpgradesGUIConfig.territoryBrowserPreviousPageItem
        contents.set(previousPageButtonItem.coordinate.row, previousPageButtonItem.coordinate.column,
                ClickableItem.of(previousPageButtonItem.displayItem.buildItem()) {
                    getInv(forFaction)?.open(player, pagination.previous().page)
                })

        UpgradesGUIConfig.territoryBrowserSpecialItems.forEach { commandItem ->
            contents.set(commandItem.item.coordinate.row, commandItem.item.coordinate.column, ClickableItem.of(commandItem.item.displayItem.buildItem()) {
                commandItem.commandsToRunAsPlayer.forEach { command -> player.chat(command) }
            })
        }


    }


    private fun formatLore(flocation: FLocation, lore: List<String>): List<String> {
        return lore.map { line ->
            line
                    .replace("{world}", flocation.world)
                    .replace("{x}", flocation.x.toString())
                    .replace("{z}", flocation.z.toString())
        }
    }

}