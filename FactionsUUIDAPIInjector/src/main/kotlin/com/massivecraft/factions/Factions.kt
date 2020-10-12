package com.massivecraft.factions

import net.prosavage.factionsx.core.Faction
import java.util.*


class Factions {

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

    fun getFactionById(id: String?): Faction? {

    }

    fun getByTag(str: String?): Faction? {}

    fun getBestTagMatch(start: String?): Faction? {}

    fun isTagTaken(str: String?): Boolean {}

    fun isValidFactionId(id: String?): Boolean {}

    fun createFaction(): Faction? {}

    fun removeFaction(id: String?) {}

    fun getFactionTags(): Set<String?>? {}

    fun getAllFactions(): ArrayList<Faction?>? {}

    @Deprecated("")
    fun getNone(): Faction? {
    }

    fun getWilderness(): Faction? {}

    fun getSafeZone(): Faction? {}

    fun getWarZone(): Faction? {}

    fun forceSave() {}

    fun forceSave(sync: Boolean) {}


}