package net.prosavage.factionsx.gui.access

import fr.minuskube.inv.ClickableItem
import fr.minuskube.inv.ClickableItem.empty
import fr.minuskube.inv.ClickableItem.of
import fr.minuskube.inv.InventoryListener
import fr.minuskube.inv.SmartInventory
import fr.minuskube.inv.content.InventoryContents
import fr.minuskube.inv.content.InventoryProvider
import fr.minuskube.inv.content.SlotIterator.Type.HORIZONTAL
import net.prosavage.baseplugin.ItemBuilder
import net.prosavage.factionsx.FactionsX
import net.prosavage.factionsx.core.FPlayer
import net.prosavage.factionsx.core.Faction
import net.prosavage.factionsx.manager.FactionManager
import net.prosavage.factionsx.manager.PlaceholderManager.processPlaceholders
import net.prosavage.factionsx.manager.PlayerManager
import net.prosavage.factionsx.persist.config.gui.AccessGUIConfig.objectsMenuBackgroundFill
import net.prosavage.factionsx.persist.config.gui.AccessGUIConfig.objectsMenuItemsPerPage
import net.prosavage.factionsx.persist.config.gui.AccessGUIConfig.objectsMenuLoreFactions
import net.prosavage.factionsx.persist.config.gui.AccessGUIConfig.objectsMenuLorePlayers
import net.prosavage.factionsx.persist.config.gui.AccessGUIConfig.objectsMenuMaterialFactions
import net.prosavage.factionsx.persist.config.gui.AccessGUIConfig.objectsMenuMaterialPlayers
import net.prosavage.factionsx.persist.config.gui.AccessGUIConfig.objectsMenuName
import net.prosavage.factionsx.persist.config.gui.AccessGUIConfig.objectsMenuNextButton
import net.prosavage.factionsx.persist.config.gui.AccessGUIConfig.objectsMenuPlayersNoFaction
import net.prosavage.factionsx.persist.config.gui.AccessGUIConfig.objectsMenuPlayersNoLeader
import net.prosavage.factionsx.persist.config.gui.AccessGUIConfig.objectsMenuPreviousButton
import net.prosavage.factionsx.persist.config.gui.AccessGUIConfig.objectsMenuRows
import net.prosavage.factionsx.persist.config.gui.AccessGUIConfig.objectsMenuStartCoordinate
import net.prosavage.factionsx.persist.config.gui.AccessGUIConfig.objectsMenuTitleFactions
import net.prosavage.factionsx.persist.config.gui.AccessGUIConfig.objectsMenuTitlePlayers
import net.prosavage.factionsx.persist.data.FLocation
import net.prosavage.factionsx.persist.data.Players
import net.prosavage.factionsx.util.preparePagination
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent

class AccessObjectMenu(private val claim: FLocation, private val isFactions: Boolean): InventoryProvider {
    override fun init(player: Player, contents: InventoryContents) {
        contents.fill(empty(objectsMenuBackgroundFill.buildItem()))

        val fPlayer = PlayerManager.getFPlayer(player)
        val claimItems = arrayListOf<ClickableItem>()

        if (isFactions) {
            for (faction in FactionManager.getFactions().filter { !it.isSystemFaction() && it != fPlayer.getFaction() }) {
                claimItems += buildFactionsItem(player, faction)
            }
        } else {
            for (factionPlayer in Players.fplayers.values.filter { it != fPlayer && it.getPlayer()?.hasMetadata("NPC") == false }) {
                claimItems += buildPlayersItem(player, factionPlayer)
            }
        }

        val pagination = contents.pagination()
        preparePagination(
                pagination,
                contents,
                objectsMenuStartCoordinate,
                objectsMenuItemsPerPage,
                objectsMenuNextButton,
                objectsMenuPreviousButton,
                HORIZONTAL,
                claimItems,
                { of(claim, isFactions).open(player, pagination.next().page) },
                { of(claim, isFactions).open(player, pagination.previous().page) }
        )
    }

    private fun buildFactionsItem(player: Player, faction: Faction): ClickableItem {
        return of(ItemBuilder(objectsMenuMaterialFactions.parseItem())
                .name(objectsMenuName.replace("{NAME}", faction.tag))
                .lore(objectsMenuLoreFactions.map { line ->
                    if (!line.contains('{') || !line.contains('}')) return@map line
                    processPlaceholders(null, faction, line)
                })
                .build()
        ) { AccessModificationMenu.of(claim, faction).open(player) }
    }

    private fun buildPlayersItem(player: Player, fPlayer: FPlayer): ClickableItem {
        return of(ItemBuilder(objectsMenuMaterialPlayers.parseItem())
                .name(objectsMenuName.replace("{NAME}", fPlayer.name))
                .lore(objectsMenuLorePlayers.map { line ->
                    if (!this.hasPlaceholder(line)) return@map line
                    val faction = fPlayer.getFaction()
                    line
                            .replace("{faction}", if (!faction.isWilderness()) faction.tag else objectsMenuPlayersNoFaction)
                            .replace("{leader}", if (!faction.isWilderness()) faction.getLeader()?.name ?: objectsMenuPlayersNoLeader else objectsMenuPlayersNoLeader)
                            .replace("{tag}", fPlayer.name)
                })
                .build()
        ) { AccessModificationMenu.of(claim, fPlayer).open(player) }
    }

    private fun hasPlaceholder(line: String): Boolean = line.contains('{') && line.contains('}')

    companion object {
        fun of(claim: FLocation, isFactions: Boolean): SmartInventory {
            return SmartInventory
                    .builder()
                    .manager(FactionsX.inventoryManager)
                    .provider(AccessObjectMenu(claim, isFactions))
                    .size(objectsMenuRows, 9)
                    .title(if (isFactions) objectsMenuTitleFactions else objectsMenuTitlePlayers)
                    .listener(InventoryListener(InventoryClickEvent::class.java) { event ->
                        event.isCancelled = true
                    })
                    .build()
        }
    }
}