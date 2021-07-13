package net.prosavage.factionsx.persist

import com.cryptomorin.xseries.XMaterial
import net.prosavage.factionsx.addonframework.AddonPlugin
import org.bukkit.entity.EntityType
import java.io.File
import java.text.DecimalFormat
import java.util.*

object FTOPConfig {

    @Transient
    private val instance = this

    var pricePattern = "#,###.00"

    @Transient val priceFormat = DecimalFormat.getNumberInstance(Locale.US).also {
        (it as DecimalFormat).applyPattern(pricePattern)
    }

    var intervalPeriodMillis = 1000L
    var chunkLoadDelayInMilliseconds = 50L

    var broadcastCompletion = true
    var broadcastMessage = "&7FTOP calculation finished within approx. &6&n{time}s&7 (&6&n{factions}&6 factions&7)."
    var logFinishedFactionCalculation = false

    var ftopFactionEntryLore = listOf(
            "&7Leader: &6{leader}",
            "&6Spawners",
            "&7Creeper Spawners: &6{CREEPER}",
            "&7Zombie Spawners: &6{ZOMBIE}",
            "&7Skeleton Spawners: &6{SKELETON}",
            "&6Blocks",
            "&7Diamond Block: &6{DIAMOND_BLOCK}"
    )

    var calcPermission = "factionsx.calculate"
    var ftopPermission = "factionsx.top"
    var ftopAdminPermission = "factionsx.admin.top"
    var ftopAdminStartPermission = "factionsx.admin.top.start"
    var ftopAdminEndPermission = "factionsx.admin.top.end"

    var autoCalc = true
    var autoCalcIntervalSeconds = 60 * 60

    var useShopGUIPlusPricing = false
    var useShopGUIPlusSellPrice = false
    var fallbackToBlockValue = true
    var countFactionBankBalance = false

    var allowedBlocksToCalculate = setOf(XMaterial.SPAWNER, XMaterial.DIAMOND_BLOCK, XMaterial.EMERALD_BLOCK)

    var manualCalcCoolDownSeconds = 1800
    var calcCoolDownFormat = "{minutes}m, {seconds}s"

    var ftopPercentageLoss = "&7 (&c-%s%% Value Loss&7)"
    var ftopLineFormat = "{message-prefix}&6{rank}. &7{tag} - &6\${value}{percentage-loss}"

    var progressiveValueUpdateEverySeconds = 30L
    var progressiveSpawnersMilliseconds = mapOf<EntityType, Long>(EntityType.ZOMBIE to 300_000)
    var progressiveMaterialsMilliseconds = mapOf<XMaterial, Long>()

    var commandCalcHelp = "calculate your faction's value"
    var commandCalcNoClaims = "&cYour faction does not have any claims to calculate!"
    var commandCalcCoolDown = "&7You cannot calculate manually for another &6%s&7."
    var commandCalcSuccess = setOf(
            "&7Calculation of your Faction took approximately &6&n{time}s&7!",
            "&6Status:",
            "&f &f* &7Rank: &6{index}",
            "&f &f* &7Price: &6\${price}",
            "&f &f* &7Blocks: &6{blocks}",
            "&f &f* &7Spawners: &6{spawners}"
    )

    var commandTopNotCalculated = "&7Please calculate values first. Use &6/fx top start&7."
    var commandTopHelp = "view top factions"

    var commandAdminTopStartAlreadyCalculating = "&7FactionsTop is already calculating, please abort it to restart."
    var commandAdminStartStarted = "&7You have started calculation."
    var commandAdminStartHelp = "start factionstop calculation"
    var commandAdminTopEndAborting = "&7Aborting Calculations.."
    var commandAdminTopEndAborted = "&7FactionsTOP calculations aborted."
    var commandAdminTopEndHelp = "abort factionstop calculation"
    var commandAdminTopHelp = "manage factions top."

    var unknownValuePlaceholder = "N/A"

    fun save(addon: AddonPlugin) {
        addon.configSerializer.save(instance, File(addon.dataFolder, "config.json"))
    }

    fun load(addon: AddonPlugin) {
        addon.configSerializer.load(instance, FTOPConfig::class.java, File(addon.dataFolder, "config.json"))
    }
}