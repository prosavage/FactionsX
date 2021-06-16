package net.prosavage.factionsx.listener

import com.cryptomorin.xseries.XMaterial
import net.prosavage.factionsx.disablePrinter
import net.prosavage.factionsx.event.FPlayerTerritoryChangeEvent
import net.prosavage.factionsx.getPrinterData
import net.prosavage.factionsx.hook.ShopGUIPlusHook
import net.prosavage.factionsx.isInPrinter
import net.prosavage.factionsx.persist.PricingConfig
import net.prosavage.factionsx.persist.PrinterConfig
import net.prosavage.factionsx.persist.PrinterData
import net.prosavage.factionsx.persist.data.getFLocation
import net.prosavage.factionsx.util.getFPlayer
import org.bukkit.Material
import org.bukkit.entity.Arrow
import org.bukkit.entity.ItemFrame
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.inventory.InventoryOpenEvent
import org.bukkit.event.inventory.InventoryType
import org.bukkit.event.player.*

class PrinterListener : Listener {

    @EventHandler
    fun openInv(event: InventoryOpenEvent) {
        val fPlayer = (event.player as Player).getFPlayer()
        if (!fPlayer.isInPrinter()) return
        event.isCancelled = true
        fPlayer.message(PrinterConfig.printerInventory)
    }

    @EventHandler
    fun onItemDrop(event: PlayerDropItemEvent) {
        val fplayer = event.player.getFPlayer()
        if (!fplayer.isInPrinter()) return
        event.isCancelled = true
        fplayer.message(PrinterConfig.printerDrop)
    }

    @EventHandler
    fun onDamage(event: EntityDamageByEntityEvent) {
        if (event.entity !is Player || event.damager !is Arrow || (event.damager as Arrow).shooter !is Player) return
        val fplayer = ((event.damager as Arrow).shooter as Player).getFPlayer()
        if (fplayer.isInPrinter()) {
            fplayer.message(PrinterConfig.printerBowDeny)
            event.isCancelled = true
        }
    }

    @EventHandler
    fun onPlayerLeave(event: PlayerQuitEvent) {
        val fPlayer = event.player.getFPlayer()
        if (fPlayer.isInPrinter()) {
            fPlayer.disablePrinter()
        }
    }


    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        val fplayer = event.player.getFPlayer()
        if (fplayer.isInPrinter()) {
            fplayer.disablePrinter()
        }
    }

    @EventHandler
    fun onBlockBreak(event: BlockBreakEvent) {
        val fplayer = event.player.getFPlayer()
        if (!fplayer.isInPrinter()) return
        val block = event.block
        val printingData = fplayer.getPrinterData()!!
        val blockLookup = printingData.blocksPlaced?.get(block.location)
        if (blockLookup == null) {
            fplayer.message(PrinterConfig.printerBlockBreakDeny)
            event.isCancelled = true
            return
        }

        if (blockLookup != block.type) {
            fplayer.message(PrinterConfig.printerBlockBreakChangeDeny)
            event.isCancelled = true
            return
        }

    }

    @EventHandler
    fun onEnemyNearby(event: PlayerMoveEvent) {
        val fPlayer = event.player.getFPlayer()
        if (PrinterData.inPrinter.isNotEmpty()) {
            val enemiesNearby = fPlayer.getEnemiesNearby(PrinterConfig.printerEnemyNearByBlocks).toMutableList()
            if (enemiesNearby.isEmpty()) return
            enemiesNearby.add(fPlayer)
            enemiesNearby.filter { fp -> fp.isInPrinter() }.forEach { fp ->
                fp.message(PrinterConfig.printerDisabledEnemy)
                fp.disablePrinter()
            }
        }
    }


    @EventHandler
    fun onInteract(event: PlayerInteractEvent) {
        val fplayer = event.player.getFPlayer()
        if (!fplayer.isInPrinter()) return
        if (event.item == null) return

        val xmaterialInHand: XMaterial
        try {
            xmaterialInHand = XMaterial.matchXMaterial(event.item!!.type)
        } catch (exc: IllegalArgumentException) {
            println(event.item!!.type)
            return
        }

        if (PrinterConfig.printerDisabledTypes.contains(xmaterialInHand)) {
            fplayer.message(PrinterConfig.printerBlockPlaceDisabled)
            event.player.itemInHand.type = Material.AIR
            event.player.updateInventory()
            event.isCancelled = true
            return
        }
    }

    @EventHandler
    fun onBlockPlace(event: BlockPlaceEvent) {
        val fplayer = event.player.getFPlayer()
        if (!fplayer.isInPrinter()) return
        if (event.player.openInventory.type == InventoryType.CRAFTING) event.player.closeInventory()
        val xmaterialInHand: XMaterial
        try {
            xmaterialInHand = XMaterial.matchXMaterial(event.itemInHand.type)
        } catch (exc: IllegalArgumentException) {
            println(event.itemInHand.type)
            return
        }

        if (PrinterConfig.printerDisabledTypes.contains(xmaterialInHand)) {
            fplayer.message(PrinterConfig.printerBlockPlaceDisabled)
            event.isCancelled = true
            return
        }

        val flocation = getFLocation(event.block.location)
        if (flocation.getFaction() != fplayer.getFaction()) {
            fplayer.message(PrinterConfig.printerBlockOutsideLand)
            event.isCancelled = true
            return
        }

        val price: Double

        if (PrinterConfig.useShopGUIPlusHook) {
            val clonedItem = event.itemInHand.clone()
            clonedItem.amount = 1
            var buyPrice = ShopGUIPlusHook.getBuyPrice(clonedItem, PrinterConfig.shopGUIFallBackPrice)
            if (buyPrice == null) {
                if (PrinterConfig.dontAllowPrintingIfNotInShopGUI) {
                    fplayer.message(PrinterConfig.printerBlockPlaceDisabled)
                    event.isCancelled = true
                    return
                } else {
                    buyPrice = PrinterConfig.shopGUIFallBackPrice
                }
            }
            price = buyPrice
        } else {
            price = PricingConfig.blockValues[event.itemInHand.type]!!
        }

        val success = fplayer.takeMoney(price)
        if (!success) {
            event.itemInHand.type = Material.AIR
            event.isCancelled = true
            return
        }

        val printerData = fplayer.getPrinterData()
        if (printerData?.blocksPlaced == null) printerData?.blocksPlaced = hashMapOf()
        printerData?.spent = printerData?.spent?.plus(price) ?: 0.0

        printerData?.blocksPlaced?.set(event.blockPlaced.location, event.blockPlaced.type)
    }

//    @EventHandler
//    fun onPlayerQuit(event: PlayerQuitEvent) {
//        val fplayer = event.player.getFPlayer()
//        if (!fplayer.isInPrinter()) return
//        fplayer.disablePrinter()
//    }

    @EventHandler
    fun onItemPickup(event: PlayerPickupItemEvent) {
        val fplayer = event.player.getFPlayer()
        if (!fplayer.isInPrinter()) return
        event.item.pickupDelay = 50
        event.isCancelled = true
        fplayer.message(PrinterConfig.printerBlockItemPickup)
    }


    @EventHandler
    fun onFPlayerTerritoryChange(event: FPlayerTerritoryChangeEvent) {
        if (!PrinterConfig.canOnlyPrintInOwnTerritory || event.fPlayer.getFaction() != event.fromFaction) return
        if (event.fPlayer.isInPrinter()) event.fPlayer.disablePrinter()
    }

    @EventHandler
    fun onArmorStandManipulate(event: PlayerArmorStandManipulateEvent) {
        val fplayer = event.player.getFPlayer()
        if (!fplayer.isInPrinter()) return
        event.isCancelled = true
        fplayer.message(PrinterConfig.printerBlockArmorStand)
    }

    @EventHandler
    fun onEntityInteract(event: PlayerInteractEntityEvent) {
        if (event.rightClicked !is ItemFrame) return
        val fplayer = event.player.getFPlayer()
        if (!fplayer.isInPrinter()) return
        event.isCancelled = true
        fplayer.message(PrinterConfig.printerBlockItemFrame)
    }

    @EventHandler
    fun onHitPlayer(event: EntityDamageByEntityEvent) {
        val damaged = event.entity
        val damager = event.damager
        if (damager !is Player || damaged !is Player) return
        val fplayer = damager.getFPlayer()
        if (!fplayer.isInPrinter()) return
        event.isCancelled = true
        fplayer.message(PrinterConfig.printerBlockPvP)
    }

    // For support in 1.13+ since MONSTER_EGG no longer exists.
    val monsterEgg = Material.valueOf("MONSTER_EGG")

    @EventHandler
    fun onPlayerInteract(event: PlayerInteractEvent) {
        val player = event.player
        val fplayer = player.getFPlayer()
        if (!fplayer.isInPrinter()) return
        if (player.itemInHand == null || player.itemInHand.type == Material.AIR) return
        if (PrinterConfig.blockEnchantingBottles && event.material == XMaterial.EXPERIENCE_BOTTLE.parseMaterial()) {
            event.isCancelled = true
            fplayer.message(PrinterConfig.printerBlockExpBottle)
            return
        }
        if (event.action == Action.RIGHT_CLICK_BLOCK && event.material == monsterEgg) {
            event.isCancelled = true
            fplayer.message(PrinterConfig.printerBlockSpawnEgg)
            return
        }
        if (!player.itemInHand.hasItemMeta()) return
        event.isCancelled = true
        fplayer.message(PrinterConfig.printerBlockSpecialMetaInteract)
    }

    @EventHandler
    fun onCommandPreProcess(event: PlayerCommandPreprocessEvent) {
        val fplayer = event.player.getFPlayer()
        if (!fplayer.isInPrinter()) return
        var allowed = false
        PrinterConfig.commandStartsWithWhiteList.forEach { line ->
            if (event.message.toLowerCase().startsWith(line.toLowerCase())) {
                allowed = true
            }
        }
        if (!allowed) {
            event.isCancelled = true
            fplayer.message(PrinterConfig.printerBlockCommand)
        }
    }


}