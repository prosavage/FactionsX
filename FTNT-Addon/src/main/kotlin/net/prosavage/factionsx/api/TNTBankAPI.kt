package net.prosavage.factionsx.api

import net.prosavage.factionsx.core.Faction
import net.prosavage.factionsx.core.FactionTNTData

/**
 * All functionality related to the bank storage.
 */
interface TNTBankAPI {
    /**
     * Get the bank of a faction.
     *
     * @param faction [Faction] the faction to fetch bank from.
     * @return [FactionTNTData.TNTBank]
     */
    fun of(faction: Faction): FactionTNTData.TNTBank

    /**
     * Get the bank of a faction by it's id. Keep in mind, this could be nullable.
     *
     * @param id [Long] the id to fetch bank from.
     * @return [FactionTNTData.TNTBank]
     */
    fun of(id: Long): FactionTNTData.TNTBank?

    /**
     * Get the amount from bank of a faction.
     *
     * @param faction [Faction] the faction to fetch bank amount from.
     * @return [Int]
     */
    fun amountOf(faction: Faction): Int

    /**
     * Get the amount from bank of a faction.
     *
     * @param id [Long] the id to fetch bank amount from.
     * @return [Int] -1 if id is not a valid faction, X otherwise.
     */
    fun amountOf(id: Long): Int

    /**
     * Get the amount limit from bank of a faction.
     *
     * @param faction [Faction] the faction to fetch bank amount limit from.
     * @return [Int]
     */
    fun limitOf(faction: Faction): Int

    /**
     * Get the amount limit from bank of a faction.
     *
     * @param id [Long] the id to fetch bank amount limit from.
     * @return [Int] -1 if id is not a valid faction, X otherwise.
     */
    fun limitOf(id: Long): Int

    /**
     * Get whether or not a faction has enough tnt stored in their bank
     * by passed down amount in second parameter.
     *
     * @param faction [Faction] the faction to perform the check on.
     * @param amount  [Int] the amount needed to pass the check.
     * @return [Int]
     */
    fun hasEnough(faction: Faction, amount: Int): Boolean

    /**
     * Get whether or not a faction has enough tnt stored in their bank
     * by passed down amount in second parameter.
     *
     * @param id     [Long] the id of a faction to perform the check on.
     * @param amount [Int] the amount needed to pass the check.
     * @return [Int]
     */
    fun hasEnough(id: Long, amount: Int): Boolean
}