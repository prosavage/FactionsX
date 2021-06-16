package net.prosavage.factionsx.event

import net.prosavage.factionsx.core.FPlayer
import org.bukkit.event.Cancellable
import org.bukkit.event.Event
import org.bukkit.event.HandlerList

class FactionPreCreateEvent(val factionTag: String, val fPlayer: FPlayer?) : Event(false), Cancellable {
    private var cancelled: Boolean = false

    companion object {
        @JvmStatic
        val handlerList = HandlerList()
    }

    override fun getHandlers(): HandlerList = handlerList
    override fun isCancelled(): Boolean = this.cancelled
    override fun setCancelled(shouldCancel: Boolean) {
        this.cancelled = shouldCancel; }
}