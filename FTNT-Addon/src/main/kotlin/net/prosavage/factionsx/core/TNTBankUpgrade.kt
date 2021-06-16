package net.prosavage.factionsx.core

import net.prosavage.factionsx.FactionsX
import net.prosavage.factionsx.getTNTBank
import net.prosavage.factionsx.persist.data.FLocation
import net.prosavage.factionsx.upgrade.LevelInfo
import net.prosavage.factionsx.upgrade.Upgrade
import net.prosavage.factionsx.upgrade.UpgradeListener
import net.prosavage.factionsx.util.SerializableItem
import kotlin.math.roundToInt

class TNTBankUpgrade(name: String, item: SerializableItem, maxLevelLore: List<String>, costLevel: Map<Int, LevelInfo>)
    : Upgrade(name, item, maxLevelLore, costLevel) {
    override val upgradeListener = TNTBankUpgradeListener(FactionsX.instance, this)

    override fun executeLevelUp(forFaction: Faction, fLocation: FLocation, upgrader: FPlayer): Boolean {
        val success = super.executeLevelUp(forFaction, fLocation, upgrader)
        if (!success) return false

        val upgradeParamForLevel = getUpgradeParamForLevel(getUpgradeLevelForScope(forFaction, fLocation))
                ?: return false
        forFaction.getTNTBank().limit = upgradeParamForLevel.roundToInt()
        return true
    }

    class TNTBankUpgradeListener(override val factionsX: FactionsX, override val upgrade: Upgrade) : UpgradeListener
}
