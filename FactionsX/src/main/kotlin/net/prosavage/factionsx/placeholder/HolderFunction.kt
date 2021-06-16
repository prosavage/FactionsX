package net.prosavage.factionsx.placeholder

import net.prosavage.factionsx.core.FPlayer
import net.prosavage.factionsx.core.Faction

/**
 * This functional interface is used to process the base of every internal placeholder.
 */
fun interface HolderFunction {
    /**
     * Process the corresponding placeholder by player & faction.
     *
     * @param player [FPlayer] whom is involved, null if not involved.
     * @param faction [Faction] instance of the faction that this placeholder should process for.
     */
    fun process(player: FPlayer?, faction: Faction): String
}