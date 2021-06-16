package net.prosavage.factionsx.util

import com.cryptomorin.xseries.XMaterial
import fr.minuskube.inv.SmartInventory
import net.prosavage.factionsx.calc.Response
import net.prosavage.factionsx.core.Faction
import net.prosavage.factionsx.manager.FactionManager
import net.prosavage.factionsx.manager.PlaceholderManager.processPlaceholders
import net.prosavage.factionsx.persist.FTOPConfig.priceFormat
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta
import kotlin.collections.Map.Entry

/**
 * Class to hold data for the top item feel and look.
 */
data class TopItem(val material: XMaterial, val name: String, val lore: List<String>, val glow: Boolean, val ownerHead: Boolean) {
    /**
     * Build the top item accordingly.
     */
    fun build(entry: Entry<Long, Response>): ItemStack = (material.parseItem()
            ?: XMaterial.DIRT.parseItem()!!).apply {
        val index = entry.key
        val info = entry.value
        val faction = FactionManager.getFaction(info.faction)

        itemMeta = itemMeta?.apply {
            this.setDisplayName(processInfo(faction, info, index, color(name)))

            this.lore = this@TopItem.lore.map { line -> processInfo(faction, info, index, color(line)) }

            if (glow) addEnchant(Enchantment.DURABILITY, 1, false); addItemFlags(ItemFlag.HIDE_ENCHANTS)
        }

        if (ownerHead && material == XMaterial.PLAYER_HEAD) itemMeta = (itemMeta as SkullMeta).apply {
            this.owner = faction.getLeader()?.name
        }
    }
}

/**
 * Process all information placeholders.
 */
internal fun processInfo(faction: Faction, info: Response, index: Long, string: String): String {
    return processPlaceholders(null, faction, string)
            .replace("{index}", index.toString())
            .replace("{price}", priceFormat.format(info.latestCalculatedValue))
            .replace("{blocks}", priceFormat.format(0))
            .replace("{spawners}", priceFormat.format(info.spawners.values.sum()))
            .replace("{percentage-loss}", info.percentageLoss.toString())
}

/**
 * Build a [SmartInventory].
 */
internal fun buildMenu(handle: SmartInventory.Builder.() -> Unit): SmartInventory {
    val builder = SmartInventory.builder()
    handle(builder)
    return builder.build()
}