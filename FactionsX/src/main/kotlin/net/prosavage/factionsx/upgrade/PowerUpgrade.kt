package net.prosavage.factionsx.upgrade

import net.prosavage.factionsx.FactionsX
import net.prosavage.factionsx.core.FPlayer
import net.prosavage.factionsx.core.Faction
import net.prosavage.factionsx.persist.data.FLocation
import net.prosavage.factionsx.util.SerializableItem

class PowerUpgrade(name: String, item: SerializableItem, maxLevelLore: List<String>, costLevel: Map<Int, LevelInfo>)
    : Upgrade(name, item, maxLevelLore, costLevel) {
    override val upgradeListener = PowerUpgradeListener(FactionsX.instance, this)

    override fun executeLevelUp(forFaction: Faction, fLocation: FLocation, upgrader: FPlayer): Boolean {
        val success = super.executeLevelUp(forFaction, fLocation, upgrader)
        if (!success) return false
        val upgradeParamForLevel = getUpgradeParamForLevel(getUpgradeLevelForScope(forFaction, fLocation))
                ?: return false
        forFaction.maxPowerBoost += upgradeParamForLevel
        return true
    }

    class PowerUpgradeListener(override val factionsX: FactionsX, override val upgrade: Upgrade) : UpgradeListener
}


