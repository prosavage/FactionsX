package net.prosavage.factionsx.upgrade

import net.prosavage.factionsx.FactionsX
import net.prosavage.factionsx.core.FPlayer
import net.prosavage.factionsx.core.Faction
import net.prosavage.factionsx.manager.UpgradeManager
import net.prosavage.factionsx.persist.Message
import net.prosavage.factionsx.persist.config.UpgradesConfig
import net.prosavage.factionsx.persist.data.FLocation
import net.prosavage.factionsx.util.SerializableItem
import net.prosavage.factionsx.util.checkPercentage
import org.bukkit.Bukkit
import org.bukkit.event.HandlerList
import org.bukkit.event.Listener


data class LevelInfo(val upgradeParam: Double, val price: Double)

abstract class Upgrade(val name: String, val item: SerializableItem, val maxLevelItemLore: List<String>, val upgradeLevelInfo: Map<Int, LevelInfo>) {

    abstract val upgradeListener: UpgradeListener

    open fun executeLevelUp(forFaction: Faction, fLocation: FLocation, upgrader: FPlayer): Boolean {
        val upgradeScope = UpgradeManager.getUpgradeScope(this)
        val nextLevel = when (upgradeScope) {
            UpgradeScope.GLOBAL -> forFaction.getUpgrade(this) + 1
            UpgradeScope.TERRITORY -> fLocation.getUpgrade(this) + 1
            null -> return false
        }

        val costForLevel = getCostForLevel(nextLevel) ?: return false

        val success = if (UpgradesConfig.useCreditsForUpgradePricing) upgrader.takeCredits(costForLevel) else upgrader.takeMoney(costForLevel)
        if (upgradeScope == UpgradeScope.TERRITORY) {
            if (!success) return false
            forFaction.message(Message.commandUpgradeTerritoryUpgraded, upgrader.name, name, fLocation.name, fLocation.x.toString(), fLocation.z.toString(), fLocation.world, excludeFPlayers = listOf(upgrader))
            fLocation.setUpgrade(this, nextLevel)
        } else {
            if (!success) return false
            forFaction.message(Message.commandUpgradeGlobalUpgraded, upgrader.name, name)
            forFaction.setUpgrade(this, forFaction.getUpgrade(this) + 1)
        }
        return true
    }

    fun getScope(): UpgradeScope? {
        return UpgradeManager.getUpgradeScope(this)
    }

    fun canLevelUp(level: Int): Boolean {
        return upgradeLevelInfo.containsKey(level)
    }

    fun getUpgradeLevelForScope(forFaction: Faction, fLocation: FLocation): Int {
        val scope = getScope()!!
        return if (scope == UpgradeScope.TERRITORY) {
            fLocation.getUpgrade(this)
        } else {
            forFaction.getUpgrade(this)
        }
    }

    fun getCostForLevel(level: Int): Double? {
        return upgradeLevelInfo[level]?.price
    }

    fun getUpgradeParamForLevel(level: Int): Double? {
        return upgradeLevelInfo[level]?.upgradeParam
    }

    fun registerUpgradeListener(factionsX: FactionsX) {
        Bukkit.getPluginManager().registerEvents(upgradeListener, factionsX)
    }

    fun deRegisterUpgradeListener(factionsX: FactionsX) {
        HandlerList.unregisterAll(upgradeListener)
    }

}

enum class UpgradeScope {
    GLOBAL, TERRITORY
}

enum class UpgradeType {
    DOUBLE_TALL, MOB_DROP_MULTIPLIER, SPECIAL
}

interface UpgradeListener : Listener {

    val factionsX: FactionsX

    val upgrade: Upgrade

    fun isUpgraded(fLocation: FLocation): Boolean {
        val scope = upgrade.getScope()
        if (scope == UpgradeScope.TERRITORY && !fLocation.isUpgraded(upgrade)) return false
        val factionAt = fLocation.getFaction()
        if (scope == UpgradeScope.GLOBAL && !factionAt.isUpgraded(upgrade)) return false
        return true
    }

    fun runUpgradeEffectWithChance(fLocation: FLocation): Boolean {
        if (!isUpgraded(fLocation)) return false
        val factionAt = fLocation.getFaction()
        val level = when (upgrade.getScope()) {
            UpgradeScope.GLOBAL -> factionAt.getUpgrade(upgrade)
            UpgradeScope.TERRITORY -> fLocation.getUpgrade(upgrade)
            else -> return false
        }
        val chance = upgrade.getUpgradeParamForLevel(level) ?: return false
        if (chance == 0.0) return false
        return (checkPercentage(chance))
    }
}

fun Collection<ConfigurableUpgrade>.filterDisabled(): Collection<ConfigurableUpgrade> = this.filter { upgrade -> upgrade.enabled }


data class ConfigurableUpgrade(val enabled: Boolean, val name: String, val scope: UpgradeScope, val costLevel: Map<Int, LevelInfo>, val upgradeItem: SerializableItem, val upgradeMaxLevelLore: List<String>, val upgradeParam: String)

