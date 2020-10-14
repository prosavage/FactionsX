package com.massivecraft.factions

interface Board {
    companion object {
        private lateinit var instance: Board

        fun setInstance(instance: Board) {
            this.instance = instance
        }

        @JvmStatic
        fun getInstance(): Board {
            return instance;
        }
    }

    fun getFactionAt(fLocation: FLocation): Faction

    fun setFactionAt(faction: Faction, fLocation: FLocation)

    fun getAllClaims(factionId: String): Set<FLocation>

    fun getAllClaims(faction: Faction): Set<FLocation>
}