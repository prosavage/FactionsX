package net.prosavage.factionsx.event

import net.prosavage.factionsx.core.Faction
import net.prosavage.factionsx.util.Relation
import org.bukkit.event.Event
import org.bukkit.event.HandlerList

class FactionRelationChangeEvent(
    val faction: Faction,
    val factionTo: Faction,
    val oldRelation: Relation,
    val newRelation: Relation
) : Event(false) {

    override fun getHandlers(): HandlerList = handlerList

    companion object {
        @JvmStatic
        val handlerList = HandlerList()
    }
}