package net.prosavage.factionsx.event

import net.prosavage.factionsx.core.FPlayer
import net.prosavage.factionsx.core.Faction
import org.bukkit.event.Cancellable
import org.bukkit.event.Event
import org.bukkit.event.HandlerList

class FPlayerPreFlyCheckEvent(val fplayer: FPlayer, val faction: Faction, val factionAt: Faction) : Event(false), Cancellable {
    private var isCancelled = false

    override fun getHandlers(): HandlerList {
        return handlerList
    }

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