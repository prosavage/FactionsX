package net.prosavage.factionsx.gui.shield

import fr.minuskube.inv.ClickableItem
import fr.minuskube.inv.InventoryListener
import fr.minuskube.inv.SmartInventory
import fr.minuskube.inv.content.InventoryContents
import fr.minuskube.inv.content.InventoryProvider
import net.prosavage.baseplugin.ItemBuilder
import net.prosavage.factionsx.FactionsX
import net.prosavage.factionsx.core.FPlayer
import net.prosavage.factionsx.persist.Message
import net.prosavage.factionsx.persist.config.Config
import net.prosavage.factionsx.persist.config.Config.shieldModeTwoTimerItem
import net.prosavage.factionsx.util.color
import net.prosavage.factionsx.util.getFPlayer
import net.prosavage.factionsx.util.toDate
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.inventory.InventoryOpenEvent
import java.time.LocalTime
import java.util.*
import java.util.concurrent.TimeUnit
import java.util.function.BiConsumer

class ShieldMenuTwo : InventoryProvider {
    val playerPages = IdentityHashMap<Player, Int>()
    private val hours = mutableMapOf<Int, BiConsumer<FPlayer, InventoryContents>>()

    init {
        val shield = Config.factionShield
        for (hour in 0..23) {
            val time = "$hour:00"
            val endDate = Date(LocalTime.of(hour, 0).toDate().time + TimeUnit.SECONDS.toMillis(shield.durationInSeconds))

            val endCal = Calendar.getInstance()
            endCal.time = endDate

            val hourOfDay = endCal.get(Calendar.HOUR_OF_DAY)
            val minuteOfDay = String.format("%02d", endCal.get(Calendar.MINUTE))

            hours += hour to BiConsumer { fPlayer, contents ->
                val (row, column) = shieldModeTwoTimerItem.coordinate

                contents.set(row, column,
                        ClickableItem.of(ItemBuilder(shieldModeTwoTimerItem.displayItem.buildItem())
                                .name(Config.shieldTimerItem.name.replace("{time}", time).replace("{end}", "$hourOfDay:$minuteOfDay"))
                                .lore(Config.shieldTimerItem.lore.map { line -> line.replace("{time}", time).replace("{end}", "$hourOfDay:$minuteOfDay") })
                                .build()
                        ) {
                            fPlayer.getFaction().shieldTimeStart = LocalTime.of(hour, 0)
                            fPlayer.message(Message.shieldTimeSet, time)
                            fPlayer.getPlayer()?.closeInventory()
                            fPlayer.getFaction().setShield()
                        })
            }
        }
    }

    override fun init(player: Player, contents: InventoryContents) {
        val fPlayer = player.getFPlayer()
        contents.fill(ClickableItem.empty(Config.shieldsGUIBackgroundItem.buildItem()))

        hours[0]?.accept(fPlayer, contents)

        with(Config.factionShieldInfoItem) {
            contents.set(coordinate.row, coordinate.column, ClickableItem.empty(
                    ItemBuilder(displayItem.buildItem()).lore(displayItem.lore.map { lore ->
                        lore.replace("{server-time}", LocalTime.now().toString())
                    }).build()
            ))
        }

        with(Config.shieldModeTwoPreviousItem) {
            contents.set(coordinate.row, coordinate.column, ClickableItem.of(displayItem.buildItem()) {
                val page = playerPages[player] ?: 0
                if (page == 0) return@of

                hours[page - 1]?.accept(fPlayer, contents)
                playerPages[player] = page - 1
            })
        }

        with(Config.shieldModeTwoNextItem) {
            contents.set(coordinate.row, coordinate.column, ClickableItem.of(displayItem.buildItem()) {
                val page = playerPages[player] ?: 0
                if (page == 23) return@of

                hours[page + 1]?.accept(fPlayer, contents)
                playerPages[player] = page + 1
            })
        }
    }

    companion object {
        val INVENTORY: SmartInventory by lazy {
            val shieldMenuTwo = ShieldMenuTwo()
            SmartInventory.builder()
                    .manager(FactionsX.inventoryManager)
                    .id("shield_mode_2")
                    .provider(shieldMenuTwo)
                    .size(Config.shieldsGUIRows, 9)
                    .title(color(Config.shieldsGUIName))
                    .listener(InventoryListener(InventoryOpenEvent::class.java) { shieldMenuTwo.playerPages[it.player as Player] = 0 })
                    .listener(InventoryListener(InventoryCloseEvent::class.java) { shieldMenuTwo.playerPages.remove(it.player) })
                    .build()
        }
    }
}