package net.prosavage.factionsx.event

import net.prosavage.factionsx.core.Faction
import org.bukkit.Location
import org.bukkit.event.Cancellable
import org.bukkit.event.Event
import org.bukkit.event.HandlerList

class FactionUpdateHomeEvent(val faction: Faction, val location: Location) : Event(false), Cancellable {
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