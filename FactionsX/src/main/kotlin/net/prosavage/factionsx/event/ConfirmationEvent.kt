package net.prosavage.factionsx.event

import net.prosavage.factionsx.command.engine.ConfirmAction
import net.prosavage.factionsx.core.FPlayer
import net.prosavage.factionsx.core.Faction
import org.bukkit.event.Event
import org.bukkit.event.HandlerList

class ConfirmationEvent(val faction: Faction, val caller: FPlayer, val action: ConfirmAction) : Event(false) {
    override fun getHandlers(): HandlerList = handlerList

    companion object {
        @JvmStatic
        val handlerList = HandlerList()
    }
}