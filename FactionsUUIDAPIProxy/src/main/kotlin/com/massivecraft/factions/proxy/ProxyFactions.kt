package com.massivecraft.factions.proxy

import com.massivecraft.factions.Factions
import net.prosavage.factionsx.manager.FactionManager

class ProxyFactions : Factions {
    override fun getFactionById(id: String?): com.massivecraft.factions.Faction? {
        if (id == null) return null
        return com.massivecraft.factions.Faction(FactionManager.getFaction(id.toLong()))
    }

    override fun getByTag(str: String?): com.massivecraft.factions.Faction? {
        if (str == null) return null
        return FactionManager.getFaction(str)?.let { com.massivecraft.factions.Faction(it) }
    }

    override fun isTagTaken(str: String?): Boolean {
        if (str == null) return true
        return FactionManager.isTagTaken(str)
    }

    override fun isValidFactionId(id: String?): Boolean {
        if (id == null) return false
        return FactionManager.getFaction(id.toLong()) != FactionManager.getWilderness()
    }


    override  fun removeFaction(id: String?) {
        if (id == null) return
        FactionManager.deleteFaction(FactionManager.getFaction(id.toLong()))
    }

    override fun getFactionTags(): Set<String?>? {
        return FactionManager.getFactions().map { it.tag }.toSet()
    }


    override fun getWilderness(): com.massivecraft.factions.Faction? {
        return com.massivecraft.factions.Faction(FactionManager.getWilderness())
    }

    override fun getSafeZone(): com.massivecraft.factions.Faction? {
        return com.massivecraft.factions.Faction(FactionManager.getFaction(FactionManager.SAFEZONE_ID))
    }

    override fun getWarZone(): com.massivecraft.factions.Faction? {
        return com.massivecraft.factions.Faction(FactionManager.getFaction(FactionManager.WARZONE_ID))
    }
}