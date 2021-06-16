package net.prosavage.factionsx.upgrade

import net.prosavage.factionsx.FactionsX
import net.prosavage.factionsx.core.FPlayer
import net.prosavage.factionsx.core.Faction
import net.prosavage.factionsx.persist.ChestData
import net.prosavage.factionsx.persist.data.FLocation
import net.prosavage.factionsx.persist.getChest
import net.prosavage.factionsx.util.SerializableItem
import org.bukkit.Bukkit

class ChestUpgrade(name: String, item: SerializableItem, maxLevelLore: List<String>, costLevel: Map<Int, LevelInfo>)
    : Upgrade(name, item, maxLevelLore, costLevel) {
    override val upgradeListener: UpgradeListener
        get() = ChestUpgradeListener(FactionsX.instance, this)


    override fun executeLevelUp(forFaction: Faction, fLocation: FLocation, upgrader: FPlayer): Boolean {
        val success = super.executeLevelUp(forFaction, fLocation, upgrader)
        if (!success) return false

        val upgradeParamForLevel = getUpgradeParamForLevel(getUpgradeLevelForScope(forFaction, fLocation))
                ?: return false
        forFaction.setChestSlots(upgradeParamForLevel.toInt())
        return true
    }

    class ChestUpgradeListener(override val factionsX: FactionsX, override val upgrade: Upgrade) : UpgradeListener
}

fun Faction.setChestSlots(rows: Int) {
    val chest = ChestData.chests[id]
    if (chest != null) {
        val clone = chest.viewers.toTypedArray().clone()
        for (viewer in clone) {
            viewer.closeInventory()
        }
    }

    createChestWithSize(rows)
    getChest().contents = chest?.contents?.copyOf(9 * rows) ?: arrayOf()
}

private fun Faction.createChestWithSize(rows: Int) {
    ChestData.chests[id] = Bukkit.createInventory(null, 9 * rows, tag)
}
