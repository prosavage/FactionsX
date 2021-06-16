package net.prosavage.factionsx.upgrade

import net.prosavage.factionsx.FactionsX
import net.prosavage.factionsx.persist.data.getFLocation
import net.prosavage.factionsx.util.SerializableItem
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityDeathEvent
import kotlin.math.roundToInt

class MobExpUpgrade(name: String, item: SerializableItem, maxLevelLore: List<String>, costLevel: Map<Int, LevelInfo>)
    : Upgrade(name, item, maxLevelLore, costLevel) {
    override val upgradeListener = MobExpUpgradeListener(FactionsX.instance, this)

    class MobExpUpgradeListener(override val factionsX: FactionsX, override val upgrade: Upgrade) : UpgradeListener {

        @EventHandler
        fun onMobDeath(event: EntityDeathEvent) {
            if (event.entity is Player) return
            val fLocation = getFLocation(event.entity.location)
            val factionAt = fLocation.getFaction()
            if (isUpgraded(fLocation).not()) return
            val level = when (upgrade.getScope()) {
                UpgradeScope.GLOBAL -> factionAt.getUpgrade(upgrade)
                UpgradeScope.TERRITORY -> fLocation.getUpgrade(upgrade)
                else -> return
            }
            val multiplier = upgrade.getUpgradeParamForLevel(level) ?: return
            event.droppedExp = (event.droppedExp * multiplier).roundToInt()
        }


    }
}

