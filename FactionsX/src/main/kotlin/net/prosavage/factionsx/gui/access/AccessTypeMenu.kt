package net.prosavage.factionsx.gui.access

import fr.minuskube.inv.ClickableItem.empty
import fr.minuskube.inv.ClickableItem.of
import fr.minuskube.inv.InventoryListener
import fr.minuskube.inv.SmartInventory
import fr.minuskube.inv.content.InventoryContents
import fr.minuskube.inv.content.InventoryProvider
import net.prosavage.baseplugin.ItemBuilder
import net.prosavage.factionsx.FactionsX
import net.prosavage.factionsx.core.FPlayer
import net.prosavage.factionsx.core.Faction
import net.prosavage.factionsx.manager.PlayerManager
import net.prosavage.factionsx.persist.config.gui.AccessGUIConfig
import net.prosavage.factionsx.persist.config.gui.AccessGUIConfig.allowed
import net.prosavage.factionsx.persist.config.gui.AccessGUIConfig.disallowed
import net.prosavage.factionsx.persist.config.gui.AccessGUIConfig.typeMenuBackgroundFill
import net.prosavage.factionsx.persist.config.gui.AccessGUIConfig.typeMenuFactionsItem
import net.prosavage.factionsx.persist.config.gui.AccessGUIConfig.typeMenuPlayersItem
import net.prosavage.factionsx.persist.config.gui.AccessGUIConfig.typeMenuRows
import net.prosavage.factionsx.persist.config.gui.AccessGUIConfig.typeMenuTitle
import net.prosavage.factionsx.util.MemberAction
import net.prosavage.factionsx.util.MemberAction.ACCESS_FACTIONS
import net.prosavage.factionsx.util.MemberAction.ACCESS_PLAYERS
import net.prosavage.factionsx.util.SerializableItem
import net.prosavage.factionsx.util.color
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemStack

class AccessTypeMenu(private val faction: Faction): InventoryProvider {
    override fun init(player: Player, contents: InventoryContents) {
        val fPlayer = PlayerManager.getFPlayer(player)
        val factionsItem = typeMenuFactionsItem
        val factionsCoordinate = factionsItem.coordinate
        val playersItem = typeMenuPlayersItem
        val playersCoordinate = playersItem.coordinate

        contents.fill(empty(typeMenuBackgroundFill.buildItem()))

        contents.set(factionsCoordinate.row, factionsCoordinate.column, of(
                processItem(fPlayer, ACCESS_FACTIONS, factionsItem.displayItem)
        ) {
            if (!fPlayer.canDoMemberAction(ACCESS_FACTIONS)) {
                fPlayer.message(AccessGUIConfig.typeMenuNoPermission, ACCESS_FACTIONS.actionName)
                return@of
            }
            AccessChunkMenu.of(faction, true, null).open(player)
        })

        contents.set(playersCoordinate.row, playersCoordinate.column, of(
                processItem(fPlayer, ACCESS_PLAYERS, playersItem.displayItem)
        ) {
            if (!fPlayer.canDoMemberAction(ACCESS_PLAYERS)) {
                fPlayer.message(AccessGUIConfig.typeMenuNoPermission, ACCESS_PLAYERS.actionName)
                return@of
            }
            AccessChunkMenu.of(faction, false, null).open(player)
        })
    }

    private fun processItem(player: FPlayer, action: MemberAction, item: SerializableItem): ItemStack {
        return ItemBuilder(item.buildItem()).lore(item.lore.map { line -> line
                .replace("{ALLOWED_TO_EDIT}", if (player.canDoMemberAction(action)) allowed else disallowed)
        }).build()
    }

    companion object {
        fun of(faction: Faction): SmartInventory {
            return SmartInventory
                    .builder()
                    .manager(FactionsX.inventoryManager)
                    .provider(AccessTypeMenu(faction))
                    .size(typeMenuRows, 9)
                    .title(color(typeMenuTitle))
                    .listener(InventoryListener(InventoryClickEvent::class.java) { event ->
                        event.isCancelled = true
                    })
                    .build()
        }
    }
}