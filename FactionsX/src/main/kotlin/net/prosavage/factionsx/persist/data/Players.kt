package net.prosavage.factionsx.persist.data

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import net.prosavage.baseplugin.serializer.Serializer
import net.prosavage.factionsx.FactionsX
import net.prosavage.factionsx.core.FPlayer
import net.prosavage.factionsx.util.logColored
import java.io.File
import java.nio.charset.Charset
import java.util.concurrent.ConcurrentHashMap

object Players {

    @Transient
    private val instance = this

    var nextFPlayerId = 0L
    var fplayers = ConcurrentHashMap<String, FPlayer>()

    fun save() {
        Serializer(true).save(instance)
    }

    fun load() {
        migrate()
        Serializer(true).load(instance, Players::class.java, "players")
    }

    private fun migrate() {
        println("attempting pre-alts migration...")
        val file = File(File(FactionsX.instance.dataFolder, "data"), "players.json")
        if (!file.exists()) return
        val parser = JsonParser()
        val factionsInvitedToField = "factionsInvitedTo"
        val element = parser.parse(file.reader(Charset.defaultCharset()))
        if (element == null || !element.isJsonObject || element.asJsonObject.get("fplayers") == null) {
            logColored("&4invalid element or fplayers, skipping migration...")
            return
        }
        var modified = false
        val fplayers = element.asJsonObject.getAsJsonObject("fplayers");
        for (uuid in fplayers.keySet()) {
            // necessity
            var playerModified = false
            val player = fplayers.getAsJsonObject(uuid)
            val playerObject = player.asJsonObject
            val name = playerObject.get("name").asString

            // faction invited to
            var factionsInvitedTo = playerObject.get(factionsInvitedToField)
            if (factionsInvitedTo == null) factionsInvitedTo = JsonArray()

            if (factionsInvitedTo.isJsonArray) {
                println("migrating pre-alts invite for $name")
                val oldInvites = factionsInvitedTo.asJsonArray;
                val newInvites = JsonObject()
                oldInvites.forEach { factionid -> newInvites.addProperty(factionid.asString, false) }
                playerObject.remove(factionsInvitedToField)
                playerObject.add(factionsInvitedToField, newInvites)
                playerModified = true
            }

            // chunk message
            if (!playerObject.has("enabledChunkMessage")) {
                println("migrating chunk message for $name")
                playerObject.addProperty("enabledChunkMessage", true)
                playerModified = true
            }

            // max power boost
            if (!playerObject.has("maxPowerBoost")) {
                playerObject.addProperty("maxPowerBoost", playerObject.get("powerBoost").asDouble)
                playerObject.remove("powerBoost")
                playerObject.addProperty("powerBoost", 0.0)
            }

            // modification boolean
            if (playerModified && !modified) modified = true
        }

        if (modified) {
            println("some data was migrated... Writing new file.")
            file.writeText(element.toString())
            println("file written!")
        }
        println("migration finished.")
    }


}

