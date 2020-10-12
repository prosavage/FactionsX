package com.massivecraft.factions

class FPlayers {
    companion object {
        private lateinit var instance: FPlayers

        fun setInstance(instance: FPlayers) {
            this.instance = instance
        }

        @JvmStatic
        fun getInstance(): FPlayers {
            return instance
        }
    }
}