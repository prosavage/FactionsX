package net.prosavage.factionsx.listener

import net.prosavage.factionsx.FactionsX
import net.prosavage.factionsx.manager.PlayerManager
import net.prosavage.factionsx.persist.data.Players
import net.prosavage.factionsx.util.getFPlayer
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerLoginEvent
import org.bukkit.event.player.PlayerQuitEvent

class DataListener : Listener {
    @EventHandler
    fun playerJoined(event: PlayerLoginEvent) {
        if (event.result != PlayerLoginEvent.Result.ALLOWED) return

        val player = event.player
        val uuid = player.uniqueId.toString()

        Players.fplayers.compute(uuid) { _, fPlayer ->
            if (fPlayer == null) {
                FactionsX.instance.logger.info("${player.name}'s FPlayer instance was created")
                return@compute PlayerManager.createNewFPlayer(player, false)
            }

            PlayerManager.runLoginTasks(player)
            return@compute fPlayer
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    fun PlayerQuitEvent.onData() {
        val fPlayer = player.getFPlayer()
        fPlayer.timeAtLastLogin = System.currentTimeMillis()
    }
}