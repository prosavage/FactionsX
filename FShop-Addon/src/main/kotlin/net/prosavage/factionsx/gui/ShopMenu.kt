package net.prosavage.factionsx.gui

import fr.minuskube.inv.ClickableItem
import fr.minuskube.inv.SmartInventory
import fr.minuskube.inv.content.InventoryContents
import fr.minuskube.inv.content.InventoryProvider
import fr.minuskube.inv.content.SlotPos
import net.prosavage.factionsx.FactionsX
import net.prosavage.factionsx.core.Faction
import net.prosavage.factionsx.persist.ShopConfig
import net.prosavage.factionsx.util.color
import net.prosavage.factionsx.util.getFPlayer
import org.bukkit.entity.Player
import java.util.*

class ShopMenu(val forFaction: Faction) : InventoryProvider {

    companion object {
        fun getInv(forFaction: Faction): SmartInventory? {
            return SmartInventory.builder()
                    .manager(FactionsX.inventoryManager)
                    .id(UUID.randomUUID().toString())
                    .provider(ShopMenu(forFaction))
                    .size(ShopConfig.guiRows, 9)
                    .title(color(ShopConfig.guiTitle))
                    .build()
        }
    }


    override fun init(player: Player, contents: InventoryContents) {
        contents.fill(ClickableItem.empty(ShopConfig.guiBackgroundItem.buildItem()))
        val fplayer = player.getFPlayer()
        ShopConfig.guiShopItems.forEach { shopItem ->
            contents.set(SlotPos(shopItem.row, shopItem.column), ClickableItem.of(shopItem.displayItem.buildItem()) {
                if (fplayer.credits < shopItem.price) {
                    fplayer.message(ShopConfig.cmdShopNotEnough)
                    player.closeInventory()
                    return@of
                }

                fplayer.credits -= shopItem.price
                fplayer.message(ShopConfig.cmdShopSuccess, shopItem.price.toString())
                shopItem.executeCommandsOnBuy(fplayer)
                player.closeInventory()
            })
        }
    }
}