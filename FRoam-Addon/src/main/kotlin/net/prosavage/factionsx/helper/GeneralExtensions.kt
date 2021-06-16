package net.prosavage.factionsx.helper

import com.sk89q.worldguard.protection.regions.ProtectedRegion
import net.prosavage.factionsx.FactionsX
import net.prosavage.factionsx.persist.config.Config.worldGuardRegionAllowedClaimInWorlds
import net.prosavage.factionsx.persist.data.FLocation
import org.bukkit.Location
import org.bukkit.entity.Player

/**
 * Get two corners by a bound size.
 *
 * @param size to divide by 2 and subtract / add for the two points.
 * @return [Pair] of corresponding locations.
 */
fun Location.getCorners(size: Double, height: Double): Pair<Location, Location> {
    val side = (size / 2).times(2)
    val exactHeight = (height / 2).times(2)
    return Pair(
            this.clone().subtract(side, exactHeight, side),
            this.clone().add(side, exactHeight, side)
    )
}

/**
 * Get if a [Player] is allowed in region.
 *
 * @param fLocation to check for regions in.
 * @return [Boolean] if they're allowed.
 */
fun Player.isAllowedInRegion(fLocation: FLocation): Boolean = with(FactionsX.worldGuard) {
    val regionId = worldGuardRegionAllowedClaimInWorlds[world.name]
    val regions = getRegionsAt(fLocation.world, fLocation.x.toInt(), fLocation.z.toInt()) as Set<ProtectedRegion>
    regions.isEmpty() || regions.any { it.id == regionId } && regions.size == 1
}