package net.prosavage.factionsx.gui.upgrades

import fr.minuskube.inv.ClickableItem
import fr.minuskube.inv.SmartInventory
import fr.minuskube.inv.content.InventoryContents
import fr.minuskube.inv.content.InventoryProvider
import fr.minuskube.inv.content.SlotIterator
import net.prosavage.baseplugin.ItemBuilder
import net.prosavage.factionsx.FactionsX
import net.prosavage.factionsx.core.Faction
import net.prosavage.factionsx.manager.PlayerManager
import net.prosavage.factionsx.manager.UpgradeManager
import net.prosavage.factionsx.persist.Message
import net.prosavage.factionsx.persist.config.gui.UpgradesGUIConfig
import net.prosavage.factionsx.persist.data.FLocation
import net.prosavage.factionsx.upgrade.Upgrade
import net.prosavage.factionsx.upgrade.UpgradeScope
import net.prosavage.factionsx.util.color
import org.bukkit.entity.Player

class UpgradesTerritoryManageMenu(val forFaction: Faction, val fLocation: FLocation) : InventoryProvider {

    companion object {
        fun getInv(forFaction: Faction, fLocation: FLocation): SmartInventory? {
            return SmartInventory.builder()
                    .manager(FactionsX.inventoryManager)
                    .id("manage")
                    .provider(UpgradesTerritoryManageMenu(forFaction, fLocation))
                    .size(UpgradesGUIConfig.territoryManageMenuRows, 9)
                    .title(color(UpgradesGUIConfig.territoryManageMenuTitle))
                    .build()
        }
    }

    override fun init(player: Player, contents: InventoryContents) {
        contents.fill(ClickableItem.empty(UpgradesGUIConfig.territoryManageMenuBackgroundItem.buildItem()))
        val fPlayer = PlayerManager.getFPlayer(player)


        val territoryManageMenuNicknameItem = UpgradesGUIConfig.territoryManageMenuNicknameItem
        contents.set(territoryManageMenuNicknameItem.coordinate.row, territoryManageMenuNicknameItem.coordinate.column,
                ClickableItem.of(territoryManageMenuNicknameItem.displayItem.buildItem()) {
                    player.closeInventory()
                    fPlayer.namingClaim = fLocation
                    fPlayer.message(Message.commandUpgradeClaimRenameMessage)
                })

        val territoryManagemenuIconItem = UpgradesGUIConfig.territoryManageMenuIconItem
        contents.set(territoryManagemenuIconItem.coordinate.row, territoryManagemenuIconItem.coordinate.column,
                ClickableItem.of(territoryManagemenuIconItem.displayItem.buildItem()) {
                    player.closeInventory()
                    fPlayer.assigningClaimIcon = fLocation
                    fPlayer.message(Message.commandUpgradeClaimIconAssignMessage)
                })

        val territoryManageMenuCurrentItem = UpgradesGUIConfig.territoryManageMenuCurrentItem
        contents.set(territoryManageMenuCurrentItem.coordinate.row, territoryManageMenuCurrentItem.coordinate.column,
                ClickableItem.empty(
                        ItemBuilder(fLocation.icon.parseItem())
                                .name(fLocation.name)
                                .amount(territoryManageMenuCurrentItem.displayItem.amt)
                                .lore(formatLore(fLocation, territoryManageMenuCurrentItem.displayItem.lore)).build()))

        val pagination = contents.pagination()
        val upgradeItems = mutableListOf<ClickableItem>()


        UpgradeManager.getUpgrades(UpgradeScope.TERRITORY).forEach { upgrade ->
            val level = fLocation.getUpgrade(upgrade)
            val canLevelUp = upgrade.canLevelUp(level + 1)
            val loreToUse: List<String> = if (canLevelUp) upgrade.item.lore else upgrade.maxLevelItemLore
            upgradeItems.add(ClickableItem.of(
                    ItemBuilder(upgrade.item.buildItem()).lore(formatUpgradeLore(upgrade, fLocation, loreToUse)).amount(if (level == 0) 1 else level).glowing(canLevelUp).build()
            ) {
                if (canLevelUp) {
                    upgrade.executeLevelUp(forFaction, fLocation, fPlayer)
                    getInv(forFaction, fLocation)?.open(player)

                }
            })
        }

        pagination.setItems(*upgradeItems.toTypedArray())
        pagination.setItemsPerPage(UpgradesGUIConfig.territoryManageMenuItemsPerPage)
        pagination.addToIterator(contents.newIterator(SlotIterator.Type.HORIZONTAL, UpgradesGUIConfig.territoryManageMenuPageStartCoordinate.row, UpgradesGUIConfig.territoryManageMenuPageStartCoordinate.column))


        val nextPageButtonItem = UpgradesGUIConfig.territoryManageMenuNextPageItem
        contents.set(nextPageButtonItem.coordinate.row, nextPageButtonItem.coordinate.column,
                ClickableItem.of(nextPageButtonItem.displayItem.buildItem()) {
                    UpgradesTerritoryChooseMenu.getInv(forFaction)?.open(player, pagination.next().page)
                })

        val previousPageButtonItem = UpgradesGUIConfig.territoryManageMenuPreviousPageItem
        contents.set(previousPageButtonItem.coordinate.row, previousPageButtonItem.coordinate.column,
                ClickableItem.of(previousPageButtonItem.displayItem.buildItem()) {
                    UpgradesTerritoryChooseMenu.getInv(forFaction)?.open(player, pagination.previous().page)
                })

        UpgradesGUIConfig.territoryManageMenuSpecialItems.forEach { commandItem ->
            contents.set(commandItem.item.coordinate.row, commandItem.item.coordinate.column, ClickableItem.of(commandItem.item.displayItem.buildItem()) {
                commandItem.commandsToRunAsPlayer.forEach { command -> player.chat(command) }
            })
        }


    }

    private fun formatUpgradeLore(upgrade: Upgrade, fLocation: FLocation, lore: List<String>): List<String> {
        val level = fLocation.getUpgrade(upgrade)
        val costForLevel = upgrade.getCostForLevel(level + 1)
        return lore.map { line ->
            line
                    .replace("{cost}", costForLevel?.toString() ?: "N/A")
                    .replace("{level}", level.toString())
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