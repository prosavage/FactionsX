package net.prosavage.factionsx.persist

import com.cryptomorin.xseries.XMaterial
import net.prosavage.factionsx.addonframework.Addon
import net.prosavage.factionsx.util.Coordinate
import net.prosavage.factionsx.util.InterfaceItem
import net.prosavage.factionsx.util.SerializableItem
import net.prosavage.factionsx.util.TopItem
import java.io.File

object MenuConfig {
    @Transient
    private val instance = this

    var rows = 2
    var title = "&6Faction Top"

    var successTopItem = TopItem(
            XMaterial.PLAYER_HEAD,
            "&6#{index} &7{tag}",
            listOf(
                    "&6 &l* &7Total: &6&n\${price}",
                    "&6 &l* &7Blocks: &6&n{blocks}",
                    "&6 &l* &7Spawners: &6&n{spawners}"
            ),
            glow = false,
            ownerHead = true
    )

    var failedTopItem = SerializableItem(XMaterial.RED_STAINED_GLASS_PANE, "&c&nN/A", listOf(), 1)

    var calculationLatestNextItem = InterfaceItem(
            false, Coordinate(0, 4),
            SerializableItem(XMaterial.CLOCK, "&6Latest Calculation", listOf(
                    "&7{latest}",
                    "&r",
                    "&6Next Calculation",
                    "&7{next}"
            ), 1)
    )

    var calculationLatestNextFormat = "{days}d, {hours}h, {minutes}m, {seconds}s"
    var calculationLatestNextNever = "Never"

    data class Item(val column: Int, val row: Int, val index: Long)

    var items = setOf(
            Item(3, 1, 2),
            Item(4, 1, 1),
            Item(5, 1, 3)
    )

    var general = setOf(
            InterfaceItem(false, Coordinate(0, 0), SerializableItem(
                    XMaterial.BLACK_STAINED_GLASS_PANE, "&r", listOf(), 1
            )),
            InterfaceItem(false, Coordinate(0, 8), SerializableItem(
                    XMaterial.BLACK_STAINED_GLASS_PANE, "&r", listOf(), 1
            )),
            InterfaceItem(false, Coordinate(1, 0), SerializableItem(
                    XMaterial.BLACK_STAINED_GLASS_PANE, "&r", listOf(), 1
            )),
            InterfaceItem(false, Coordinate(1, 8), SerializableItem(
                    XMaterial.BLACK_STAINED_GLASS_PANE, "&r", listOf(), 1
            ))
    )

    fun save(addon: Addon) {
        addon.configSerializer.save(instance, File(addon.addonDataFolder, "topmenu.json"))
    }

    fun load(addon: Addon) {
        addon.configSerializer.load(instance, MenuConfig::class.java, File(addon.addonDataFolder, "topmenu.json"))
    }
}