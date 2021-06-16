package net.prosavage.factionsx.upgrade

import net.prosavage.factionsx.FactionsX
import net.prosavage.factionsx.core.FPlayer
import net.prosavage.factionsx.core.Faction
import net.prosavage.factionsx.persist.data.FLocation
import net.prosavage.factionsx.util.Relation
import net.prosavage.factionsx.util.SerializableItem
import kotlin.math.ceil

class RelationUpgrade(val relation: Relation, name: String, item: SerializableItem, maxLevelLore: List<String>, costLevel: Map<Int, LevelInfo>)
    : Upgrade(name, item, maxLevelLore, costLevel) {
    override val upgradeListener = MemberUpgradeListener(FactionsX.instance, this)

    override fun executeLevelUp(forFaction: Faction, fLocation: FLocation, upgrader: FPlayer): Boolean {
        val success = super.executeLevelUp(forFaction, fLocation, upgrader)
        if (!success) return false
        val upgradeParamForLevel = getUpgradeParamForLevel(getUpgradeLevelForScope(forFaction, fLocation))
                ?: return false

        val boost = ceil(upgradeParamForLevel).toInt()

        when (relation) {
            Relation.ALLY -> forFaction.allyBoost += boost
            Relation.ENEMY -> forFaction.enemyBoost += boost
            Relation.TRUCE -> forFaction.truceBoost += boost
            Relation.NEUTRAL -> Unit
        }
        return true
    }

    class MemberUpgradeListener(override val factionsX: FactionsX, override val upgrade: Upgrade) : UpgradeListener

}
