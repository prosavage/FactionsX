package net.prosavage.factionsx.upgrade

import com.cryptomorin.xseries.XMaterial
import net.prosavage.factionsx.FactionsX
import net.prosavage.factionsx.persist.data.getFLocation
import net.prosavage.factionsx.util.SerializableItem
import org.bukkit.block.data.Ageable
import org.bukkit.event.EventHandler
import org.bukkit.event.block.BlockGrowEvent

class CropUpgrade(name: String, item: SerializableItem, maxLevelLore: List<String>, costLevel: Map<Int, LevelInfo>, val cropType: XMaterial)
    : Upgrade(name, item, maxLevelLore, costLevel) {
    override val upgradeListener = CropInstantGrowListener(this, FactionsX.instance, cropType)

    class CropInstantGrowListener(override val upgrade: Upgrade, override val factionsX: FactionsX, val cropType: XMaterial) : UpgradeListener {

        @EventHandler
        fun onCropGrow(event: BlockGrowEvent) {
            if (XMaterial.matchXMaterial(event.block.type) !== cropType) return
            if (!runUpgradeEffectWithChance(getFLocation(event.block.chunk))) return
            event.isCancelled = true
            val blockState = event.block.blockData
            val ageable = blockState as Ageable
            ageable.age = ageable.maximumAge
            event.block.blockData = ageable
        }

    }
}
