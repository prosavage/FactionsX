package net.prosavage.factionsx.listener

import net.prosavage.factionsx.event.FactionCreateEvent
import net.prosavage.factionsx.manager.GridManager
import net.prosavage.factionsx.persist.config.Config.factionCreationAutoClaimChunkWhereStanding
import net.prosavage.factionsx.persist.data.getFLocation
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener

object FactionListener : Listener {
    @EventHandler(priority = EventPriority.MONITOR)
    private fun FactionCreateEvent.onCreation() {
        // necessity
        val player = fPlayer?.getPlayer() ?: return
        val fLocation = getFLocation(player.location)
        val factionAt = fLocation.getFaction()

        // make sure conditions are met
        if (!factionCreationAutoClaimChunkWhereStanding || fPlayer.power() < 1 || !factionAt.isWilderness()) {
            return
        }

        // claim chunk
        GridManager.claim(this.faction, fLocation, fPlayer)
    }
}