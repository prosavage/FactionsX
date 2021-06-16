package net.prosavage.factionsx.event

import net.prosavage.factionsx.core.FPlayer
import net.prosavage.factionsx.core.Faction
import org.bukkit.event.Event
import org.bukkit.event.HandlerList

class FPlayerTerritoryChangeEvent(val fPlayer: FPlayer, val fromFaction: Faction, val toFaction: Faction)
    : Event(false) {

    override fun getHandlers(): HandlerList {
        return handlerList
    }

    companion object {
        @JvmStatic
        val handlerList = HandlerList()
    }
}