package net.prosavage.factionsx.upgrade

import net.prosavage.factionsx.FactionsX
import net.prosavage.factionsx.persist.data.getFLocation
import net.prosavage.factionsx.util.SerializableItem
import org.bukkit.entity.EntityType
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityDeathEvent
import org.bukkit.inventory.ItemStack
import kotlin.math.ceil

class MobDropMultiplierUpgrade(targetEntity: EntityType, name: String, item: SerializableItem, maxLevelLore: List<String>, costLevel: Map<Int, LevelInfo>)
    : Upgrade(name, item, maxLevelLore, costLevel) {
    override val upgradeListener = MobDropMultiplierListener(targetEntity, FactionsX.instance, this)

    class MobDropMultiplierListener(val targetMob: EntityType, override val factionsX: FactionsX, override val upgrade: Upgrade) : UpgradeListener {

        @EventHandler
        fun onMobDeath(event: EntityDeathEvent) {
            if (event.entityType != targetMob) return
            val fLocation = getFLocation(event.entity.location)
            val upgradeParam = upgrade.upgradeLevelInfo[upgrade.getUpgradeLevelForScope(fLocation.getFaction(), fLocation)]?.upgradeParam
                    ?: return
            // We dont want to duplicate held items.
            val itemsToNotDuplicate = arrayListOf<ItemStack>()
            val equipment = event.entity.equipment
            equipment?.armorContents?.forEach { itemsToNotDuplicate.add(it) }
            equipment?.itemInHand?.let { itemsToNotDuplicate.add(it) }
            for (it in event.drops) {
                if (itemsToNotDuplicate.contains(it)) continue
                it.amount = ceil(it.amount * upgradeParam).toInt()
            }
        }

    }
}
