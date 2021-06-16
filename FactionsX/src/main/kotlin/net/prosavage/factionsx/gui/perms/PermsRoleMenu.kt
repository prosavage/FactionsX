package net.prosavage.factionsx.gui.perms

import com.cryptomorin.xseries.XMaterial
import fr.minuskube.inv.ClickableItem
import fr.minuskube.inv.SmartInventory
import fr.minuskube.inv.content.InventoryContents
import fr.minuskube.inv.content.InventoryProvider
import fr.minuskube.inv.content.SlotIterator
import net.prosavage.baseplugin.ItemBuilder
import net.prosavage.factionsx.FactionsX
import net.prosavage.factionsx.core.CustomRole
import net.prosavage.factionsx.core.Faction
import net.prosavage.factionsx.manager.PlayerManager
import net.prosavage.factionsx.manager.SpecialActionManager
import net.prosavage.factionsx.persist.Message
import net.prosavage.factionsx.persist.config.gui.PermsGUIConfig
import net.prosavage.factionsx.util.color
import org.bukkit.entity.Player

class PermsRoleMenu(val forFaction: Faction, val role: CustomRole) : InventoryProvider {


    companion object {
        fun getInv(forFaction: Faction, role: CustomRole): SmartInventory? {
            return SmartInventory.builder()
                    .manager(FactionsX.inventoryManager)
                    .id("role")
                    .provider(PermsRoleMenu(forFaction, role))
                    .size(PermsGUIConfig.roleMenuRows, 9)
                    .title(color(PermsGUIConfig.roleMenuTitle.replace("{role}", role.roleTag)))
                    .build()
        }
    }

    override fun init(player: Player, contents: InventoryContents) {
        contents.fill(ClickableItem.empty(PermsGUIConfig.roleMenuBackgroundItem.buildItem()))
        val fPlayer = PlayerManager.getFPlayer(player)


        val pagination = contents.pagination()
//        val permissionItems = arrayOfNulls<ClickableItem>(PermsGUIConfig.roleMenuPlayerActionItems.size + PermsGUIConfig.roleMenuMemberActionItems.size + SpecialActionManager.getAllRegisteredActions().size)
        val permissionItems = mutableListOf<ClickableItem>()

        PermsGUIConfig.roleMenuPlayerActionItems.forEach { (playerAction, item) ->
            if (item.hide || item.item.material === XMaterial.AIR) return@forEach

            val permForRole = role.allowedPlayerActions.contains(playerAction)
            val clickableItem = ClickableItem.of(ItemBuilder(item.item.buildItem()).lore(formatLore(permForRole, item.item.lore)).glowing(permForRole).build()) {
                val newStatus = !permForRole
                if (newStatus) role.allowedPlayerActions.add(playerAction) else role.allowedPlayerActions.remove(playerAction)
                getInv(forFaction, role)?.open(player, pagination.page)
                fPlayer.message(Message.commandPermsRelationSuccess, playerAction.name, newStatus.toString(), role.roleTag)
            }

            permissionItems.add(clickableItem)
//            contents.set(item.coordinate.row, item.coordinate.column, clickableItem)
        }

        PermsGUIConfig.roleMenuMemberActionItems.forEach { (memberAction, item) ->
            if (item.hide || item.item.material === XMaterial.AIR) return@forEach

            val permForRole = role.allowedMemberActions.contains(memberAction)
            val clickableItem = ClickableItem.of(ItemBuilder(item.item.buildItem()).lore(formatLore(permForRole, item.item.lore)).glowing(permForRole).build()) {
                val newStatus = !permForRole
                if (newStatus) role.allowedMemberActions.add(memberAction) else role.allowedMemberActions.remove(memberAction)
                getInv(forFaction, role)?.open(player, pagination.page)
                fPlayer.message(Message.commandPermsRoleSuccess, if (newStatus) Message.commandPermsRoleAdded else Message.commandPermsRoleRemoved, role.roleTag, memberAction.actionName)
            }

            permissionItems.add(clickableItem)
//            contents.set(item.coordinate.row, item.coordinate.column, clickableItem)
        }

        SpecialActionManager.getAllRegisteredActions().forEach { action ->
            val permForAction = role.canDoSpecialAction(action)
            val clickableItem = ClickableItem.of(ItemBuilder(action.item.buildItem()).lore(formatLore(permForAction, action.item.lore)).glowing(permForAction).build()) {
                val newStatus = !permForAction
                role.specialActions[action.name] = newStatus
                getInv(forFaction, role)?.open(player, pagination.page)
                fPlayer.message(Message.commandPermsRoleSuccess, if (newStatus) Message.commandPermsRoleAdded else Message.commandPermsRoleRemoved, role.roleTag, action.name)
            }
            permissionItems.add(clickableItem)
//            contents.set(action.interfaceItem.coordinate.row, action.interfaceItem.coordinate.column, clickableItem)
        }

        PermsGUIConfig.roleMenuSpecialItems.forEach { commandItem ->
            contents.set(commandItem.item.coordinate.row, commandItem.item.coordinate.column, ClickableItem.of(commandItem.item.displayItem.buildItem()) {
                commandItem.commandsToRunAsPlayer.forEach { command -> player.chat(command) }
            })
        }

        pagination.setItems(*permissionItems.toTypedArray())
        pagination.setItemsPerPage(PermsGUIConfig.roleMenuItemsPerPage)
        pagination.addToIterator(contents.newIterator(SlotIterator.Type.HORIZONTAL, PermsGUIConfig.roleMenuStartCoordinate.row, PermsGUIConfig.roleMenuStartCoordinate.column))

        val roleInterfaceNextPageButtonItem = PermsGUIConfig.roleMenuNextPageItem
        contents.set(roleInterfaceNextPageButtonItem.coordinate.row, roleInterfaceNextPageButtonItem.coordinate.column,
                ClickableItem.of(roleInterfaceNextPageButtonItem.displayItem.buildItem()) {
                    getInv(forFaction, role)?.open(player, pagination.next().page)
                })

        val roleInterfacePreviousPageButtonItem = PermsGUIConfig.roleMenuPreviousPageItem
        contents.set(roleInterfacePreviousPageButtonItem.coordinate.row, roleInterfacePreviousPageButtonItem.coordinate.column,
                ClickableItem.of(roleInterfacePreviousPageButtonItem.displayItem.buildItem()) {
                    getInv(forFaction, role)?.open(player, pagination.previous().page)
                })
    }

    private fun formatLore(status: Boolean, lore: List<String>): List<String> {
        return lore.map { line -> line.replace("{status}", if (status) PermsGUIConfig.permsMenuStatusPlaceholderTrue else PermsGUIConfig.permsMenuStatusPlaceholderFalse) }
    }

}