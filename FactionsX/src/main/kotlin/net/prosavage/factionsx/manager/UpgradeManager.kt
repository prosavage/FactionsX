package net.prosavage.factionsx.manager

import com.cryptomorin.xseries.XMaterial
import net.prosavage.factionsx.FactionsX
import net.prosavage.factionsx.persist.config.UpgradesConfig
import net.prosavage.factionsx.upgrade.*
import net.prosavage.factionsx.util.Relation
import org.bukkit.entity.EntityType

object UpgradeManager {

    private val upgrades: HashMap<Upgrade, UpgradeScope> = hashMapOf()

    fun getTotalUpgrades(): Int {
        return upgrades.size
    }

    fun registerUpgrade(scope: UpgradeScope, upgrade: Upgrade) {
        for (registeredUpgrade in upgrades.keys) {
            if (registeredUpgrade.name == upgrade.name) throw IllegalArgumentException("An upgrade with the name ${upgrade.name} is already registered.")
        }
        upgrades[upgrade] = scope
        upgrade.registerUpgradeListener(FactionsX.instance)
    }

    fun deRegisterUpgrade(factionsX: FactionsX, upgrade: Upgrade) {
        upgrades.remove(upgrade)
        upgrade.deRegisterUpgradeListener(factionsX)
    }

    fun deRegisterUpgradeByName(name: String) {
        val upgrade = getUpgradeByName(name)
        if (upgrade != null) {
            deRegisterUpgrade(FactionsX.instance, upgrade)
        }
    }

    fun getUpgradeByName(name: String): Upgrade? {
        return upgrades.filter { entry -> entry.key.name == name }.keys.firstOrNull()
    }

    fun getUpgradeScope(name: String): UpgradeScope? {
        return upgrades.filter { entry -> entry.key.name == name }.values.firstOrNull()
    }

    fun getUpgradeScope(upgrade: Upgrade): UpgradeScope? {
        return upgrades[upgrade]
    }

    fun getUpgrades(scope: UpgradeScope): Set<Upgrade> {
        return upgrades.filter { entry -> entry.value == scope }.keys
    }

    // TODO: This is dog shit, need to make a type adapter in framework --- migrate to YAML.
    fun registerUpgradesFromConfig() = UpgradesConfig.upgrades.forEach { (type, upgrades) ->
        when (type) {
            UpgradeType.DOUBLE_TALL -> upgrades.filterDisabled().forEach { configurableUpgrade ->
                registerUpgrade(configurableUpgrade.scope, DoubleTallUpgrade(
                        XMaterial.valueOf(configurableUpgrade.upgradeParam),
                        configurableUpgrade.name,
                        configurableUpgrade.upgradeItem,
                        configurableUpgrade.upgradeMaxLevelLore,
                        configurableUpgrade.costLevel
                )
                )
            }
            UpgradeType.MOB_DROP_MULTIPLIER -> upgrades.filterDisabled().forEach { configurableUpgrade ->
                registerUpgrade(configurableUpgrade.scope, MobDropMultiplierUpgrade(
                        EntityType.valueOf(configurableUpgrade.upgradeParam),
                        configurableUpgrade.name,
                        configurableUpgrade.upgradeItem,
                        configurableUpgrade.upgradeMaxLevelLore,
                        configurableUpgrade.costLevel
                )
                )
            }
            UpgradeType.SPECIAL -> upgrades.filterDisabled().forEach { specialUpgrade ->
                val specialUpgradeType = UpgradesConfig.SpecialUpgrade.valueOf(specialUpgrade.name.toUpperCase())
                val upgrade = when (specialUpgradeType) {
                    UpgradesConfig.SpecialUpgrade.WHEAT_INSTANT_GROWTH ->
                        WheatUpgrade(
                                specialUpgrade.name,
                                specialUpgrade.upgradeItem,
                                specialUpgrade.upgradeMaxLevelLore,
                                specialUpgrade.costLevel
                        )
                    UpgradesConfig.SpecialUpgrade.FACTION_POWER ->
                        PowerUpgrade(
                                specialUpgrade.name,
                                specialUpgrade.upgradeItem,
                                specialUpgrade.upgradeMaxLevelLore,
                                specialUpgrade.costLevel
                        )
                    UpgradesConfig.SpecialUpgrade.FACTION_CLAIMS ->
                        ClaimUpgrade(
                            specialUpgrade.name,
                            specialUpgrade.upgradeItem,
                            specialUpgrade.upgradeMaxLevelLore,
                            specialUpgrade.costLevel
                        )
                    UpgradesConfig.SpecialUpgrade.FACTION_MEMBERS ->
                        MemberUpgrade(
                                specialUpgrade.name,
                                specialUpgrade.upgradeItem,
                                specialUpgrade.upgradeMaxLevelLore,
                                specialUpgrade.costLevel
                        )
                    UpgradesConfig.SpecialUpgrade.FACTION_WARPS ->
                        WarpUpgrade(
                                specialUpgrade.name,
                                specialUpgrade.upgradeItem,
                                specialUpgrade.upgradeMaxLevelLore,
                                specialUpgrade.costLevel
                        )
                    UpgradesConfig.SpecialUpgrade.ALLY ->
                        RelationUpgrade(
                                Relation.ALLY,
                                specialUpgrade.name,
                                specialUpgrade.upgradeItem,
                                specialUpgrade.upgradeMaxLevelLore,
                                specialUpgrade.costLevel
                        )
                    UpgradesConfig.SpecialUpgrade.ENEMY ->
                        RelationUpgrade(
                                Relation.ENEMY,
                                specialUpgrade.name,
                                specialUpgrade.upgradeItem,
                                specialUpgrade.upgradeMaxLevelLore,
                                specialUpgrade.costLevel
                        )
                    UpgradesConfig.SpecialUpgrade.TRUCE ->
                        RelationUpgrade(
                                Relation.TRUCE,
                                specialUpgrade.name,
                                specialUpgrade.upgradeItem,
                                specialUpgrade.upgradeMaxLevelLore,
                                specialUpgrade.costLevel
                        )
                    UpgradesConfig.SpecialUpgrade.MOB_EXP ->
                        MobExpUpgrade(
                                specialUpgrade.name,
                                specialUpgrade.upgradeItem,
                                specialUpgrade.upgradeMaxLevelLore,
                                specialUpgrade.costLevel
                        )
                }
                registerUpgrade(specialUpgrade.scope, upgrade)
            }
        }
    }

}
