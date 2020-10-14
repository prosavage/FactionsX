package com.massivecraft.factions.event

import com.massivecraft.factions.proxy.ProxyFPlayer
import net.prosavage.factionsx.core.FPlayer
import org.bukkit.event.Cancellable
import org.bukkit.event.Event
import org.bukkit.event.HandlerList

class PowerLossEvent(val fPlayer: FPlayer, val loss: Double) : Event(false), Cancellable {
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

    fun getfPlayer(): com.massivecraft.factions.FPlayer {
        return ProxyFPlayer(fPlayer)
    }
}