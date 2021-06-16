package net.prosavage.factionsx.persist

import com.cryptomorin.xseries.XMaterial
import net.prosavage.factionsx.addonframework.Addon
import java.io.File


object PrinterConfig {
    @Transient
    private val instance = this


    var printerCommandPermission = "factionsx.printer"
    var commandPrinterEnabled = "enabled"
    var commandPrinterDisabled = "disabled"
    var commandPrinterAlreadyEnabled = "&7You are &6already&7 in printer mode."
    var commandPrinterAlreadyDisabled = "&7Printer mode is already &6disabled&7."
    var commandPrinterCanOnlyPrintInOwn = "&7You can only print in your &6own&7 territory."
    var commandPrinterArmorOff = "&7Please take your armor off first."
    var commandPrinterToggle = "&7Printer is now &6%1\$s&7."
    var commandPrinterSummary = listOf(
            "",
            "&6&lPrinter Summary",
            "&8&l> &7Blocks Printed: {blocks-placed}",
            "&8&l> &7Money Spent: &6\${spent}",
            ""
    )
    var commandPrinterHelp = "&7Toggle Printer Mode."

    var printerEnabledPlaceholder = "on"
    var printerDisabledPlaceholder = "off"

    var printerInventory = "&7You cannot open any &6inventories&7 in printer mode, please disable first."
    var printerDrop = "&7You cannot drop any &6items&7 from your inventory in printer mode, please disable first."
    var printerBlockBreakDeny = "&7You cannot &6break&7 this block as you did not place it."
    var printerBowDeny = "&7You cannot shoot other players in printer."
    var printerBlockBreakChangeDeny = "&7You cannot &6break&7 this block as it seems to have changed."
    var printerBlockPlaceDisabled = "&7This block is &6disabled&7 from being used."
    var printerBlockOutsideLand = "&7You cannot place blocks outside of your claims."
    var printerBlockItemFrame = "&7You cannot use &6itemframes&7 in printer."
    var printerBlockArmorStand = "&7You cannot use &6armorstands&7 in printer."
    var printerBlockPvP = "&7You cannot &6hurt players&7 in printer mode."
    var printerBlockSpecialMetaInteract = "&7You may not use &6special items&7 in printer mode."
    var printerBlockItemPickup = "&7You cannot pickup items in printer."
    var printerBlockSpawnEgg = "&7You cannot use &6spawn eggs&7 in printer."
    var printerBlockCommand = "&7You cannot use this command in printer."
    var printerBlockExpBottle = "&7You cannot use &6experience&7 bottles in printer."

    var printerEnemyNearByBlocks = 12.0
    var printerDisabledEnemy = "&7Printer disabled due to enemy nearby."

    var canOnlyPrintInOwnTerritory = true
    var blockEnchantingBottles = true

    var commandStartsWithWhiteList = listOf(
            "/f printer",
            "/f show",
            "/f help",
            "/help",
            "/bal",
            "/balance",
            "/ebal",
            "/ebalance",
            "/f fly",
            "/factions fly",
            "/r",
            "/reply",
            "/tell",
            "/msg",
            "/ereply",
            "/etell",
            "/pm",
            "/epm"
    )

    var useShopGUIPlusHook = true
    var dontAllowPrintingIfNotInShopGUI = false
    var shopGUIFallBackPrice = 100.0

    var printerCommandsToRunOnEnable = listOf(
            "broadcast {player} is in printer.",
            "pex user {player} add anticheatbypass.permission"
    )

    var printerCommandsToRunOnDisable = listOf(
            "broadcast {player} exited printer.",
            "pex user {player} remove anticheatbypass.permission"
    )

    var printerDisabledTypes = listOf(
            XMaterial.SLIME_BLOCK,
            XMaterial.ANVIL,
            XMaterial.BEDROCK,
            XMaterial.ENDER_CHEST,
            XMaterial.BARRIER,
            XMaterial.END_PORTAL_FRAME,
            XMaterial.SPAWNER, XMaterial.BOW,
            XMaterial.POTION,
            XMaterial.SPLASH_POTION,
            XMaterial.ENDER_EYE

    )

    fun save(addon: Addon) {
        addon.configSerializer.save(instance, File(addon.addonDataFolder, "config.json"))
    }

    fun load(addon: Addon) {
        addon.configSerializer.load(instance, PrinterConfig::class.java, File(addon.addonDataFolder, "config.json"))
    }
}