package net.prosavage.factionsx.core

import net.prosavage.factionsx.persist.TNTConfig

data class FactionTNTData(var tntBank: HashMap<Long, TNTBank>) {
    fun getTNTData(faction: Faction): TNTBank {
        if (tntBank.isNullOrEmpty()) tntBank = hashMapOf()
        return tntBank.getOrPut(faction.id) { TNTBank(0, TNTConfig.tntBankLimit) }
    }

    data class TNTBank(var tntAmt: Int, var limit: Int) {
        fun hasTnt(value: Int): Boolean {
            return tntAmt > value
        }

        /**
         * Adds to bank what it can, returns what it could not add.
         */
        fun addTnt(value: Int): Int {
            if (value < 0) return value
            if (tntAmt + value > limit) {
                val overflow = tntAmt + value - limit
                tntAmt = limit
                return overflow
            }
            tntAmt += value
            return 0
        }

        fun takeTnt(value: Int): Int {
            if (value < 0) return 0
            if (hasTnt(value)) {
                tntAmt -= value
                return value
            }
            val allTnt = tntAmt
            tntAmt = 0
            return allTnt
        }
    }
}