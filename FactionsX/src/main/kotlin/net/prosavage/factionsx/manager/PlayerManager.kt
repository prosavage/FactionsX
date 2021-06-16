package net.prosavage.factionsx.manager

import me.rayzr522.jsonmessage.JSONMessage
import net.prosavage.factionsx.core.FPlayer
import net.prosavage.factionsx.persist.color
import net.prosavage.factionsx.persist.data.Players
import net.prosavage.factionsx.util.getFPlayer
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import java.util.*

object PlayerManager {

    fun createNewFPlayer(player: Player, storeObject: Boolean): FPlayer {
        val newFPlayer = FPlayer(player.uniqueId, player.name)
        if (storeObject) Players.fplayers[player.uniqueId.toString()] = newFPlayer
        Players.nextFPlayerId++
        return newFPlayer
    }

    fun runLoginTasks(player: Player) {
        val fPlayer = getFPlayer(player.uniqueId)
        fPlayer!!.name = player.name
        if (fPlayer.teleportToSpawnOnLogin) {
            fPlayer.teleportToSpawnOnLogin = false
            player.teleport(player.world.spawnLocation)
        }
        // Other plugins like to interfere and we COULD use a delay to enable but that is not reliable, just disable fly instead.
        if (fPlayer.isFFlying) {
            fPlayer.setFly(false)
        }
        // update power
        fPlayer.updatePower(firstLogin = true)
    }


    fun getOnlineFPlayers(): List<FPlayer> {
        return Bukkit.getOnlinePlayers().map { player -> player.getFPlayer() }
    }

    fun getFPlayer(uuid: UUID?): FPlayer? {
        return Players.fplayers[uuid.toString()]
    }

    fun getFPlayer(player: Player): FPlayer {
        return Players.fplayers[player.uniqueId.toString()] ?: createNewFPlayer(player, true)
    }

    fun getFPlayer(name: String): FPlayer? {
        return Players.fplayers.values.find { fPlayer -> fPlayer.name.equals(name, ignoreCase = true) }
    }
}

fun actionbar(player: Player, message: String, vararg args: String) {
    if (message.isEmpty()) return
    JSONMessage.actionbar(color(String.format(message, *args)), player)
}