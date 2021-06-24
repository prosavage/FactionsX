package net.prosavage.factionsx.calc.progress

import com.cryptomorin.xseries.XMaterial
import net.prosavage.factionsx.FTOPAddon.Companion.factionValues
import java.lang.System.currentTimeMillis

/**
 * This data class contains information about a [BlockProgress] location.
 *
 * @param chunkX [Int] X coordinate of the chunk.
 * @param chunkZ [Int] Z coordinate of the chunk.
 * @param posX   [Int] X coordinate of the position inside the chunk.
 * @param posY   [Int] Y coordinate of the position inside the chunk.
 * @param posZ   [Int] Z coordinate of the position inside the chunk.
 */
data class ProgressLocation(
    val chunkX: Int,
    val chunkZ: Int,
    val posX: Int,
    val posY: Int,
    val posZ: Int
) {
    /**
     * Check whether or not provided coordinates are the same as this progress's location.
     */
    fun isSame(chunkX: Int, chunkZ: Int, posX: Int, posY: Int, posZ: Int): Boolean =
        this.chunkX == chunkX && this.chunkZ == chunkZ && this.posX == posX && this.posY == posY && this.posZ == posZ

    /**
     * Check whether or not provided [ProgressLocation] is the same as this.
     */
    fun isSame(other: ProgressLocation): Boolean =
        this.isSame(other.chunkX, other.chunkZ, other.posX, other.posY, other.posZ)

    /**
     * Make sure we override the [Any.equals] method.
     */
    override fun equals(other: Any?): Boolean =
        other != null && other is ProgressLocation && other.isSame(this)

    /**
     * Make sure we override the [Any.hashCode] method.
     */
    override fun hashCode(): Int {
        var result = chunkX
        result = 31 * result + chunkZ
        result = 31 * result + posX
        result = 31 * result + posY
        result = 31 * result + posZ
        return result
    }
}

/**
 * This data class contains information about a Block's progressive value.
 *
 * @param ownerThen      [Long] id of the faction that own(ed) this [BlockProgress] upon calculation.
 * @param location       [ProgressLocation] the location of where this was processed.
 * @param value          [Double] finishing value.
 * @param tillFinishedMs [Long] amount of milliseconds until this progress is completed.
 * @param startedAt      [Long] timestamp of when this progress was started.
 */
open class BlockProgress constructor(
    val material: XMaterial,
    val ownerThen: Long,
    val location: ProgressLocation,
    val value: Double,
    val tillFinishedMs: Long,
    val startedAt: Long = currentTimeMillis()
) {
    /**
     * [Long] This property contains the timestamp of when this progress is completed.
     */
    val completedAt = startedAt + tillFinishedMs

    /**
     * Get the current value of this progression.
     *
     * @return [Double]
     */
    fun currentValue(): Double {
        val now = currentTimeMillis()

        if (completedAt <= now) {
            return value
        }

        val stage = tillFinishedMs - (completedAt - now)
        return (value * ((stage * 100f) / tillFinishedMs)) / 100
    }

    /**
     * Terminate this block progress.
     */
    fun terminate() {
        val response = factionValues.values.find { it.faction == ownerThen } ?: return
        if (response.progressive[material]?.remove(this) == false || this !is SpawnerProgress) {
            return
        }

        val spawners = response.spawners
        val old = spawners[type] ?: return

        if (old == 1) {
            spawners.remove(type)
            return
        }

        spawners[type] = old - 1
    }

    /**
     * Make sure we override the [Any.equals] method.
     */
    override fun equals(other: Any?): Boolean =
        other != null && other is BlockProgress && other.location == this.location

    /**
     * Make sure we override the [Any.hashCode] method.
     */
    override fun hashCode(): Int = location.hashCode()
}