package com.massivecraft.factions

import net.prosavage.factionsx.manager.FactionManager


interface Factions {

    companion object {
        private lateinit var instance: Factions

        fun setInstance(instance: Factions) {
            this.instance = instance
        }

        @JvmStatic
        fun getInstance(): Factions {
            return instance;
        }
    }

    fun getFactionById(id: String?): com.massivecraft.factions.Faction?

    fun getByTag(str: String?): com.massivecraft.factions.Faction?

    fun isTagTaken(str: String?): Boolean

    fun isValidFactionId(id: String?): Boolean

    fun removeFaction(id: String?)

    fun getFactionTags(): Set<String?>?

    fun getWilderness(): com.massivecraft.factions.Faction?

    fun getSafeZone(): com.massivecraft.factions.Faction?

    fun getWarZone(): com.massivecraft.factions.Faction?

}