package net.prosavage.factionsx.persist.data

import com.cryptomorin.xseries.XMaterial
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.google.gson.JsonPrimitive
import net.prosavage.baseplugin.serializer.Serializer
import net.prosavage.factionsx.FactionsX
import net.prosavage.factionsx.core.Faction
import net.prosavage.factionsx.manager.FactionManager
import net.prosavage.factionsx.manager.GridManager
import net.prosavage.factionsx.upgrade.Upgrade
import net.prosavage.factionsx.util.logColored
import org.apache.commons.lang.math.NumberUtils
import org.bukkit.Bukkit
import org.bukkit.Chunk
import org.bukkit.Location
import org.bukkit.WorldBorder
import java.io.File
import java.nio.charset.Charset
import java.util.*
import kotlin.collections.HashMap

object Factions {

    @Transient
    private val instance = this

    var nextFactionId = 0L

    var factions = HashMap<Long, Faction>()

    fun save() {
        Serializer(true).save(instance)
    }

    fun load(factionsx: FactionsX) {
        // migrate
        migrate()

        // serialize and initialize
        Serializer(true).load(instance, Factions::class.java, "factions")
        FactionManager.initializeFactions(factionsx)

        // Assign factions that don't have a creation date set with a it's leaders first time played date.
        factions.values.filter { f -> f.creationDate == 0L && !f.isSystemFaction() }.forEach { f ->
            val leader = Bukkit.getOfflinePlayer(f.ownerId!!)
            f.creationDate = leader.firstPlayed
        }
    }

    private fun migrate() {
        // log attempt
        logColored("&eAttempting to migrate factions...")

        // make sure file is present
        val file = File(FactionsX.instance.dataFolder.resolve("data"), "factions.json")
        if (!file.exists()) {
            return
        }

        // entire element
        val wholeElement = JsonParser().parse(file.reader(charset = Charset.defaultCharset()))
        if (wholeElement == null || !wholeElement.isJsonObject) {
            return
        }

        // get our objects
        val wholeObject = wholeElement.asJsonObject
        val factionsObject = wholeObject.getAsJsonObject("factions")
        var wasModified = false

        // loop through all factions and modify
        for ((factionId, _) in factionsObject.entrySet().toSet()) {
            val factionObject = factionsObject.getAsJsonObject(factionId)
            var didModifyFaction = false

            // creation date
            if (!NumberUtils.isNumber(factionObject["creationDate"].asString)) {
                factionObject.remove("creationDate")
                factionObject.add("creationDate", JsonPrimitive(System.currentTimeMillis()))
                didModifyFaction = true
            }

            // relation requests
            if (!factionObject.has("relationRequests")) {
                factionObject.add("relationRequests", JsonObject())
                didModifyFaction = true
            }

            // has claimed once
            if (!factionObject.has("hasClaimedOnce")) {
                factionObject.add("hasClaimedOnce", JsonPrimitive(Grid.claimGrid.keys.contains(factionId)))
                didModifyFaction = true
            }

            // relations
            val relations = factionObject.getAsJsonObject("relations")
            for ((relationId, _) in relations.entrySet().toSet()) {
                val relationElement = relations.get(relationId)

                if (!relationElement.isJsonObject) {
                    if (!didModifyFaction) {
                        didModifyFaction = true
                    }

                    relations.remove(relationId)
                    relations.add(relationId, JsonObject().apply {
                        addProperty("relation", relationElement.asString)
                        addProperty("whoRequested", -1)
                    })
                }

                if (!factionsObject.has(relationId)) {
                    relations.remove(relationId)
                    didModifyFaction = true
                }
            }

            // if the faction has been modified, change state
            if (didModifyFaction) {
                factionsObject.remove(factionId)
                factionsObject.add(factionId, factionObject)
                if (!wasModified) wasModified = true
            }
        }

        // write
        if (wasModified) {
            wholeObject.remove("factions")
            wholeObject.add("factions", factionsObject)

            file.writeText(wholeObject.toString())
            logColored("&aMigration of factions has finished successfully.")
            return
        }

        // no modification made
        logColored("&aThere was no migration needed for factions.")
    }
}

data class FLocation(val x: Long, val z: Long, val world: String) {

    var name: String
        get() = getMetadata().name
        set(value) {
            getMetadata().name = value
        }

    var icon: XMaterial
        get() = getMetadata().icon
        set(value) {
            getMetadata().icon = value
        }

    private fun getMetadata(): Faction.FLocationMetadata {
        return this.getFaction().getFLocationMetadata(this)
    }

    fun getAccessPoint(): Faction.FLocationAccessPoint = getMetadata().access

    fun resetMetadata() {
        getMetadata().reset()
    }

    fun setUpgrade(upgrade: Upgrade, level: Int) {
        getMetadata().setUpgrade(upgrade, level)
    }

    fun getUpgrade(upgrade: Upgrade): Int {
        return getMetadata().getUpgrade(upgrade)
    }

    fun isUpgraded(upgrade: Upgrade): Boolean {
        return getMetadata().isUpgraded(upgrade)
    }

    fun getChunk(): Chunk? {
        return Bukkit.getWorld(world)?.getChunkAt(x.toInt(), z.toInt())
    }

    fun outsideBorder(claimBuffer: Int): Boolean {
        if (claimBuffer < 0) return false;
        val border: WorldBorder = Bukkit.getWorld(world)?.worldBorder ?: return false
        val limit: Int = (border.size.toInt() shr 5) - claimBuffer
        val xDifference: Long = (border.center.x % 16).toLong() - x
        val zDifference: Long = (border.center.z % 16).toLong() - z
        return xDifference > limit || zDifference > limit || -xDifference > limit - 1 || -zDifference > limit - 1
    }

    fun getFaction(): Faction {
        return GridManager.getFactionAt(this)
    }

}


fun getFLocation(location: Location): FLocation {
    return FLocation(
            location.chunk.x.toLong(),
            location.chunk.z.toLong(),
            location.world!!.name
    )
}

fun getFLocation(chunk: Chunk): FLocation {
    return FLocation(chunk.x.toLong(), chunk.z.toLong(), chunk.world.name)
}
