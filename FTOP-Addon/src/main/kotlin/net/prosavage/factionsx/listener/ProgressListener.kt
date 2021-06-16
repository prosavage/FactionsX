package net.prosavage.factionsx.listener

import com.cryptomorin.xseries.XMaterial.matchXMaterial
import net.prosavage.factionsx.FTOPAddon.Companion.factionValues
import net.prosavage.factionsx.calc.progress.BlockProgress
import net.prosavage.factionsx.calc.progress.SpawnerProgress
import net.prosavage.factionsx.persist.data.getFLocation
import net.prosavage.factionsx.util.component1
import net.prosavage.factionsx.util.component2
import net.prosavage.factionsx.util.component3
import org.bukkit.Location
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.entity.EntityExplodeEvent
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletableFuture.supplyAsync

/**
 * This [Listener] handles all progress work.
 */
object ProgressListener : Listener {
    /**
     * [BlockBreakEvent] this event handles blocks being broken by players.
     */
    @EventHandler(priority = EventPriority.MONITOR)
    private fun BlockBreakEvent.onBroken() {
        // make sure the event is NOT cancelled
        if (this.isCancelled) return

        // fetch and handle
        val material = block.type
        fetch(block.location).thenAccept { it?.terminate(matchXMaterial(material)) }
    }

    /**
     * [EntityExplodeEvent] this event handles blocks being exploded.
     */
    @EventHandler(priority = EventPriority.MONITOR)
    private fun EntityExplodeEvent.onBroken() {
        // make sure the event is NOT cancelled
        if (this.isCancelled) return

        // loop, fetch and handle
        blockList().forEach { block ->
            val material = block.type
            fetch(block.location).thenAccept { it?.terminate(matchXMaterial(material)) }
        }
    }

    /**
     * Fetch a [BlockProgress] by location.
     *
     * @param location [Location] the location correspondent to our progress.
     * @return [CompletableFuture]
     */
    private fun fetch(location: Location): CompletableFuture<BlockProgress?> = supplyAsync {
        // necessity
        val (x, y, z) = location

        // trigger
        factionValues.values
            .find { it.faction == getFLocation(location).getFaction().id }
            ?.progressive?.values
            ?.find { set -> set.any {
                val (chunkX, chunkZ) = it.location
                chunkX == (x shr 4) && chunkZ == (z shr 4)
            }}?.find {
                val (_, _, posX, posY, posZ) = it.location

                if (it is SpawnerProgress) posX == x && posY == y && posZ == z
                else x and 15 == posX && y == posY && z and 15 == posZ
            }
    }
}