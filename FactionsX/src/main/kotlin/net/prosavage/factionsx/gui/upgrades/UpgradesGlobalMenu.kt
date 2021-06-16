package net.prosavage.factionsx.gui.upgrades

import fr.minuskube.inv.ClickableItem
import fr.minuskube.inv.SmartInventory
import fr.minuskube.inv.content.InventoryContents
import fr.minuskube.inv.content.InventoryProvider
import fr.minuskube.inv.content.SlotIterator
import net.prosavage.baseplugin.ItemBuilder
import net.prosavage.factionsx.FactionsX
import net.prosavage.factionsx.core.Faction
import net.prosavage.factionsx.manager.UpgradeManager
import net.prosavage.factionsx.persist.config.gui.UpgradesGUIConfig
import net.prosavage.factionsx.persist.data.getFLocation
import net.prosavage.factionsx.upgrade.Upgrade
import net.prosavage.factionsx.upgrade.UpgradeScope
import net.prosavage.factionsx.util.color
import net.prosavage.factionsx.util.getFPlayer
import org.bukkit.entity.Player
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.*

class UpgradesGlobalMenu(val forFaction: Faction) : InventoryProvider {
    private val costFormat by lazy {
        val format = NumberFormat.getInstance(Locale.US) as DecimalFormat
        format.applyPattern("#,###.##"); format
    }

    companion object {
        fun getInv(forFaction: Faction): SmartInventory {
            return SmartInventory.builder()
                    .manager(FactionsX.inventoryManager)
                    .id("global")
                    .provider(UpgradesGlobalMenu(forFaction))
                    .size(UpgradesGUIConfig.globalUpgradesRows, 9)
                    .title(color(UpgradesGUIConfig.globalUpgradesTitle))
                    .build()
        }
    }


    override fun init(player: Player, contents: InventoryContents) {
        contents.fill(ClickableItem.empty(UpgradesGUIConfig.globalUpgradesBackgroundItem.buildItem()))
        val pagination = contents.pagination()
        val upgradeItems = mutableListOf<ClickableItem>()
        UpgradeManager.getUpgrades(UpgradeScope.GLOBAL).forEach { upgrade ->
            val level = forFaction.getUpgrade(upgrade)
            val canLevelUp = upgrade.canLevelUp(level + 1)
            val loreToUse: List<String> = if (canLevelUp) upgrade.item.lore else upgrade.maxLevelItemLore
            upgradeItems.add(ClickableItem.of(ItemBuilder(
                    upgrade.item.buildItem()
            )
                    .lore(formatUpgradeLore(upgrade, forFaction, loreToUse)).amount(if (level == 0) 1 else level).glowing(canLevelUp)
                    .build()) {
                if (canLevelUp) {
                    upgrade.executeLevelUp(forFaction, getFLocation(player.location), player.getFPlayer())
                    getInv(forFaction).open(player)

                }
            })
        }

        pagination.setItems(*upgradeItems.toTypedArray())
        pagination.setItemsPerPage(UpgradesGUIConfig.globalUpgradesItemsPerPage)
        pagination.addToIterator(contents.newIterator(SlotIterator.Type.HORIZONTAL, UpgradesGUIConfig.globalUpgradesStartCoordinate.row, UpgradesGUIConfig.globalUpgradesStartCoordinate.column))

        val nextPageButtonItem = UpgradesGUIConfig.globalNextPageItem
        if (!nextPageButtonItem.hide) {
            contents.set(nextPageButtonItem.coordinate.row, nextPageButtonItem.coordinate.column,
                    ClickableItem.of(nextPageButtonItem.displayItem.buildItem()) {
                        getInv(forFaction).open(player, pagination.next().page)
                    })
        }

        val previousPageButtonItem = UpgradesGUIConfig.globalPageItem
        if (!previousPageButtonItem.hide) {
            contents.set(previousPageButtonItem.coordinate.row, previousPageButtonItem.coordinate.column,
                    ClickableItem.of(previousPageButtonItem.displayItem.buildItem()) {
                        getInv(forFaction).open(player, pagination.previous().page)
                    })
        }

        UpgradesGUIConfig.globalMenuSpecialItems.forEach { commandItem ->
            contents.set(commandItem.item.coordinate.row, commandItem.item.coordinate.column, ClickableItem.of(commandItem.item.displayItem.buildItem()) {
                commandItem.commandsToRunAsPlayer.forEach { command -> player.chat(command) }
            })
        }
    }


    private fun formatUpgradeLore(upgrade: Upgrade, faction: Faction, lore: List<String>): List<String> {
        val level = faction.getUpgrade(upgrade)
        val costForLevel = upgrade.getCostForLevel(level + 1)
        return lore.map { line ->
            line
                    .replace("{cost}", costFormat.format(costForLevel ?: 0.0))
                    .replace("{level}", level.toString())
        }
    }

}

