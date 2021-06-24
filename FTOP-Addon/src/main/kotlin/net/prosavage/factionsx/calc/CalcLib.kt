package net.prosavage.factionsx.calc

import com.cryptomorin.xseries.XMaterial
import io.papermc.lib.PaperLib
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import net.prosavage.factionsx.FTOPAddon
import net.prosavage.factionsx.FTOPAddon.Companion.spawnerProvider
import net.prosavage.factionsx.FactionsX
import net.prosavage.factionsx.calc.progress.BlockProgress
import net.prosavage.factionsx.calc.progress.ProgressLocation
import net.prosavage.factionsx.calc.progress.SpawnerProgress
import net.prosavage.factionsx.core.Faction
import net.prosavage.factionsx.hook.ShopGUIPlusHook
import net.prosavage.factionsx.manager.FactionManager
import net.prosavage.factionsx.manager.GridManager
import net.prosavage.factionsx.persist.BlockWorth
import net.prosavage.factionsx.persist.FTOPConfig
import net.prosavage.factionsx.persist.FTOPConfig.progressiveMaterialsMilliseconds
import net.prosavage.factionsx.persist.FTOPConfig.progressiveSpawnersMilliseconds
import net.prosavage.factionsx.util.PlaceholderAction
import net.prosavage.factionsx.util.getChunkSnapshotBlockType
import org.bukkit.Bukkit
import org.bukkit.ChunkSnapshot
import org.bukkit.block.BlockState
import org.bukkit.block.CreatureSpawner
import org.bukkit.entity.EntityType
import java.util.*
import kotlin.system.measureTimeMillis

/**
 * Recalculate a [Faction]'s top value.
 */
fun Faction.recalculate(): Response {
    val oldResponse = FTOPAddon.factionValues.values.find { it.faction == this.id }
    var price = 0.0
    val progressive: EnumMap<XMaterial, HashSet<BlockProgress>> =
        oldResponse?.progressive ?: EnumMap(XMaterial::class.java)
    val spawners: EnumMap<EntityType, Int> = EnumMap(EntityType::class.java)
    val otherMaterials: EnumMap<XMaterial, Int> = EnumMap(XMaterial::class.java)
    val isPaper13OrHigher = PaperLib.isPaper() && XMaterial.isNewVersion()

    runBlocking {
        val chunks = hashSetOf<Pair<ChunkSnapshot, Array<out BlockState>>>()
        val claims = GridManager.getAllClaims(this@recalculate)

        var chunkCounter = 1
        for (claim in claims) {
            val world = Bukkit.getWorld(claim.world) ?: continue
            Bukkit.getScheduler().runTask(FactionsX.instance, Runnable {
                PaperLib.getChunkAtAsync(world, claim.x.toInt(), claim.z.toInt())
                    .thenAccept { chunk -> chunks += chunk.chunkSnapshot to chunk.tileEntities }
            })

            if (chunkCounter++ % 25 == 0 && !isPaper13OrHigher) {
                delay(FTOPConfig.chunkLoadDelayInMilliseconds)
            }
        }

        while (claims.size > chunks.size) delay(1)

        val useNewGetBlockType = XMaterial.getVersion() >= 12.0
        for (data in chunks) {
            val snapshot = data.first
            val chunkX = snapshot.x
            val chunkZ = snapshot.z

            for (sy in 0 until 16) {
                if (snapshot.isSectionEmpty(sy)) continue

                for (x in 0 until 16) for (y in 0 until 16) for (z in 0 until 16) {
                    val blockType = getChunkSnapshotBlockType(
                        useNewGetBlockType,
                        snapshot, x, sy * 16 + y, z
                    )!!

                    val material = XMaterial.matchXMaterial(blockType)
                    if (material !in FTOPConfig.allowedBlocksToCalculate) {
                        continue
                    }

                    val thisPrice = getBlockPrice(material)
                    if (material !== XMaterial.SPAWNER) {
                        price += thisPrice
                    }

                    val tillFinishedMs = progressiveMaterialsMilliseconds[material]
                    if (material === XMaterial.SPAWNER || tillFinishedMs == null) {
                        otherMaterials.compute(material) { _, amount -> amount?.plus(1) ?: 1 }
                        continue
                    }

                    val oldCollection = progressive.getOrPut(material) { hashSetOf() }
                    val progressLocation = ProgressLocation(chunkX, chunkZ, x, y, z)
                    val oldProgress = oldCollection?.find { it.location.isSame(progressLocation) }

                    if (oldProgress != null) {
                        if (oldProgress.ownerThen == this@recalculate.id) {
                            continue
                        }

                        oldCollection.remove(oldProgress)
                    }

                    oldCollection.add(BlockProgress(material, this@recalculate.id, progressLocation, thisPrice, tillFinishedMs))
                }
            }

            for (spawner in data.second) {
                if (spawner !is CreatureSpawner) continue

                val spawnerType = spawner.spawnedType
                var spawnerValue = BlockWorth.spawnerValues[spawnerType] ?: 0.0

                var finishedValuePrep = false
                var stackedAmount = 1

                if (spawnerProvider == null) {
                    price += spawnerValue
                    finishedValuePrep = true
                } else Bukkit.getScheduler().runTask(FactionsX.instance, Runnable {
                    val stacked = spawnerProvider!!.getAmount(spawner)
                    stackedAmount = stacked
                    spawnerValue *= stacked
                    price += spawnerValue
                    finishedValuePrep = true
                })

                // make sure prep has finished
                while (!finishedValuePrep) {
                    delay(1)
                }

                spawners.compute(spawnerType) { _, amount ->
                    amount?.plus(stackedAmount) ?: stackedAmount
                }

                val tillFinishedMs = progressiveSpawnersMilliseconds[spawnerType] ?: continue
                val oldCollection = progressive.getOrPut(XMaterial.SPAWNER) { hashSetOf() }
                val progressLocation = ProgressLocation(chunkX, chunkZ, spawner.x, spawner.y, spawner.z)
                val oldProgress = oldCollection?.find { it.location.isSame(progressLocation) }

                if (oldProgress != null) {
                    if (oldProgress.ownerThen == this@recalculate.id) {
                        continue
                    }

                    oldCollection.remove(oldProgress)
                }

                oldCollection.add(
                    SpawnerProgress(
                        spawnerType,
                        this@recalculate.id,
                        progressLocation,
                        spawnerValue,
                        tillFinishedMs
                    )
                )
            }
        }
    }

    val percentageLoss = PlaceholderAction.ofValuePercentage(this)?.run {
        price -= (price / 100) * this
        this
    } ?: 0

    val bankBalance = this.bank.amount
    val otherMaterialPrice = otherMaterials.map { (BlockWorth.values[it.key] ?: 0.0) * it.value }.sum()

    return Response(
        this.id,
        if (FTOPConfig.countFactionBankBalance) bankBalance else 0.0,
        percentageLoss,
        progressive,
        spawners,
        otherMaterials,
        otherMaterialPrice,
        otherMaterialPrice + progressive.map { p -> p.value.map { it.value }.sum() }.sum()
    ).also { it.recalculate() }
}

/**
 * Recalculate all present [Faction].
 */
fun recalculateAll(then: (Double, Set<Response>) -> Unit) {
    GlobalScope.launch {
        val values = mutableSetOf<Response>()

        val approximateTime = measureTimeMillis {
            for (faction in FactionManager.getFactions()) {
                if (faction.isSystemFaction() || faction.claimAmt <= 0) continue
                val calculationInfo = faction.recalculate()
                values += calculationInfo
            }
        }

        FTOPAddon.latestCalc = System.currentTimeMillis()
        then(approximateTime.div(1000.0), values)
    }
}

/**
 * Get the corresponding price of a [XMaterial].
 */
internal fun getBlockPrice(material: XMaterial): Double {
    val itemStack = material.parseItem() ?: return 0.0

    if (FTOPConfig.useShopGUIPlusPricing) {
        val price: Double = with(ShopGUIPlusHook) {
            if (FTOPConfig.useShopGUIPlusSellPrice) getSellPrice(itemStack)
            else getBuyPrice(itemStack, 0.0) ?: 0.0
        }

        if (!FTOPConfig.fallbackToBlockValue || price != 0.0) return price
    }

    return BlockWorth.values[material] ?: 0.0
}