package net.prosavage.factionsx.manager

import com.cryptomorin.xseries.XMaterial
import com.google.common.collect.ArrayListMultimap
import com.google.common.collect.ListMultimap
import net.prosavage.factionsx.FactionsX
import net.prosavage.factionsx.FactionsX.Companion.worldGuard
import net.prosavage.factionsx.core.FPlayer
import net.prosavage.factionsx.core.Faction
import net.prosavage.factionsx.event.FactionOverclaimEvent
import net.prosavage.factionsx.event.FactionPreClaimEvent
import net.prosavage.factionsx.event.FactionUnClaimEvent
import net.prosavage.factionsx.persist.Message
import net.prosavage.factionsx.persist.config.Config
import net.prosavage.factionsx.persist.config.Config.factionCreationFillChunkBorderOnFirstClaim
import net.prosavage.factionsx.persist.config.Config.factionCreationFillChunkBorderOnFirstClaimCoolDownSeconds
import net.prosavage.factionsx.persist.config.Config.factionCreationFillChunkBorderOnFirstClaimPassableType
import net.prosavage.factionsx.persist.config.Config.factionCreationFillChunkBorderOnFirstClaimType
import net.prosavage.factionsx.persist.config.Config.factionFirstClaimAutoSetHome
import net.prosavage.factionsx.persist.config.Config.factionFirstClaimExecuteCommands
import net.prosavage.factionsx.persist.config.Config.factionFirstClaimExecuteCommandsCoolDownSeconds
import net.prosavage.factionsx.persist.config.Config.worldGuardRegionAllowedClaimInWorlds
import net.prosavage.factionsx.persist.config.EconConfig
import net.prosavage.factionsx.persist.config.ProtectionConfig
import net.prosavage.factionsx.persist.data.FLocation
import net.prosavage.factionsx.persist.data.Grid
import net.prosavage.factionsx.persist.data.getFLocation
import net.prosavage.factionsx.persist.data.wrappers.getDataLocation
import net.prosavage.factionsx.util.Relation
import net.prosavage.factionsx.util.format
import net.prosavage.factionsx.util.isAnyAir
import org.bukkit.*
import kotlin.math.roundToInt

object GridManager {
    fun claim(faction: Faction, fLocation: FLocation, fPlayer: FPlayer? = null) {
        Grid.claimGrid[fLocation] = faction.id
        if (!hasAdjacentClaimOwnedBySameFaction(fLocation, faction))
            faction.unconnectedClaimAmt++
        faction.claimAmt++

        // check for one claim amount
        if (faction.hasClaimedOnce) {
            return
        }

        // modify value of hasClaimedOnce
        faction.hasClaimedOnce = true

        // attempt to set home if option is enabled.
        if (factionFirstClaimAutoSetHome) {
            Bukkit.getScheduler().runTaskAsynchronously(FactionsX.instance, Runnable {
                val snapshot = fLocation.getChunk()?.chunkSnapshot ?: return@Runnable
                var location: Location? = null

                for (y in 253 downTo 0) {
                    val top = snapshot.getBlockType(8, y + 2, 7)
                    val middle = snapshot.getBlockType(8, y + 1, 7)
                    val bottom = snapshot.getBlockType(8, y, 7)

                    if (!top.isAnyAir() || !middle.isAnyAir() || bottom.isAnyAir()) {
                        continue
                    }

                    location = Location(
                        Bukkit.getWorld(fLocation.world),
                        (fLocation.x * 16 + 8).toDouble(),
                        y + 1.0,
                        (fLocation.z * 16 + 7).toDouble()
                    )
                }

                if (location == null) {
                    return@Runnable
                }

                fPlayer?.lastLocation?.let {
                    location.yaw = it.yaw
                    location.pitch = it.pitch
                }

                faction.home = location.getDataLocation(withYaw = true, withPitch = true)
            })
        }

        // execute commands if present
        if (fPlayer != null && (fPlayer.latestCreationCommandsExecution + factionFirstClaimExecuteCommandsCoolDownSeconds.times(1000)) <= System.currentTimeMillis()) {
            fPlayer.latestCreationCommandsExecution = System.currentTimeMillis()
            factionFirstClaimExecuteCommands.forEach {
                Bukkit.dispatchCommand(
                    Bukkit.getConsoleSender(),
                    it.replace("{player}", fPlayer.name).replace("{faction}", faction.tag)
                )
            }
        }

        // make sure feature is enabled
        if (
            !factionCreationFillChunkBorderOnFirstClaim
            || fPlayer == null
            || (fPlayer.latestCreationChunkBorderFill + factionCreationFillChunkBorderOnFirstClaimCoolDownSeconds.times(1000)) > System.currentTimeMillis()
        ) return

        // set faction player's latest border fill
        fPlayer.latestCreationChunkBorderFill = System.currentTimeMillis()

        // set barrier of specific block at chunk borders
        Bukkit.getScheduler().runTaskAsynchronously(FactionsX.instance, Runnable {
            val snapshot = fLocation.getChunk()?.chunkSnapshot ?: return@Runnable
            val locations = highestBlocksYAt(snapshot)

            Bukkit.getScheduler().runTask(FactionsX.instance, Runnable {
                locations.forEach { location -> location.block.type = factionCreationFillChunkBorderOnFirstClaimType.parseMaterial()!! }
            })
        })
    }

    private fun highestBlocksYAt(snapshot: ChunkSnapshot): Set<Location> {
        val locations = hashSetOf<Location>()
        val world = Bukkit.getWorld(snapshot.worldName)

        for (i in 0..15) for (y in 254 downTo 0) {
            val first = checkBlockFromSnapshot(0, y, i, snapshot)
            val second = checkBlockFromSnapshot(i, y, 0, snapshot)
            val third = checkBlockFromSnapshot(15, y, i, snapshot)
            val fourth = checkBlockFromSnapshot(i, y, 15, snapshot)

            val unsignedX = snapshot.x.shl(4).toDouble()
            val unsignedZ = snapshot.z.shl(4).toDouble()

            if (first.first) locations += Location(world, unsignedX, y + first.second, unsignedZ + i)
            if (second.first) locations += Location(world, unsignedX + i, y + second.second, unsignedZ)
            if (third.first) locations += Location(world, unsignedX + 15, y + third.second, unsignedZ + i)
            if (fourth.first) locations += Location(world, unsignedX + i, y + fourth.second, unsignedZ + 15)
        }

        return locations
    }

    private fun checkBlockFromSnapshot(x: Int, y: Int, z: Int, snapshot: ChunkSnapshot): Pair<Boolean, Double> {
        val above = XMaterial.matchXMaterial(snapshot.getBlockType(x, y + 1, z))
        val below = XMaterial.matchXMaterial(snapshot.getBlockType(x, y, z))

        val condition = above in factionCreationFillChunkBorderOnFirstClaimPassableType && below != XMaterial.AIR
        return condition to if (below in factionCreationFillChunkBorderOnFirstClaimPassableType) 0.0 else 1.0
    }

    fun getAllFactionClaimsSorted(): ListMultimap<Long, FLocation> {
        val claims = ArrayListMultimap.create<Long, FLocation>()
        Grid.claimGrid.forEach { (floc, id) -> claims.put(id, floc) }
        return claims
    }

    fun getAllClaims(faction: Faction): Set<FLocation> {
        return Grid.claimGrid.filter { (_, facId) -> facId == faction.id }.keys
    }

    fun getFactionAt(fLocation: FLocation): Faction {
        return FactionManager.getFaction(Grid.claimGrid[fLocation] ?: FactionManager.WILDERNESS_ID)
    }

    fun getFactionAt(chunk: Chunk): Faction {
        return getFactionAt(getFLocation(chunk))
    }

    fun unclaim(faction: Faction, fLocation: FLocation) {
        fLocation.resetMetadata()
        Grid.claimGrid.remove(fLocation)
        if (faction.home != null && faction.home!!.getLocation().chunk == fLocation.getChunk()) {
            faction.home = null
            faction.message(Message.commandHomeInvalid)
        }
        faction.warps.values.removeIf { getFLocation(it.dataLocation.getLocation()) == fLocation }
        if (!hasAdjacentClaimOwnedBySameFaction(fLocation, faction))
            faction.unconnectedClaimAmt--
        faction.claimAmt--
    }

    enum class ClaimError {
        OUTSIDE_BORDER,
        ALREADY_OWN,
        ENOUGH_POWER,
        CLAIM_LIMIT,
        INVALID_WORLD,
        NOT_ENOUGH_POWER,
        PLUGIN,
        SYSTEM_FACTION,
        REGION,
        NOT_OUTSIDE,
        ADJACENT_CLAIM,
        NOT_CONNECTED,
        UNCONNECTED_CLAIM_LIMIT,
        CLAIM_TOO_CLOSE,
        OUTSIDE_REGION
    }

    val claimErrors = hashMapOf(
            ClaimError.PLUGIN to "another plugin &6denying&7 it.",
            ClaimError.ALREADY_OWN to "your faction &6already&7 owning the land.",
            ClaimError.OUTSIDE_BORDER to "claims being &6outside&7 the WorldBorder claim buffer.",
            ClaimError.ENOUGH_POWER to "owner having &6enough&7 power to &6keep&7 it.",
            ClaimError.CLAIM_LIMIT to "server not allowing you to own &6anymore&7 claims.",
            ClaimError.NOT_ENOUGH_POWER to "your faction not having enough power to claim more land.",
            ClaimError.INVALID_WORLD to "claiming being disabled in this world.",
            ClaimError.SYSTEM_FACTION to "being owned by a &6system&7 faction.",
            ClaimError.REGION to "being protected by &nWorldGuard&7.",
            ClaimError.NOT_OUTSIDE to "claim being in the middle.",
            ClaimError.ADJACENT_CLAIM to "claim adjacent to another faction.",
            ClaimError.NOT_CONNECTED to "claim not connected to faction claims.",
            ClaimError.UNCONNECTED_CLAIM_LIMIT to "server not allowing you to own &6anymore unconnected&7 claims.",
            ClaimError.CLAIM_TOO_CLOSE to "claim being too close to another faction.",
            ClaimError.OUTSIDE_REGION to "being outside of claim &nregion&6."
    )

    fun claimLand(faction: Faction, vararg flocations: FLocation, fplayer: FPlayer, asAdmin: Boolean = false) {
        val successfulClaims = mutableListOf<FLocation>()
        val pluginManager = Bukkit.getPluginManager()
        if (Config.minFactionMembers) {
            if (faction.getMembers().size < Config.minFactionMembersToClaim) {
                fplayer.message(Message.commandClaimNotEnoughMembers, Config.minFactionMembersToClaim.toString())
                return
            }
        }

        val claimFailureMap = mutableMapOf<ClaimError, Int>()
        var totalFailures = 0

        for (flocation in flocations) {
            val facAt = flocation.getFaction()
            val power = faction.getPower().roundToInt()

            if (!asAdmin && !fplayer.inBypass) {
                val claimsLeft = flocations.size - successfulClaims.size
                if (Config.maxClaimLimit != -1 && faction.claimAmt >= faction.getMaxClaims()) {
//                    fplayer.message(Message.commandClaimMaxAmt, Config.maxClaimLimit.toString())
                    claimFailureMap.putIfAbsent(ClaimError.CLAIM_LIMIT, 0)
                    claimFailureMap[ClaimError.CLAIM_LIMIT] = claimFailureMap[ClaimError.CLAIM_LIMIT]!! + claimsLeft
                    totalFailures += claimsLeft
                    break
                }

                val worldIsBlacklisted = Config.worldsNoClaiming.contains(flocation.world)
                if ((Config.turnBlacklistWorldsToWhitelist && !worldIsBlacklisted) || (!Config.turnBlacklistWorldsToWhitelist && worldIsBlacklisted)) {
//                    fplayer.message(Message.commandClaimWorldDisabled, flocation.world)
                    claimFailureMap.putIfAbsent(ClaimError.INVALID_WORLD, 0)
                    claimFailureMap[ClaimError.INVALID_WORLD] = claimFailureMap[ClaimError.INVALID_WORLD]!! + claimsLeft
                    totalFailures += claimsLeft
                    break
                }

                val allClaimSize = getAllClaims(faction).size
                if (power <= allClaimSize + successfulClaims.size) {
//                    fplayer.message(Message.commandClaimCannotAfford)
                    claimFailureMap.putIfAbsent(ClaimError.NOT_ENOUGH_POWER, 0)
                    claimFailureMap[ClaimError.NOT_ENOUGH_POWER] = claimFailureMap[ClaimError.NOT_ENOUGH_POWER]!! + claimsLeft
                    totalFailures += claimsLeft
                    break
                }

                if (!worldGuard.isNotPresent) {
                    val regionId = worldGuardRegionAllowedClaimInWorlds[flocation.world]
                    val isInRegion = if (regionId == null) false else worldGuard.hasRegion(flocation.world, flocation.x.toInt(), flocation.z.toInt(), regionId)

                    if (regionId != null && !isInRegion) {
                        claimFailureMap.compute(ClaimError.OUTSIDE_REGION) { _, fails -> fails?.plus(1) ?: 1 }
                        totalFailures++
                        continue
                    }

                    val allRegions = worldGuard.getRegionsAt(flocation.world, flocation.x.toInt(), flocation.z.toInt())
                    if (!Config.allowClaimInWorldGuardRegion && allRegions.size > 0 && !isInRegion) {
                        claimFailureMap.compute(ClaimError.REGION) { _, fails -> fails?.plus(1) ?: 1 }
                        totalFailures++
                        continue
                    }
                }

                if (facAt.isWarzone() || facAt.isSafezone()) {
//                    fplayer.message(Message.commandClaimDeniedSystemFaction)
                    claimFailureMap.putIfAbsent(ClaimError.CLAIM_LIMIT, 0)
                    claimFailureMap[ClaimError.CLAIM_LIMIT] = claimFailureMap[ClaimError.CLAIM_LIMIT]!! + 1
                    totalFailures++
                    continue
                }

                if (!facAt.isWilderness() && (facAt != faction && facAt.getPower() >= getAllClaims(facAt).size || Config.disableOverclaimMechanism)) {
//                    fplayer.message(Message.commandClaimDenied, facAt.tag)
                    claimFailureMap.putIfAbsent(ClaimError.ENOUGH_POWER, 0)
                    claimFailureMap[ClaimError.ENOUGH_POWER] = claimFailureMap[ClaimError.ENOUGH_POWER]!! + 1
                    totalFailures++
                    continue
                }

                if (flocation.outsideBorder(Config.claimWorldBorderBuffer)) {
                    claimFailureMap.putIfAbsent(ClaimError.OUTSIDE_BORDER, 0)
                    claimFailureMap[ClaimError.OUTSIDE_BORDER] = claimFailureMap[ClaimError.OUTSIDE_BORDER]!! + 1
                    totalFailures++
                    continue
                }

                if (facAt == faction) {
                    claimFailureMap.putIfAbsent(ClaimError.ALREADY_OWN, 0)
                    claimFailureMap[ClaimError.ALREADY_OWN] = claimFailureMap[ClaimError.ALREADY_OWN]!! + 1
                    totalFailures++
                    continue
                }

                if (!facAt.isWilderness() && ProtectionConfig.denyConnectedOverClaim && isClaimConnectedAt(fplayer, faction, flocation)) {
                    claimFailureMap.compute(ClaimError.NOT_OUTSIDE) { _, fails -> fails?.plus(1) ?: 1 }
                    totalFailures++
                    continue
                }

                if (faction.claimAmt > 0 && ProtectionConfig.denyUnConnectedClaim && !hasAdjacentClaimOwnedBySameFaction(flocation, faction)) {
                    if (
                        ProtectionConfig.maxUnConnectedClaimsAllowed <= 0 || !(
                            ProtectionConfig.allowUnconnectedOverClaimOfEnemies
                            && !facAt.isWilderness()
                            && faction.getRelationTo(facAt) == Relation.ENEMY
                            && facAt.getPower() < getAllClaims(facAt).size
                            && !Config.disableOverclaimMechanism
                        )
                    ) {
                        claimFailureMap.compute(ClaimError.NOT_CONNECTED) { _, fails -> fails?.plus(1) ?: 1 }
                        totalFailures++
                        continue
                    } else if (faction.unconnectedClaimAmt >= ProtectionConfig.maxUnConnectedClaimsAllowed) {
                        claimFailureMap.compute(ClaimError.UNCONNECTED_CLAIM_LIMIT) { _, fails -> fails?.plus(1) ?: 1 }
                        totalFailures++
                        continue
                    }
                }

                if (ProtectionConfig.denyAdjacentClaimsToOtherFactions && hasAdjacentClaimOwnedByAnotherFaction(fplayer, flocation, faction)) {
                    claimFailureMap.compute(ClaimError.ADJACENT_CLAIM) { _, fails -> fails?.plus(1) ?: 1 }
                    totalFailures++
                    continue
                }

                if (ProtectionConfig.minDistanceFromOtherFactions > 0 && hasNearClaimOwnedByAnotherFaction(flocation, faction, ProtectionConfig.minDistanceFromOtherFactions)) {
                    claimFailureMap.compute(ClaimError.CLAIM_TOO_CLOSE) { _, fails -> fails?.plus(1) ?: 1 }
                    totalFailures++
                    continue
                }

            }

            val factionPreClaimEvent = FactionPreClaimEvent(faction, facAt, flocation, fplayer, asAdmin)
            pluginManager.callEvent(factionPreClaimEvent)

            if (factionPreClaimEvent.isCancelled) {
                claimFailureMap.putIfAbsent(ClaimError.PLUGIN, 0)
                claimFailureMap[ClaimError.PLUGIN] = claimFailureMap[ClaimError.PLUGIN]!! + 1
                continue
            }

            if (faction != facAt) {
                val factionOverclaimEvent = FactionOverclaimEvent(faction, facAt, flocation)
                pluginManager.callEvent(factionOverclaimEvent)

                if (factionOverclaimEvent.isCancelled) {
                    claimFailureMap.putIfAbsent(ClaimError.PLUGIN, 0)
                    claimFailureMap[ClaimError.PLUGIN] = claimFailureMap[ClaimError.PLUGIN]!! + 1
                    continue
                }
            }

            if (faction.home == null && Config.setHomeOnClaimIfNotSetYet) {
                val player = fplayer.getPlayer()
                if (player != null) {
                    if (player.location.chunk == flocation.getChunk())
                        faction.home = player.location.getDataLocation()
                    else
                        faction.home = Bukkit.getWorld(flocation.world)!!.getHighestBlockAt(flocation.x.toInt(), flocation.z.toInt()).location.getDataLocation()
                    fplayer.message(Message.commandClaimHomeAutoSet, faction.home!!.x.toString(), faction.home!!.y.toString(), faction.home!!.z.toString())
                }
            }
            successfulClaims.add(flocation)
        }

        val successSize = successfulClaims.size
        var fullPrice = 0.0
        if (EconConfig.economyEnabled && successSize > 0 && !asAdmin) {
            val priceForOneClaim = EconConfig.claimLandCost
            if (priceForOneClaim != 0.0) {
                fullPrice = successSize * priceForOneClaim
                val response = fplayer.takeMoneyFromFactionBank(fullPrice, Faction.BankLogType.COMMAND);
                if (!response.transactionSuccess()) {
                    fplayer.message(Message.commandClaimNotEnoughMoney, fullPrice.format(), successSize.format())
                    return
                }
//                faction.bank.command(fplayer, fullPrice)
            }

        }

        successfulClaims.stream().forEach { floc ->
            claim(faction, floc, fplayer)
        }

        if (successSize > 0) {
            if (EconConfig.economyEnabled) {
                fplayer.message(
                        Message.commandClaimSuccessEconEnabled,
                        successSize.format(),
                        fullPrice.format()
                )
                faction.message(
                        Message.commandClaimSuccessEconEnabledFactionNotify,
                        fplayer.name,
                        successSize.format(),
                        fullPrice.format(),
                        excludeFPlayers = listOf(fplayer)
                )
            } else {
                fplayer.message(
                        Message.commandClaimSuccess,
                        successSize.format()
                )
                faction.message(
                        Message.commandClaimSuccessFactionNotify,
                        fplayer.name,
                        successSize.format(),
                        excludeFPlayers = listOf(fplayer)
                )
            }
        }

        if (totalFailures > 0) {
            fplayer.message(Message.commandClaimFailuresHeader, totalFailures.toString())
            for (failureReason in claimFailureMap.keys) {
                fplayer.message(Message.commandClaimFailuresFormat,
                        // we can use the claimErrors map if the lang file is missing the entry due to updates :)
                        claimFailureMap[failureReason].toString(), Message.commandClaimFailureReasons[failureReason]
                        ?: claimErrors[failureReason]!!)
            }
        }


    }


    private fun isClaimConnectedAt(fPlayer: FPlayer, faction: Faction, fLocation: FLocation): Boolean {
        val world = fPlayer.getPlayer()!!.world
        return fLocation.getAdjacentClaims(world).none {
            with(getFactionAt(it)) {
                this.isWilderness() || this == faction
            }
        }
    }


    private fun hasNearClaimOwnedByAnotherFaction(fLocation: FLocation, faction: Faction, radius: Int): Boolean {
        val limit = (radius - 1) * 2
        var len = -1
        var current = 0
        var x = fLocation.x
        var z = fLocation.z
        var zl = false
        var neg = false
        while (current < limit) {
            if (current < len) {
                current++
            } else {
                current = 0
                zl = zl xor true

                if (zl) {
                    neg = neg xor true
                    len++
                }
            }

            if (zl) {
                z += if (neg) -1 else 1
            } else {
                x += if (neg) -1 else 1
            }

            val location = FLocation(x, z, fLocation.world)
            if (location.getFaction() != faction && !location.getFaction().isSystemFaction()) return true
        }
        return false
    }

    private fun hasAdjacentClaimOwnedBySameFaction(fLocation: FLocation, claimingFac: Faction): Boolean {
        val world = Bukkit.getWorld(fLocation.world) !!
        val adjacentFactions = fLocation.getAdjacentClaims(world).map { loc -> loc.getFaction() }
        return adjacentFactions.any { faction -> !faction.isWilderness() && faction == claimingFac }
    }

    private fun hasAdjacentClaimOwnedByAnotherFaction(fPlayer: FPlayer, fLocation: FLocation, claimingFac: Faction): Boolean {
        val world = fPlayer.getPlayer()!!.world
        val adjacentFactions = fLocation.getAdjacentClaims(world).map { loc -> loc.getFaction() }
        return adjacentFactions.any { faction -> !faction.isWilderness() && faction != claimingFac }
    }

    private fun FLocation.getAdjacentClaims(world: World): Set<FLocation> {
        return setOf(
                FLocation((x - 1), z, world.name),
                FLocation(x, (z - 1), world.name),
                FLocation((x - 1), z - 1, world.name),
                FLocation((x - 1), z + 1, world.name),
                FLocation((x + 1), z - 1, world.name),
                FLocation((x + 1), z, world.name),
                FLocation(x, (z + 1), world.name),
                FLocation(x + 1, (z + 1), world.name)
        )
    }

    // TODO: make unclaim land have breakdown functional
    fun unClaimLand(faction: Faction, vararg flocations: FLocation, fplayer: FPlayer, asAdmin: Boolean = false) {
        val successfulClaims = mutableListOf<FLocation>()
        val pluginManager = Bukkit.getPluginManager()
        for (flocation in flocations) {
            val facAt = flocation.getFaction()

            if (!asAdmin && !fplayer.inBypass) {
                if (facAt != faction) {
                    continue
                }
                if (facAt.isWarzone() || facAt.isSafezone()) {
                    break
                }
            }

            val factionPreClaimEvent = FactionUnClaimEvent(faction, flocation, fplayer, asAdmin)
            pluginManager.callEvent(factionPreClaimEvent)

            if (factionPreClaimEvent.isCancelled) continue
            successfulClaims.add(flocation)
        }

        successfulClaims.stream().forEach { floc -> unclaim(faction, floc) }
        fplayer.message(
                Message.commandUnclaimSuccess,
                successfulClaims.size.toString()
        )
        faction.message(
                Message.commandUnclaimSuccessFactionNotify,
                fplayer.name,
                successfulClaims.size.toString(),
                excludeFPlayers = listOf(fplayer)
        )
    }


    fun claimSpiral(radius: Int, fPlayer: FPlayer, faction: Faction, origin: FLocation, asAdmin: Boolean = false) {
        val limit = (radius - 1) * 2
        var len = -1
        var current = 0
        var x = origin.x
        var z = origin.z
        var zl = false
        var neg = false
        val claims = mutableSetOf<FLocation>()
        claims.add(FLocation(x, z, origin.world))
        while (current < limit) {
            if (current < len) {
                current++
            } else {
                current = 0
                zl = zl xor true

                if (zl) {
                    neg = neg xor true
                    len++
                }
            }

            if (zl) {
                z += if (neg) -1 else 1
            } else {
                x += if (neg) -1 else 1
            }
            claims.add(FLocation(x, z, origin.world))
        }
        if (radius > 1) claims.remove(claims.last())
        claimLand(faction, *claims.toTypedArray(), fplayer = fPlayer, asAdmin = asAdmin)
    }

    fun unclaimSpiral(radius: Int, fPlayer: FPlayer, faction: Faction, origin: FLocation, asAdmin: Boolean = false) {
        val limit = (radius - 1) * 2
        var len = -1
        var current = 0
        var x = origin.x
        var z = origin.z
        var zl = false
        var neg = false
        val claims = mutableSetOf<FLocation>()
        claims.add(FLocation(x, z, origin.world))
        while (current < limit) {
            if (current < len) {
                current++
            } else {
                current = 0
                zl = zl xor true

                if (zl) {
                    neg = neg xor true
                    len++
                }
            }

            if (zl) {
                z += if (neg) -1 else 1
            } else {
                x += if (neg) -1 else 1
            }

            val location = FLocation(x, z, origin.world)
            if (location.getFaction() != faction) continue
            claims.add(location)
        }
        if (radius > 1) claims.remove(claims.last())
        unClaimLand(faction, *claims.toTypedArray(), fplayer = fPlayer, asAdmin = asAdmin)
    }
}