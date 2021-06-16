package net.prosavage.factionsx.event

import net.prosavage.factionsx.core.FPlayer
import net.prosavage.factionsx.core.Faction
import org.bukkit.event.Event
import org.bukkit.event.HandlerList

class FactionDisbandEvent(val faction: Faction, val fPlayer: FPlayer?, val isAdmin: Boolean) : Event(false) {
    override fun getHandlers(): HandlerList = handlerList

    companion object {
        @JvmStatic
        val handlerList = HandlerList()
    }
}
