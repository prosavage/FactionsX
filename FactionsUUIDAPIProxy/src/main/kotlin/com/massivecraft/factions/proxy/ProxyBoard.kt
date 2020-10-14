package com.massivecraft.factions.proxy

import com.massivecraft.factions.Board
import com.massivecraft.factions.FLocation
import com.massivecraft.factions.Faction
import com.massivecraft.factions.Factions
import net.prosavage.factionsx.manager.GridManager

class ProxyBoard : Board {
    override fun getFactionAt(fLocation: FLocation): Faction {
        return Faction(GridManager.getFactionAt(fLocation.fLocation))
    }

    override fun setFactionAt(faction: Faction, fLocation: FLocation) {
        GridManager.claim(faction.faction, fLocation.fLocation)
    }

    override fun getAllClaims(factionId: String): Set<FLocation> {
        return Factions.getInstance().getFactionById(factionId)?.faction?.let { GridManager.getAllClaims(it) }?.map { FLocation(it) }?.toSet()
                ?: emptySet()
    }

    override fun getAllClaims(faction: Faction): Set<FLocation> {
        return faction.faction.let { GridManager.getAllClaims(it) }.map { FLocation(it) }.toSet()
    }
}
