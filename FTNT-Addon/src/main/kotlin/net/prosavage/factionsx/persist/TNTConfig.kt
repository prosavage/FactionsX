package net.prosavage.factionsx.persist

import com.cryptomorin.xseries.XMaterial
import net.prosavage.factionsx.addonframework.AddonPlugin
import net.prosavage.factionsx.upgrade.ConfigurableUpgrade
import net.prosavage.factionsx.upgrade.LevelInfo
import net.prosavage.factionsx.upgrade.UpgradeScope
import net.prosavage.factionsx.util.SerializableItem
import java.io.File

object TNTConfig {

    @Transient
    private val instance = this

    var tntBankCommandPermission = "factionsx.tnt.bank"
    var tntFillCommandPermission = "factionsx.tnt.fill"

    var commandTntFillHelp = "use tnt fill."

    var commandTntCannotBeNegaitve = "&7The specified amount cannot be negative."

    var commandTntBankHelp = "use tnt bank."
    var commandTntbankAddNotEnoughTnt = "&7You only have &6%1\$s&7 tnt in your inventory."
    var commandTntbankAddSuccess = "&7You've added &6%1\$s&7 tnt, bank balance: &6%2\$s&7/&6%3\$s&7."
    var commandTntBankAddHelp = "add tnt to your bank."
    var commandTntBankRemoveHelp = "remove tnt from your bank."
    var commandTntBankBalHelp = "view amount of tnt in bank."
    var commandTntBankBalMessage = "&7Your tntbank has &6%1\$s&7."
    var commandTntBankRemoveNotEnough = "&7You do not have enough tnt in the bank to widthdraw. Balance: %1\$s"
    var commandTntBankRemoveTaken = "&7You have taken %1\$s tnt from the bank."

    var notifyDisbandConfirmation = true
    var notifyDisbandConfirmationMessage = "&c&l&nNOTE! &7Your faction has got &6%s &7tnt in the bank!"


    var blockTntFillInOtherFactionLand = true

    var tntBankLimit = 10000

    var tntFillMaxRadius = 20

    var commandTntFillRadiusTooHigh = "&7The max tntfill radius is &6%1\$s&7."
    var commandTntFillFailed = "&7Tnt was only partially filled since you ran out of tnt."
    var commandTntFillComplete = "&7Transaction completed, %1\$s tnt was taken from your inventory/bank."
    var commandTntFillDispensers = "&7Found &6%1\$s&7 dispensers in radius."
    var commandTntFillNotYourDispensers = "&7The dispensers in radius are not part of your faction's land."
    var commandTntFillNoneFound = "&7Could not find any dispensers in radius."

    var tntFillPermissionName = "use_tntfill"

    var tntFillPermissionInterfaceItem =
            SerializableItem(
                    XMaterial.MILK_BUCKET,
                    "&aUse TNT Fill",
                    listOf(
                            "&7Click to &atoggle&7 status.", "&7Currently: {status}"
                    ),
                    1
            )

    var tntBankPermissionName = "use_tntbank"

    var tntBankPermissionInterfaceItem =
            SerializableItem(
                    XMaterial.CHEST_MINECART,
                    "&aUse TNT Bank",
                    listOf(
                            "&7Click to &atoggle&7 status.", "&7Currently: {status}"
                    ),
                    1
            )

    var tntBankUpgrade: ConfigurableUpgrade = ConfigurableUpgrade(
            true,
            "TNTBANK",
            UpgradeScope.GLOBAL,
            mapOf(
                    1 to LevelInfo(25000.0, 10000.0),
                    2 to LevelInfo(35000.0, 20000.0),
                    3 to LevelInfo(45000.0, 20000.0),
                    4 to LevelInfo(50000.0, 20000.0)
            ),
            SerializableItem(
                    XMaterial.TNT,
                    "&cTNT Bank Upgrade",
                    listOf("&7Upgrade Faction TNT Bank Capacity.", "&7Level: &c&l{level}", "&7Upgrade Cost: &c&l\${cost}"),
                    1
            ),
            listOf("&7Maxed out tnt bank capacity.", "&c&lMAX LEVEL REACHED"),
            XMaterial.CHEST.name
    )


    fun save(addon: AddonPlugin) {
        addon.configSerializer.save(instance, File(addon.dataFolder, "config.json"))
    }

    fun load(addon: AddonPlugin) {
        addon.configSerializer.load(instance, TNTConfig::class.java, File(addon.dataFolder, "config.json"))
    }
}