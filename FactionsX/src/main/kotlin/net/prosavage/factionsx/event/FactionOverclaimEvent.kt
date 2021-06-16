package net.prosavage.factionsx.event

import net.prosavage.factionsx.core.Faction
import net.prosavage.factionsx.persist.data.FLocation
import org.bukkit.event.Cancellable
import org.bukkit.event.Event
import org.bukkit.event.HandlerList

class FactionOverclaimEvent(val faction: Faction, val overclaimedFaction: Faction, val fLocation: FLocation) : Event(false), Cancellable {
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