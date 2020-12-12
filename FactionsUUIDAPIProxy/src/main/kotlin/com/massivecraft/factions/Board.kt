package com.massivecraft.factions

abstract class Board {
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

    abstract fun getFactionAt(fLocation: FLocation): Faction

    abstract fun setFactionAt(faction: Faction, fLocation: FLocation)

    abstract fun getAllClaims(factionId: String): Set<FLocation>

    abstract fun getAllClaims(faction: Faction): Set<FLocation>
}