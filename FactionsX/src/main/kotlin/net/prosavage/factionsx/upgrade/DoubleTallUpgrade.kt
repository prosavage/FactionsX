package net.prosavage.factionsx.upgrade

import com.cryptomorin.xseries.XMaterial
import net.prosavage.factionsx.FactionsX
import net.prosavage.factionsx.persist.data.getFLocation
import net.prosavage.factionsx.util.SerializableItem
import org.bukkit.event.EventHandler
import org.bukkit.event.block.BlockGrowEvent

class DoubleTallUpgrade(targetCrop: XMaterial, name: String, item: SerializableItem, maxLevelLore: List<String>, costLevel: Map<Int, LevelInfo>)
    : Upgrade(name, item, maxLevelLore, costLevel) {
    override val upgradeListener = DoubleTallUpgradeListener(targetCrop, this, FactionsX.instance)


    class DoubleTallUpgradeListener(val targetCrop: XMaterial, override val upgrade: Upgrade, override val factionsX: FactionsX) : UpgradeListener {
        @EventHandler
        fun onCropGrow(event: BlockGrowEvent) {
            // Gotta make sure it's target growing.
            val blockBelow = event.block.location.subtract(0.0, 1.0, 0.0).block
            val target = targetCrop.parseMaterial()
            if (blockBelow.type != target) {
                return
            }
            // We need to make sure we have air above it.
            val blockAbove = event.block.location.add(0.0, 1.0, 0.0).block
            // This should not be target, because we will be bypassing the 3 block limit by growing it twice.
            val base = event.block.location.subtract(0.0, 2.0, 0.0).block
            if (blockAbove.type == XMaterial.AIR.parseMaterial() && base.type != target) {
                if (!runUpgradeEffectWithChance(getFLocation(event.block.chunk))) return
                blockAbove.type = target
            }
        }
    }
}
