package net.prosavage.factionsx.gui.shield

import fr.minuskube.inv.ClickableItem
import fr.minuskube.inv.SmartInventory
import fr.minuskube.inv.content.InventoryContents
import fr.minuskube.inv.content.InventoryProvider
import fr.minuskube.inv.content.SlotIterator
import net.prosavage.baseplugin.ItemBuilder
import net.prosavage.factionsx.FactionsX
import net.prosavage.factionsx.core.Faction
import net.prosavage.factionsx.persist.Message
import net.prosavage.factionsx.persist.config.Config
import net.prosavage.factionsx.util.color
import net.prosavage.factionsx.util.getFPlayer
import net.prosavage.factionsx.util.toDate
import org.bukkit.entity.Player
import java.time.LocalTime
import java.util.*
import java.util.concurrent.TimeUnit

class ShieldMenu(val forFaction: Faction) : InventoryProvider {
    companion object {
        fun getInv(forFaction: Faction): SmartInventory? {
            return SmartInventory.builder()
                    .manager(FactionsX.inventoryManager)
                    .id(UUID.randomUUID().toString())
                    .provider(ShieldMenu(forFaction))
                    .size(Config.shieldsGUIRows, 9)
                    .title(color(Config.shieldsGUIName))
                    .build()
        }
    }


    override fun init(player: Player, contents: InventoryContents) {
        val fPlayer = player.getFPlayer()
        contents.fill(ClickableItem.empty(Config.shieldsGUIBackgroundItem.buildItem()))
        val shield = Config.factionShield
        contents.set(Config.factionShieldInfoItem.coordinate.row, Config.factionShieldInfoItem.coordinate.column,
                ClickableItem.empty(
                        ItemBuilder(Config.factionShieldInfoItem.displayItem.buildItem())
                                .lore(Config.factionShieldInfoItem.displayItem.lore.map { lore -> lore.replace("{server-time}", LocalTime.now().toString()) })
                                .build()
                ))
        val timerIterator = contents.newIterator(SlotIterator.Type.HORIZONTAL, Config.factionShieldTimes.row, Config.factionShieldTimes.column)
        for (hour in 0..23) {
            val time = "$hour:00"
            val endDate = Date(LocalTime.of(hour, 0).toDate().time + TimeUnit.SECONDS.toMillis(shield.durationInSeconds))
            val endCal = Calendar.getInstance()
            endCal.time = endDate
            val hourOfDay = endCal.get(Calendar.HOUR_OF_DAY)
            val minuteOfDay = String.format("%02d", endCal.get(Calendar.MINUTE))
            timerIterator.set(ClickableItem.of(ItemBuilder(Config.shieldTimerItem.buildItem())
                    .name(Config.shieldTimerItem.name.replace("{time}", time).replace("{end}", "$hourOfDay:$minuteOfDay"))
                    .lore(Config.shieldTimerItem.lore.map { line -> line.replace("{time}", time).replace("{end}", "$hourOfDay:$minuteOfDay") })
                    .build()) {
                fPlayer.getFaction().shieldTimeStart = LocalTime.of(hour, 0)
                fPlayer.message(Message.shieldTimeSet, time)
                player.closeInventory()
                fPlayer.getFaction().setShield()
            }).next()
        }


    }

//    override fun update(player: Player, contents: InventoryContents) {
//        val lore = Config.shieldTimer.displayItem.lore.map { line ->
//            PlaceholderManager.processPlaceholders(player.getFPlayer(), forFaction, line)
//        }
//        contents.set(Config.shieldTimer.coordinate.row, Config.shieldTimer.coordinate.column, ClickableItem.empty(ItemBuilder(Config.shieldTimer.displayItem.buildItem()).lore(lore).build()))
//
//    }
}