package net.prosavage.factionsx.gui.access

import com.cryptomorin.xseries.XMaterial
import fr.minuskube.inv.ClickableItem
import fr.minuskube.inv.ClickableItem.empty
import fr.minuskube.inv.InventoryListener
import fr.minuskube.inv.SmartInventory
import fr.minuskube.inv.content.InventoryContents
import fr.minuskube.inv.content.InventoryProvider
import fr.minuskube.inv.content.Pagination
import fr.minuskube.inv.content.SlotIterator.Type.HORIZONTAL
import net.prosavage.factionsx.FactionsX
import net.prosavage.factionsx.core.FPlayer
import net.prosavage.factionsx.core.Faction
import net.prosavage.factionsx.persist.config.gui.AccessGUIConfig
import net.prosavage.factionsx.persist.config.gui.AccessGUIConfig.disallowed
import net.prosavage.factionsx.persist.config.gui.AccessGUIConfig.modificationMenuActionLore
import net.prosavage.factionsx.persist.config.gui.AccessGUIConfig.modificationMenuActionName
import net.prosavage.factionsx.persist.config.gui.AccessGUIConfig.modificationMenuBackgroundFill
import net.prosavage.factionsx.persist.config.gui.AccessGUIConfig.modificationMenuItemsPerPage
import net.prosavage.factionsx.persist.config.gui.AccessGUIConfig.modificationMenuNextButton
import net.prosavage.factionsx.persist.config.gui.AccessGUIConfig.modificationMenuPreviousButton
import net.prosavage.factionsx.persist.config.gui.AccessGUIConfig.modificationMenuRows
import net.prosavage.factionsx.persist.config.gui.AccessGUIConfig.modificationMenuStartCoordinate
import net.prosavage.factionsx.persist.config.gui.AccessGUIConfig.modificationMenuTitle
import net.prosavage.factionsx.persist.data.FLocation
import net.prosavage.factionsx.util.PlayerAction
import net.prosavage.factionsx.util.SerializableItem
import net.prosavage.factionsx.util.preparePagination
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent

class AccessModificationMenu(private val claim: FLocation, private val theObject: Any) : InventoryProvider {
    private val isFaction = theObject is Faction

    override fun init(player: Player, contents: InventoryContents) {
        contents.fill(empty(modificationMenuBackgroundFill.buildItem()))

        val pagination = contents.pagination()
        val actionItems = arrayListOf<ClickableItem>()

        for (action in PlayerAction.values()) {
            if (action.icon === XMaterial.AIR) continue
            actionItems += buildClickableItem(player, action, pagination)
        }

        preparePagination(
                pagination,
                contents,
                modificationMenuStartCoordinate,
                modificationMenuItemsPerPage,
                modificationMenuNextButton,
                modificationMenuPreviousButton,
                HORIZONTAL,
                actionItems,
                { of(claim, theObject).open(player, pagination.next().page) },
                { of(claim, theObject).open(player, pagination.previous().page) }
        )
    }

    private fun buildClickableItem(player: Player, action: PlayerAction, pagination: Pagination): ClickableItem {
        val allowed = when (isFaction) {
            true -> claim.getAccessPoint().canPerform(theObject as Faction, action)
            false -> claim.getAccessPoint().canPerform(theObject as FPlayer, action)
        }
        val item = SerializableItem(action.icon, modificationMenuActionName.replace("{NAME}", action.actionName.replace("_", " ").capitalize()),
                modificationMenuActionLore.map { line ->
                    line.replace("{ALLOWED}", if (allowed) AccessGUIConfig.allowed else disallowed)
                },
                1
        )

        return ClickableItem.of(item.buildItem(allowed)) {
            if (isFaction) claim.getAccessPoint().opposite(theObject as Faction, action)
            else claim.getAccessPoint().opposite(theObject as FPlayer, action)
            of(claim, theObject).open(player, pagination.page)
        }

    }

    companion object {
        fun of(claim: FLocation, theObject: Any): SmartInventory {
            return SmartInventory
                    .builder()
                    .manager(FactionsX.inventoryManager)
                    .provider(AccessModificationMenu(claim, theObject))
                    .size(modificationMenuRows, 9)
                    .title(modificationMenuTitle.replace("{NAME}", (theObject as? Faction)?.tag
                            ?: (theObject as? FPlayer)?.name ?: "Unknown"))
                    .listener(InventoryListener(InventoryClickEvent::class.java) { event ->
                        event.isCancelled = true
                    })
                    .build()
        }
    }
}