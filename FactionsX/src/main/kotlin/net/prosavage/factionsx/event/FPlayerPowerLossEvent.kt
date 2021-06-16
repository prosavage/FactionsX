package net.prosavage.factionsx.event

import net.prosavage.factionsx.core.FPlayer
import org.bukkit.event.Cancellable
import org.bukkit.event.Event
import org.bukkit.event.HandlerList

class FPlayerPowerLossEvent(val fPlayer: FPlayer, val loss: Double) : Event(false), Cancellable {
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