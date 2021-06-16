package net.prosavage.factionsx.event

import net.prosavage.factionsx.core.FPlayer
import net.prosavage.factionsx.core.Faction
import net.prosavage.factionsx.persist.data.FLocation
import org.bukkit.event.Cancellable
import org.bukkit.event.Event
import org.bukkit.event.HandlerList

class FactionUnClaimAllEvent(val unclaimingFaction: Faction, val unclaimedLocations: Set<FLocation>, val fplayer: FPlayer, val asAdmin: Boolean) : Event(false), Cancellable {
    private var isCancelled = false

    override fun getHandlers(): HandlerList = handlerList

    companion object {
        @JvmStatic
        val handlerList = HandlerList()
    }

    override fun setCancelled(cancel: Boolean) {
        this.isCancelled = cancel
    }

    override fun isCancelled(): Boolean {
        return this.isCancelled
    }
}