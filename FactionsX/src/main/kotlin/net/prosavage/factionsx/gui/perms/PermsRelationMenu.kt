package net.prosavage.factionsx.gui.perms

import com.cryptomorin.xseries.XMaterial
import fr.minuskube.inv.ClickableItem
import fr.minuskube.inv.SmartInventory
import fr.minuskube.inv.content.InventoryContents
import fr.minuskube.inv.content.InventoryProvider
import net.prosavage.baseplugin.ItemBuilder
import net.prosavage.factionsx.FactionsX
import net.prosavage.factionsx.core.Faction
import net.prosavage.factionsx.manager.PlayerManager
import net.prosavage.factionsx.persist.Message
import net.prosavage.factionsx.persist.config.gui.PermsGUIConfig
import net.prosavage.factionsx.util.Relation
import net.prosavage.factionsx.util.color
import org.bukkit.entity.Player

class PermsRelationMenu(val forFaction: Faction, val relation: Relation) : InventoryProvider {
    companion object {
        fun getInv(forFaction: Faction, relation: Relation): SmartInventory? {
            return SmartInventory.builder()
                    .manager(FactionsX.inventoryManager)
                    .id("relation")
                    .provider(PermsRelationMenu(forFaction, relation))
                    .size(PermsGUIConfig.relationMenuRows, 9)
                    .title(color(PermsGUIConfig.relationMenuTitle.replace("{relation}", relation.tagReplacement)))
                    .build()
        }
    }

    override fun init(player: Player, contents: InventoryContents) {
        contents.fill(ClickableItem.empty(PermsGUIConfig.relationMenuBackgroundItem.buildItem()))
        for ((playerAction, item) in PermsGUIConfig.relationMenuItems) {
            if (item.hide || item.displayItem.material === XMaterial.AIR) continue

            val permForRelation = forFaction.relationPerms.getPermForRelation(relation, playerAction)
            val fPlayer = PlayerManager.getFPlayer(player)

            contents.set(item.coordinate.row, item.coordinate.column, ClickableItem.of(ItemBuilder(item.displayItem.buildItem()).lore(formatLore(playerAction.isLocked(relation), permForRelation, item.displayItem.lore)).glowing(permForRelation).build()) {
                if (playerAction.isLocked(relation)) {
                    fPlayer.message(Message.commandPermsRelationOverriden, forFaction.relationPerms.getPermForRelation(relation, playerAction).toString())
                    return@of
                }
                val newStatus = !permForRelation
                forFaction.relationPerms.setPermForRelation(relation, playerAction, newStatus)
                getInv(forFaction, relation)?.open(player)
                fPlayer.message(Message.commandPermsRelationSuccess, playerAction.name, newStatus.toString(), relation.name)
            })
        }
        PermsGUIConfig.relationMenuSpecialItems.forEach { commandItem ->
            contents.set(commandItem.item.coordinate.row, commandItem.item.coordinate.column, ClickableItem.of(commandItem.item.displayItem.buildItem()) {
                commandItem.commandsToRunAsPlayer.forEach { command -> player.chat(command) }
            })
        }
    }

    private fun formatLore(locked: Boolean, status: Boolean, lore: List<String>): List<String> {
        return lore.map { line -> line.replace("{status}", if (locked) PermsGUIConfig.permsMenuStatusPlaceholderLocked else if (status) PermsGUIConfig.permsMenuStatusPlaceholderTrue else PermsGUIConfig.permsMenuStatusPlaceholderFalse) }
    }
}