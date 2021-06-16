package net.prosavage.factionsx.event

import net.prosavage.factionsx.core.FPlayer
import net.prosavage.factionsx.core.Faction
import org.bukkit.event.Event
import org.bukkit.event.HandlerList

class FactionRenameEvent(
        val faction: Faction,
        val fPlayer: FPlayer?,
        val admin: Boolean,
        val oldTag: String,
        val newTag: String
) : Event(false) {
    override fun getHandlers(): HandlerList = handlerList

    companion object {
        @JvmStatic
        val handlerList = HandlerList()
    }
}