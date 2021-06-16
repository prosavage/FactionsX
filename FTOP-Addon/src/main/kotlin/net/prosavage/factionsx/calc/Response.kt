package net.prosavage.factionsx.calc

import com.cryptomorin.xseries.XMaterial
import net.prosavage.factionsx.calc.progress.BlockProgress
import net.prosavage.factionsx.core.Faction
import net.prosavage.factionsx.manager.FactionManager
import org.bukkit.entity.EntityType
import java.lang.System.currentTimeMillis
import java.util.*

/**
 * This data class information contains the response of a Faction's top calculation.
 *
 * @param faction        [Long] id of the faction that this Response belongs to.
 * @param bankBalance    [Double] the faction's bank balance.
 * @param percentageLoss [Int] the percentage loss.
 * @param progressive    [Map] this map contains all block progress, in this case progressive values.
 * @param otherMaterials [Map] this map contains all non-progressive values.
 * @param potentialValue [Int] the faction's potential value after all progress has been completed.
 * @param finishedAt     [Long] when the calculation was finished and this response was created.
 */
data class Response constructor(
    val faction: Long,
    val bankBalance: Double,
    val percentageLoss: Int,
    val progressive: EnumMap<XMaterial, HashSet<BlockProgress>>,
    val spawners: EnumMap<EntityType, Int>,
    val otherMaterials: EnumMap<XMaterial, Int>,
    val otherMaterialsValue: Double,
    val potentialValue: Double,
    val finishedAt: Long = currentTimeMillis()
) {
    /**
     * [Faction] the faction instance of this block.
     */
    @Transient
    val factionObject: Faction = FactionManager.getFaction(faction)

    /**
     * [Long] This property contains the latest calculated value.
     */
    var latestCalculatedValue = otherMaterialsValue
        private set

    /**
     * Recalculate this [Response]'s value.
     */
    fun recalculate() {
        // calculate the progressive value
        val progressiveValue = progressive.values
            .stream().mapToDouble { c ->
                c.stream().mapToDouble(BlockProgress::currentValue).sum()
            }.sum()

        // set the latest calculated value
        latestCalculatedValue = otherMaterialsValue + progressiveValue
    }
}