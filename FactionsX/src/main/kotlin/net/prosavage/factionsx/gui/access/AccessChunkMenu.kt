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
import net.prosavage.factionsx.core.Faction
import net.prosavage.factionsx.manager.GridManager
import net.prosavage.factionsx.manager.PlayerManager
import net.prosavage.factionsx.persist.config.gui.AccessGUIConfig.chunkMenuBackgroundFill
import net.prosavage.factionsx.persist.config.gui.AccessGUIConfig.chunkMenuItemsPerPage
import net.prosavage.factionsx.persist.config.gui.AccessGUIConfig.chunkMenuLore
import net.prosavage.factionsx.persist.config.gui.AccessGUIConfig.chunkMenuName
import net.prosavage.factionsx.persist.config.gui.AccessGUIConfig.chunkMenuNextButton
import net.prosavage.factionsx.persist.config.gui.AccessGUIConfig.chunkMenuPreviousButton
import net.prosavage.factionsx.persist.config.gui.AccessGUIConfig.chunkMenuRows
import net.prosavage.factionsx.persist.config.gui.AccessGUIConfig.chunkMenuStartCoordinate
import net.prosavage.factionsx.persist.config.gui.AccessGUIConfig.chunkMenuTitle
import net.prosavage.factionsx.persist.config.gui.AccessGUIConfig.chunkMenuTitleFactions
import net.prosavage.factionsx.persist.config.gui.AccessGUIConfig.chunkMenuTitlePlayers
import net.prosavage.factionsx.persist.data.FLocation
import net.prosavage.factionsx.persist.data.getFLocation
import net.prosavage.factionsx.util.preparePagination
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent

class AccessChunkMenu(private val faction: Faction, private val isFactions: Boolean, private val possibleValue: Any?): InventoryProvider {
    override fun init(player: Player, contents: InventoryContents) {
        contents.fill(empty(chunkMenuBackgroundFill.buildItem()))

        val fPlayer = PlayerManager.getFPlayer(player)
        val claims = GridManager.getAllClaims(faction)
        val claimItems = arrayListOf<ClickableItem>()
        val factionAt = fPlayer.getFactionAt()
        val currentChunk = getFLocation(player.location)

        if (faction == factionAt) {
            claimItems += buildClickable(player, currentChunk)
        }

        for (claim in claims.filter { it != currentChunk }) {
            claimItems += buildClickable(player, claim)
        }

        val pagination = contents.pagination()
        preparePagination(
                pagination,
                contents,
                chunkMenuStartCoordinate,
                chunkMenuItemsPerPage,
                chunkMenuNextButton,
                chunkMenuPreviousButton,
                HORIZONTAL,
                claimItems,
                { of(faction, isFactions, possibleValue).open(player, pagination.next().page) },
                { of(faction, isFactions, possibleValue).open(player, pagination.previous().page) }
        )
    }

    private fun buildClickable(player: Player, claim: FLocation): ClickableItem {
        return of(
            ItemBuilder(claim.icon.parseItem())
                .name(chunkMenuName.replace("{NAME}", claim.name))
                .lore(processLore(claim))
                .amount(1)
                .build()
        ) {
            if (possibleValue == null) {
                AccessObjectMenu.of(claim, isFactions).open(player)
                return@of
            }
            AccessModificationMenu.of(claim, possibleValue).open(player)
        }
    }

    private fun processLore(claim: FLocation): List<String> {
        return chunkMenuLore.map { line -> line
                .replace("{WORLD}", claim.world)
                .replace("{X}", (claim.x * 16).toString())
                .replace("{Z}", (claim.z * 16).toString())
        }
    }

    companion object {
        fun of(faction: Faction, isFactions: Boolean, possibleValue: Any?): SmartInventory {
            return SmartInventory
                    .builder()
                    .manager(FactionsX.inventoryManager)
                    .provider(AccessChunkMenu(faction, isFactions, possibleValue))
                    .size(chunkMenuRows, 9)
                    .title(chunkMenuTitle.format(if (isFactions) chunkMenuTitleFactions else chunkMenuTitlePlayers))
                    .listener(InventoryListener(InventoryClickEvent::class.java) { event ->
                        event.isCancelled = true
                    })
                    .build()
        }
    }
}