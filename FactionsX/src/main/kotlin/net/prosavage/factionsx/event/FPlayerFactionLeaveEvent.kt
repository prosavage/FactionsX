package net.prosavage.factionsx.event

import net.prosavage.factionsx.core.FPlayer
import net.prosavage.factionsx.core.Faction
import org.bukkit.event.Event
import org.bukkit.event.HandlerList

class FPlayerFactionLeaveEvent(val fPlayer: FPlayer, val faction: Faction, val isAdmin: Boolean) : Event(false) {
    companion object {
        @JvmStatic
        val handlerList = HandlerList()
    }

    override fun getHandlers(): HandlerList = handlerList
}