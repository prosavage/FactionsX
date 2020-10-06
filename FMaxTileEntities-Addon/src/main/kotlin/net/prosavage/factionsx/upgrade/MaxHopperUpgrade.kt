package net.prosavage.factionsx.upgrade

import net.prosavage.factionsx.FactionsX
import net.prosavage.factionsx.util.SerializableItem
import org.bukkit.event.EventHandler
import org.bukkit.event.block.BlockPlaceEvent
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

class MaxHopperUpgrade(name: String, item: SerializableItem, maxLevelLore: List<String>, costLevel: Map<Int, LevelInfo>)
    : Upgrade(name, item, maxLevelLore, costLevel) {
    override val upgradeListener = BlockPlaceListener(this, FactionsX.instance)


    class BlockPlaceListener(override val upgrade: Upgrade, override val factionsX: FactionsX) : UpgradeListener {
        @ExperimentalTime
        @EventHandler
        fun onBlockPlace(event: BlockPlaceEvent) {
            var count = 0;
            val time = measureTime {
                event.blockPlaced.location.chunk.tileEntities.forEach { entity ->
                    if (entity is org.bukkit.block.Hopper) {
                        count++;
                    }
                }

            }
//            event.player.sendMessage("Found ${count} hoppers in ${16 * 16 * 255} blocks. (${time})")
        }
    }
}