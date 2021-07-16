package net.prosavage.factionsx.api

import net.prosavage.factionsx.core.Faction
import net.prosavage.factionsx.core.FactionTNTData
import net.prosavage.factionsx.persist.TNTAddonData

/**
 * Simple implementation of [TNTBankAPI].
 */
internal object SimpleAPIService : TNTBankAPI {
    override fun of(faction: Faction): FactionTNTData.TNTBank =
        TNTAddonData.tntData.getTNTData(faction)

    override fun of(id: Long): FactionTNTData.TNTBank? =
        TNTAddonData.tntData.tntBank[id]

    override fun amountOf(faction: Faction): Int =
        of(faction).tntAmt

    override fun amountOf(id: Long): Int =
        of(id)?.tntAmt ?: -1

    override fun limitOf(faction: Faction): Int =
        of(faction).limit

    override fun limitOf(id: Long): Int =
        of(id)?.limit ?: -1

    override fun hasEnough(faction: Faction, amount: Int): Boolean =
        of(faction).tntAmt >= amount

    override fun hasEnough(id: Long, amount: Int): Boolean =
        (of(id)?.tntAmt ?: 0) >= amount
}