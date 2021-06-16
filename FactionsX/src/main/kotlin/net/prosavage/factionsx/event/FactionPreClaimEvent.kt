package net.prosavage.factionsx.event

import net.prosavage.factionsx.core.FPlayer
import net.prosavage.factionsx.core.Faction
import net.prosavage.factionsx.persist.data.FLocation
import org.bukkit.event.Cancellable
import org.bukkit.event.Event
import org.bukkit.event.HandlerList

class FactionPreClaimEvent(val factionClaiming: Faction, val claimedFaction: Faction, val fLocation: FLocation, val fplayer: FPlayer, val claimingAsServerAdmin: Boolean) : Event(false), Cancellable {
    private var isCancelled = false

    override fun getHandlers(): HandlerList = handlerList

    override fun setCancelled(cancel: Boolean) {
        this.isCancelled = cancel
    }

    override fun isCancelled(): Boolean {
        return this.isCancelled
    }

    companion object {
        @JvmStatic
        val handlerList = HandlerList()
    }
}