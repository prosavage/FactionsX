package net.prosavage.factionsx.persist.config

import com.cryptomorin.xseries.XMaterial
import net.prosavage.baseplugin.serializer.Serializer
import net.prosavage.factionsx.FactionsX
import net.prosavage.factionsx.persist.IConfigFile
import net.prosavage.factionsx.upgrade.ConfigurableUpgrade
import net.prosavage.factionsx.upgrade.LevelInfo
import net.prosavage.factionsx.upgrade.UpgradeScope
import net.prosavage.factionsx.upgrade.UpgradeType
import net.prosavage.factionsx.util.SerializableItem
import net.prosavage.factionsx.util.nmsVersion
import org.bukkit.entity.EntityType
import java.io.File

object UpgradesConfig : IConfigFile {

    @Transient
    private val instance = this

    var useCreditsForUpgradePricing = false

    var upgrades: HashMap<UpgradeType, Set<ConfigurableUpgrade>> = hashMapOf<UpgradeType, Set<ConfigurableUpgrade>>(
            UpgradeType.DOUBLE_TALL to setOf<ConfigurableUpgrade>(
                    ConfigurableUpgrade(
                            true,
                            "CACTUS",
                            UpgradeScope.TERRITORY,
                            hashMapOf(
                                    1 to LevelInfo(.25, 10000.0),
                                    2 to LevelInfo(.40, 50000.0),
                                    3 to LevelInfo(.50, 100000.0),
                                    4 to LevelInfo(.80, 500000.0),
                                    5 to LevelInfo(1.0, 1000000.0)
                            ),
                            SerializableItem(
                                    XMaterial.CACTUS,
                                    "&2Cactus Growth",
                                    listOf("&7Chance of growing &2two&7 blocks.", "&7Level: &2&l{level}", "&7Upgrade Cost: &2&l\${cost}"),
                                    1
                            ),
                            listOf("&7Chance of growing &2two&7 blocks.", "&c&lMAX LEVEL REACHED"),
                            XMaterial.CACTUS.name
                    ),
                    ConfigurableUpgrade(
                            true,
                            "SUGARCANE",
                            UpgradeScope.TERRITORY,
                            hashMapOf(
                                    1 to LevelInfo(.25, 10000.0),
                                    2 to LevelInfo(.40, 50000.0),
                                    3 to LevelInfo(.50, 100000.0),
                                    4 to LevelInfo(.80, 500000.0),
                                    5 to LevelInfo(1.0, 1000000.0)
                            ),
                            SerializableItem(
                                    XMaterial.SUGAR_CANE,
                                    "&aCane Growth",
                                    listOf("&7Chance of growing &atwo&7 blocks.", "&7Level: &a&l{level}", "&7Upgrade Cost: &a&l\${cost}"),
                                    1
                            ),
                            listOf("&7Chance of growing &atwo&7 blocks.", "&c&lMAX LEVEL REACHED"),
                            XMaterial.SUGAR_CANE.name
                    )

            ),
            UpgradeType.MOB_DROP_MULTIPLIER to setOf(
                    ConfigurableUpgrade(
                            true,
                            "SKELETON",
                            UpgradeScope.TERRITORY,
                            hashMapOf(
                                    1 to LevelInfo(2.0, 10000.0),
                                    2 to LevelInfo(2.5, 50000.0),
                                    3 to LevelInfo(3.0, 100000.0),
                                    4 to LevelInfo(3.25, 500000.0),
                                    5 to LevelInfo(3.5, 1000000.0)
                            ),
                            SerializableItem(
                                    XMaterial.BONE,
                                    "&fSkeleton Upgrade",
                                    listOf("&7Chance of dropping &fextra&7 items.", "&7Level: &f&l{level}", "&7Upgrade Cost: &f&l\${cost}"),
                                    1
                            ),
                            listOf("&7Chance of dropping &fextra&7 items.", "&c&lMAX LEVEL REACHED"),
                            EntityType.SKELETON.name
                    ),
                    ConfigurableUpgrade(
                            true,
                            "BLAZE",
                            UpgradeScope.TERRITORY,
                            hashMapOf(
                                    1 to LevelInfo(2.0, 10000.0),
                                    2 to LevelInfo(2.5, 50000.0),
                                    3 to LevelInfo(3.0, 100000.0),
                                    4 to LevelInfo(3.25, 500000.0),
                                    5 to LevelInfo(3.5, 1000000.0)
                            ),
                            SerializableItem(
                                    XMaterial.BLAZE_POWDER,
                                    "&6Blaze Upgrade",
                                    listOf("&7Chance of dropping &6extra&7 items.", "&7Level: &6&l{level}", "&7Upgrade Cost: &6&l\${cost}"),
                                    1
                            ),
                            listOf("&7Chance of dropping &6extra&7 items.", "&c&lMAX LEVEL REACHED"),
                            EntityType.BLAZE.name
                    ),
                    ConfigurableUpgrade(
                            true,
                            "CREEPER",
                            UpgradeScope.TERRITORY,
                            hashMapOf(
                                    1 to LevelInfo(2.0, 10000.0),
                                    2 to LevelInfo(2.5, 50000.0),
                                    3 to LevelInfo(3.0, 100000.0),
                                    4 to LevelInfo(3.25, 500000.0),
                                    5 to LevelInfo(3.5, 1000000.0)
                            ),
                            SerializableItem(
                                    XMaterial.GUNPOWDER,
                                    "&aCreeper Upgrade",
                                    listOf("&7Chance of dropping &aextra&7 items.", "&7Level: &a&l{level}", "&7Upgrade Cost: &a&l\${cost}"),
                                    1
                            ),
                            listOf("&7Chance of dropping &aextra&7 items.", "&c&lMAX LEVEL REACHED"),
                            EntityType.CREEPER.name

                    ),
                    ConfigurableUpgrade(
                            true,
                            "ZOMBIE",
                            UpgradeScope.TERRITORY,
                            hashMapOf(
                                    1 to LevelInfo(2.0, 10000.0),
                                    2 to LevelInfo(2.5, 50000.0),
                                    3 to LevelInfo(3.0, 100000.0),
                                    4 to LevelInfo(3.25, 500000.0),
                                    5 to LevelInfo(3.5, 1000000.0)
                            ),
                            SerializableItem(
                                    XMaterial.ROTTEN_FLESH,
                                    "&2Zombie Upgrade",
                                    listOf("&7Chance of dropping &2extra&7 items.", "&7Level: &2&l{level}", "&7Upgrade Cost: &2&l\${cost}"),
                                    1
                            ),
                            listOf("&7Chance of dropping &fextra&7 items.", "&c&lMAX LEVEL REACHED"),
                            EntityType.ZOMBIE.name
                    ),
                    ConfigurableUpgrade(
                            true,
                            "IRON_GOLEM",
                            UpgradeScope.TERRITORY,
                            hashMapOf(
                                    1 to LevelInfo(2.0, 10000.0),
                                    2 to LevelInfo(2.5, 50000.0),
                                    3 to LevelInfo(3.0, 100000.0),
                                    4 to LevelInfo(3.25, 500000.0),
                                    5 to LevelInfo(3.5, 1000000.0)
                            ),
                            SerializableItem(
                                    XMaterial.IRON_INGOT,
                                    "&4Iron Golem Upgrade",
                                    listOf("&7Chance of dropping &4extra&7 items.", "&7Level: &4&l{level}", "&7Upgrade Cost: &4&l\${cost}"),
                                    1
                            ),
                            listOf("&7Chance of dropping &4extra&7 items.", "&c&lMAX LEVEL REACHED"),
                            EntityType.IRON_GOLEM.name
                    ),
                    ConfigurableUpgrade(
                            true,
                            "ZOMBIE_PIGMAN",
                            UpgradeScope.TERRITORY,
                            hashMapOf(
                                    1 to LevelInfo(2.0, 10000.0),
                                    2 to LevelInfo(2.5, 50000.0),
                                    3 to LevelInfo(3.0, 100000.0),
                                    4 to LevelInfo(3.25, 500000.0),
                                    5 to LevelInfo(3.5, 1000000.0)
                            ),
                            SerializableItem(
                                    XMaterial.GOLD_NUGGET,
                                    if (nmsVersion >= 16) "&eZombified Piglin Upgrade" else "&eZombie Pigman Upgrade",
                                    listOf("&7Chance of dropping &eextra&7 items.", "&7Level: &e&l{level}", "&7Upgrade Cost: &e&l\${cost}"),
                                    1
                            ),
                            listOf("&7Chance of dropping &eextra&7 items.", "&c&lMAX LEVEL REACHED"),
                            if (nmsVersion >= 16) EntityType.valueOf("ZOMBIFIED_PIGLIN").name else EntityType.valueOf("PIG_ZOMBIE").name
                    )
            ),
            UpgradeType.SPECIAL to setOf(
                    ConfigurableUpgrade(
                            true,
                            SpecialUpgrade.FACTION_POWER.name,
                            UpgradeScope.GLOBAL,
                            hashMapOf(
                                    1 to LevelInfo(5.0, 10000.0),
                                    2 to LevelInfo(10.0, 50000.0),
                                    3 to LevelInfo(12.5, 100000.0),
                                    4 to LevelInfo(15.0, 500000.0),
                                    5 to LevelInfo(20.0, 1000000.0)
                            ),
                            SerializableItem(
                                    XMaterial.GHAST_TEAR,
                                    "&bPower Upgrade",
                                    listOf("&7Increased faction member &bextra&7 power.", "&7Level: &b&l{level}", "&7Upgrade Cost: &b&l\${cost}"),
                                    1
                            ),
                            listOf("&7Increased faction member &bextra&7 power.", "&c&lMAX LEVEL REACHED"),
                            "INVALID_PARAMETER"
                    ),
                    ConfigurableUpgrade(
                            true,
                            SpecialUpgrade.WHEAT_INSTANT_GROWTH.name,
                            UpgradeScope.TERRITORY,
                            hashMapOf(
                                    1 to LevelInfo(.25, 10000.0),
                                    2 to LevelInfo(.40, 50000.0),
                                    3 to LevelInfo(.50, 100000.0),
                                    4 to LevelInfo(.80, 500000.0),
                                    5 to LevelInfo(1.0, 1000000.0)
                            ),
                            SerializableItem(
                                    XMaterial.WHEAT,
                                    "&eWheat Growth",
                                    listOf("&7Chance of growing &einstantly&7.", "&7Level: &e&l{level}", "&7Upgrade Cost: &e&l\${cost}"),
                                    1
                            ),
                            listOf("&7Chance of growing &einstantly&7.", "&c&lMAX LEVEL REACHED"),
                            "INVALID_PARAMETER"
                    ),
                    ConfigurableUpgrade(
                            true,
                            SpecialUpgrade.FACTION_MEMBERS.name,
                            UpgradeScope.GLOBAL,
                            hashMapOf(
                                    1 to LevelInfo(5.0, 10000.0),
                                    2 to LevelInfo(5.0, 50000.0),
                                    3 to LevelInfo(5.0, 100000.0),
                                    4 to LevelInfo(5.0, 500000.0),
                                    5 to LevelInfo(5.0, 1000000.0)
                            ),
                            SerializableItem(
                                    XMaterial.PLAYER_HEAD,
                                    "&aMember Upgrade",
                                    listOf("&7Increase &aMax&7 Faction Members.", "&7Level: &a&l{level}", "&7Upgrade Cost: &a&l\${cost}"),
                                    1
                            ),
                            listOf("&7Increase &aMax&7 Faction Members.", "&c&lMAX LEVEL REACHED"),
                            "INVALID_PARAMETER"
                    ),
                    ConfigurableUpgrade(
                            true,
                            SpecialUpgrade.FACTION_WARPS.name,
                            UpgradeScope.GLOBAL,
                            hashMapOf(
                                    1 to LevelInfo(1.0, 10000.0),
                                    2 to LevelInfo(1.0, 50000.0),
                                    3 to LevelInfo(1.0, 100000.0),
                                    4 to LevelInfo(2.0, 500000.0),
                                    5 to LevelInfo(5.0, 1000000.0)
                            ),
                            SerializableItem(
                                    XMaterial.ENDER_EYE,
                                    "&6Warp Upgrade",
                                    listOf("&7Increase &6Max&7 Faction Warps.", "&7Level: &6&l{level}", "&7Upgrade Cost: &6&l\${cost}"),
                                    1
                            ),
                            listOf("&6Increase &6Max&7 Faction Warps.", "&c&lMAX LEVEL REACHED"),
                            "INVALID_PARAMETER"
                    ),
                    ConfigurableUpgrade(
                            false,
                            SpecialUpgrade.FACTION_CLAIMS.name,
                            UpgradeScope.GLOBAL,
                            hashMapOf(
                                    1 to LevelInfo(1.0, 50000.0),
                                    2 to LevelInfo(1.0, 50000.0),
                                    3 to LevelInfo(1.0, 100000.0),
                                    4 to LevelInfo(2.0, 1000000.0),
                                    5 to LevelInfo(5.0, 5000000.0)
                            ),
                            SerializableItem(
                                    XMaterial.ENDER_EYE,
                                    "&6Claim Upgrade",
                                    listOf("&7Increase &6Max&7 Faction Claims.", "&7Level: &6&l{level}", "&7Upgrade Cost: &6&l\${cost}"),
                                    1
                            ),
                            listOf("&6Increase &6Max&7 Faction Claims.", "&c&lMAX LEVEL REACHED"),
                            "INVALID_PARAMETER"
                    ),
                    ConfigurableUpgrade(
                            true,
                            SpecialUpgrade.ENEMY.name,
                            UpgradeScope.GLOBAL,
                            hashMapOf(
                                    1 to LevelInfo(1.0, 10000.0),
                                    2 to LevelInfo(1.0, 50000.0),
                                    3 to LevelInfo(1.0, 100000.0),
                                    4 to LevelInfo(2.0, 500000.0),
                                    5 to LevelInfo(5.0, 1000000.0)
                            ),
                            SerializableItem(
                                    XMaterial.BLAZE_POWDER,
                                    "&cEnemy Upgrade",
                                    listOf("&7Increase &cMax&7 Enemies Possible.", "&7Level: &c&l{level}", "&7Upgrade Cost: &c&l\${cost}"),
                                    1
                            ),
                            listOf("&7Increase &cMax&7 Enemies Possible.", "&c&lMAX LEVEL REACHED"),
                            "INVALID_PARAMETER"
                    ),
                    ConfigurableUpgrade(
                            true,
                            SpecialUpgrade.ALLY.name,
                            UpgradeScope.GLOBAL,
                            hashMapOf(
                                    1 to LevelInfo(1.0, 10000.0),
                                    2 to LevelInfo(1.0, 50000.0),
                                    3 to LevelInfo(1.0, 100000.0),
                                    4 to LevelInfo(2.0, 500000.0),
                                    5 to LevelInfo(5.0, 1000000.0)
                            ),
                            SerializableItem(
                                    XMaterial.PINK_TULIP,
                                    "&dAlly Upgrade",
                                    listOf("&7Increase &dMax&7 Allies Possible.", "&7Level: &d&l{level}", "&7Upgrade Cost: &d&l\${cost}"),
                                    1
                            ),
                            listOf("&7Increase &dMax&7 Allies Possible.", "&c&lMAX LEVEL REACHED"),
                            "INVALID_PARAMETER"
                    ),
                    ConfigurableUpgrade(
                            true,
                            SpecialUpgrade.TRUCE.name,
                            UpgradeScope.GLOBAL,
                            hashMapOf(
                                    1 to LevelInfo(1.0, 10000.0),
                                    2 to LevelInfo(1.0, 50000.0),
                                    3 to LevelInfo(1.0, 100000.0),
                                    4 to LevelInfo(2.0, 500000.0),
                                    5 to LevelInfo(5.0, 1000000.0)
                            ),
                            SerializableItem(
                                    XMaterial.ALLIUM,
                                    "&5Truce Upgrade",
                                    listOf("&7Increase &5Max&7 Truces Possible.", "&7Level: &5&l{level}", "&7Upgrade Cost: &c&l\${cost}"),
                                    1
                            ),
                            listOf("&7Increase &5Max&7 Truces Possible.", "&c&lMAX LEVEL REACHED"),
                            "INVALID_PARAMETER"
                    ),
                    ConfigurableUpgrade(
                            true,
                            SpecialUpgrade.MOB_EXP.name,
                            UpgradeScope.TERRITORY,
                            hashMapOf(
                                    1 to LevelInfo(1.2, 10000.0),
                                    2 to LevelInfo(1.4, 50000.0),
                                    3 to LevelInfo(1.6, 100000.0),
                                    4 to LevelInfo(2.0, 500000.0),
                                    5 to LevelInfo(2.5, 1000000.0)
                            ),
                            SerializableItem(
                                    XMaterial.EXPERIENCE_BOTTLE,
                                    "&aMob Experience Upgrade",
                                    listOf("&7Increase &aMob Experience&7.", "&7Level: &a&l{level}", "&7Upgrade Cost: &a&l\${cost}"),
                                    1
                            ),
                            listOf("&7Increase &aMob Experience&7.", "&c&lMAX LEVEL REACHED"),
                            "INVALID_PARAMETER"
                    )
            )
    )

    enum class SpecialUpgrade { ALLY, ENEMY, TRUCE, FACTION_WARPS, FACTION_POWER, FACTION_MEMBERS, FACTION_CLAIMS, WHEAT_INSTANT_GROWTH, MOB_EXP }


    override fun save(factionsx: FactionsX) {
        Serializer(false, factionsx.dataFolder, factionsx.logger)
                .save(UpgradesConfig.instance, File("${factionsx.dataFolder}/config", "upgrade-config.json"))
    }

    override fun load(factionsx: FactionsX) {
        Serializer(false, factionsx.dataFolder, factionsx.logger)
                .load(UpgradesConfig.instance, UpgradesConfig::class.java, File("${factionsx.dataFolder}/config", "upgrade-config.json"))

    }

}