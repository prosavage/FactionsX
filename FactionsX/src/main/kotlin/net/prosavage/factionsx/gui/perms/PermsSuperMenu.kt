package net.prosavage.factionsx.gui.perms

import fr.minuskube.inv.ClickableItem
import fr.minuskube.inv.SmartInventory
import fr.minuskube.inv.content.InventoryContents
import fr.minuskube.inv.content.InventoryProvider
import fr.minuskube.inv.content.SlotIterator
import net.prosavage.factionsx.FactionsX
import net.prosavage.factionsx.core.Faction
import net.prosavage.factionsx.persist.color
import net.prosavage.factionsx.persist.config.gui.PermsGUIConfig
import net.prosavage.factionsx.util.SerializableItem
import org.bukkit.entity.Player

class PermsSuperMenu(val forFaction: Faction) : InventoryProvider {

    companion object {
        fun getInv(forFaction: Faction): SmartInventory? {
            return SmartInventory.builder()
                    .manager(FactionsX.inventoryManager)
                    .id("perms")
                    .provider(PermsSuperMenu(forFaction))
                    .size(PermsGUIConfig.mainMenuRows, 9)
                    .title(color(PermsGUIConfig.mainMenuTitle))
                    .build()
        }

    }


    override fun init(player: Player?, contents: InventoryContents?) {
        contents!!.fill(ClickableItem.empty(PermsGUIConfig.mainMenuBackgroundItem.buildItem()))
        PermsGUIConfig.mainMenuRelationItems.forEach { (relation, interfaceItem) ->
            if (interfaceItem.hide) return@forEach
            contents.set(interfaceItem.coordinate.row, interfaceItem.coordinate.column,
                ClickableItem.of(interfaceItem.displayItem.buildItem()) {
                    PermsRelationMenu.getInv(forFaction, relation)?.open(player)
                })
        }

        val pagination = contents.pagination()
        val roleHierarchy = forFaction.factionRoles.roleHierarchy
        val roleItems = arrayOfNulls<ClickableItem>(roleHierarchy.size)
        val roleItemFormat = PermsGUIConfig.mainMenuRoleInterfaceItem
        roleHierarchy.forEach { (index, role) ->
            roleItems[index] = ClickableItem.of(
                SerializableItem(
                    role.iconMaterial,
                    roleItemFormat.name.replace("{role-tag}", role.roleTag),
                    roleItemFormat.lore.map { line -> line.replace("{role-tag}", role.roleTag) },
                    1
                ).buildItem()
            ) {
                PermsRoleMenu.getInv(forFaction, role)?.open(player)
            }
        }
        pagination.setItems(*roleItems)
        pagination.setItemsPerPage(PermsGUIConfig.roleItemsPerPage)

        val roleItemsStartCoordinate = PermsGUIConfig.roleItemsStartCoordinate
        pagination.addToIterator(contents.newIterator(SlotIterator.Type.HORIZONTAL, roleItemsStartCoordinate.row, roleItemsStartCoordinate.column))

        val roleInterfaceNextPageButtonItem = PermsGUIConfig.mainMenuNextPageItem
        if (!roleInterfaceNextPageButtonItem.hide) {
            contents.set(roleInterfaceNextPageButtonItem.coordinate.row, roleInterfaceNextPageButtonItem.coordinate.column,
                ClickableItem.of(roleInterfaceNextPageButtonItem.displayItem.buildItem()) {
                    getInv(forFaction)?.open(player, pagination.next().page)
                })
        }

        val roleInterfacePreviousPageButtonItem = PermsGUIConfig.mainMenuPreviousPageItem
        if (!roleInterfacePreviousPageButtonItem.hide) {
            contents.set(roleInterfacePreviousPageButtonItem.coordinate.row, roleInterfacePreviousPageButtonItem.coordinate.column,
                ClickableItem.of(roleInterfacePreviousPageButtonItem.displayItem.buildItem()) {
                    getInv(forFaction)?.open(player, pagination.previous().page)
                })
        }
    }
}


