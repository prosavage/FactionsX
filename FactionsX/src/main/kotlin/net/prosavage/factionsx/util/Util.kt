package net.prosavage.factionsx.util

import com.cryptomorin.xseries.XMaterial
import net.prosavage.factionsx.core.FPlayer
import net.prosavage.factionsx.core.Faction
import net.prosavage.factionsx.manager.PlayerManager
import net.prosavage.factionsx.persist.config.Config
import net.prosavage.factionsx.persist.config.ProtectionConfig
import org.bukkit.*
import org.bukkit.block.BlockFace
import org.bukkit.block.BlockFace.*
import org.bukkit.entity.Player
import java.lang.reflect.InvocationTargetException
import java.math.BigInteger
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.util.*
import java.util.concurrent.ForkJoinPool.commonPool
import java.util.concurrent.ThreadLocalRandom
import kotlin.math.roundToInt


fun color(message: String): String = ChatColor.translateAlternateColorCodes('&', message)

/**
 * Process all RGB colors in a [String].
 */
inline val String.coloredRgb
    get() = replace(Patterns.COLOR_RGB)
    { net.md_5.bungee.api.ChatColor.of(it.value).toString() }

/**
 * Process all type of colors in a [String].
 * NOTE: This includes 1.16 RGB support.
 */
fun multiColor(string: String): String = if (nmsVersion >= 16) color(string).coloredRgb else color(string)


fun buildBar(element: String, barLength: Int = Config.barLength): String {
    val bar = StringBuilder()
    repeat(barLength) {
        bar.append(element)
    }
    return color(bar.toString())
}

inline fun <reified T : Enum<*>> enumValueOrNull(name: String): T? =
        T::class.java.enumConstants.firstOrNull { it.name == name }

/**
 * Gets the chunksnapshot's block type.
 *
 * @param useNew        - use the new method or not, this is only here as this method is designed to be called a lot,
 * and I dont want to do the processing over and over.
 * @param chunkSnapshot - the chunksnapshot to calculate.
 * @param x             - the x to get the block of
 * @param y             - the y to get the block of
 * @param z             - the z to get the block of
 * @return - the Material it gets.
 */
@Throws(NoSuchMethodException::class, InvocationTargetException::class, IllegalAccessException::class)
fun getChunkSnapshotBlockType(useNew: Boolean, chunkSnapshot: ChunkSnapshot, x: Int, y: Int, z: Int): Material? {
    return if (useNew) chunkSnapshot.getBlockType(x, y, z)
    else {
        val id = chunkSnapshot.javaClass.getMethod(
                "getBlockTypeId",
                Int::class.javaPrimitiveType,
                Int::class.javaPrimitiveType,
                Int::class.javaPrimitiveType
        )
                .invoke(chunkSnapshot, x, y, z) as Int
        return Class.forName("org.bukkit.Material").getMethod("getMaterial", Int::class.javaPrimitiveType)
                .invoke(null, id) as Material
    }
}

fun logColored(line: String) {
    Bukkit.getConsoleSender().sendMessage(net.prosavage.factionsx.persist.color("&7[&6FactionsX&7] &r$line"))
}

fun checkPercentage(chance: Double): Boolean {
    val randomNumber = ThreadLocalRandom.current().nextInt(0, 101)
    return (randomNumber <= chance * 100)
}

fun Player.getFPlayer(): FPlayer {
    return PlayerManager.getFPlayer(this)
}

fun OfflinePlayer.getFPlayer(): FPlayer = PlayerManager.getFPlayer(this.uniqueId) ?: throw IllegalStateException(
        "Faction player with unique id '$uniqueId' does not exist."
)

internal val orderedDirections = arrayOf(
        SOUTH, SOUTH_WEST, WEST, NORTH_WEST,
        NORTH, NORTH_EAST, EAST, SOUTH_EAST
)

fun Player.getDirection(): BlockFace = orderedDirections[
        (this.location.yaw / 45).roundToInt() and 0x7
]

fun Material.getXMaterial(): XMaterial {
    return XMaterial.matchXMaterial(this)
}


fun XMaterial.isPressurePlate(): Boolean {
    return (this == XMaterial.STONE_PRESSURE_PLATE
            || this == XMaterial.ACACIA_PRESSURE_PLATE
            || this == XMaterial.BIRCH_PRESSURE_PLATE
            || this == XMaterial.DARK_OAK_PRESSURE_PLATE
            || this == XMaterial.HEAVY_WEIGHTED_PRESSURE_PLATE
            || this == XMaterial.JUNGLE_PRESSURE_PLATE
            || this == XMaterial.LIGHT_WEIGHTED_PRESSURE_PLATE
            || this == XMaterial.OAK_PRESSURE_PLATE
            || this == XMaterial.SPRUCE_PRESSURE_PLATE
            || this == XMaterial.CRIMSON_PRESSURE_PLATE
            || this == XMaterial.WARPED_PRESSURE_PLATE)
}

internal val nmsVersion: Int = Bukkit
        .getServer()::class.java
        .`package`.name
        .split("\\.".toRegex())[3]
        .replace("v1", "").replace("_", "")
        .replace("R\\d+".toRegex(), "").toInt()

internal fun asyncIf(predicate: Boolean, action: () -> Unit) =
        if (predicate) commonPool().execute(action) else action()

fun LocalTime.toDate(): Date {
    val instant: Instant = atDate(LocalDate.now())
            .atZone(ZoneId.systemDefault()).toInstant()
    return instant.toDate()
}

fun Instant.toDate(): Date {
    var milis = BigInteger.valueOf(epochSecond).multiply(
            BigInteger.valueOf(1000))
    milis = milis.add(BigInteger.valueOf(nano.toLong()).divide(
            BigInteger.valueOf(1000000)))
    return Date(milis.toLong())
}

fun Number.format(): String {
    return Config.numberFormat.format(this)
}

fun Faction.isActionsWhenOfflineCompatible(actionsWhenOffline: ProtectionConfig.ActionsWhenOffline): Boolean {
    return !this.isSystemFaction()
            && this.getOnlineMembers().isEmpty()
            && actionsWhenOffline.enabled
            && System.currentTimeMillis() >= (this.getMembers().maxBy { it.timeAtLastLogin }?.timeAtLastLogin ?: 0).plus(actionsWhenOffline.hasBeenOfflineForSeconds * 1000)
}