package net.prosavage.factionsx.persist.config

import com.cryptomorin.xseries.XMaterial
import net.prosavage.baseplugin.serializer.Serializer
import net.prosavage.factionsx.FactionsX
import net.prosavage.factionsx.persist.IConfigFile
import net.prosavage.factionsx.persist.settings.CreditSettings
import net.prosavage.factionsx.persist.settings.PowerSettings
import net.prosavage.factionsx.util.*
import org.bukkit.Bukkit
import org.bukkit.Color
import java.io.File
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import javax.swing.plaf.TreeUI

object Config : IConfigFile {

    @Transient
    private val instance = this

    @Transient
    var numberFormat = DecimalFormat.getNumberInstance(Locale.US)

    @Transient
    var dateCreationFormat = SimpleDateFormat()

    var factionsPermissionPrefix = "factionsx"

    var autoSave = true
    var autoSaveBroadcast = true
    var autoSaveBroadcastMessage = "&7[&6✕&7] &7Saving factions data..."
    var autoSaveBroadcastComplete = true
    var autoSaveBroadcastCompleteMessage = "&7[&6✕&7] &7Saved factions data successfully."
    var autoSaveIntervalInMilliseconds = TimeUnit.HOURS.toMillis(6)

    var wildernessDescription = "&7it's dangerous out here!"
    var safezoneDescription = "&7pvp is disabled."
    var warzoneDescription = "&7pvp is enabled, be &4careful&7!"
    var defaultFactionDescription = "&7Default faction description :(."

    var allowMobsNaturalSpawnInWarZone = false
    var allowMobsNaturalSpawnInSafeZone = false
    var allowMobsNaturalSpawnInWilderness = true
    var allowMobsNaturalSpawnInFactionTerritories = true

    var warZoneNoPowerLoss = false
    var safeZoneNoPowerLoss = false
    var wildernessNoPowerLoss = false

    var denyHomeTeleportInEnemyLand = true
    var denyWarpTeleportInEnemyLand = true

    var disableOverclaimMechanism = false

    var teleportToFactionHomeOnRespawn = false

    var timeFormatted = "{hours}h {minutes}m {seconds}s"

    data class InactiveMigration(val enabled: Boolean, val unit: TimeUnit, val time: Long) {
        fun toMillis(): Long = unit.toMillis(time)
    }
    var deleteInactivePlayers = InactiveMigration(false, TimeUnit.DAYS, 30)

    var helpGeneratorPageEntries = 10

    var relationOwnColor = "&a"
    var relationNeutralColor = "&f"
    var relationEnemyColor = "&c"
    var relationAllyColor = "&d"
    var relationTruceColor = "&5"
    var relationWarzoneColor = "&4"
    var relationSafezoneColor = "&e"
    var relationWildernessColor = "&8"

    var relationNeutralReplacement = "Neutral"
    var relationEnemyReplacement = "Enemy"
    var relationAllyReplacement = "Ally"
    var relationTruceReplacement = "Truce"

    var defaultRelation = Relation.NEUTRAL
    var disabledRelations = listOf<Relation>()
    var forceBothFactionsToRequestNeutral = false

    var disableSetHomeInRelationTerritory = listOf(Relation.ENEMY)

    var sendPrefixForWhoCommand = true
    var whoFormatWilderness = listOf(
            "&7Tag &l&7» &6{tag}"
    )

    var blacklistedCommands = listOf<String>()

    var whoFormat = listOf(
            "&7Tag &l&7» &6{tag}",
            "&7Description &l&7» &6{description}",
            "&7Leader &l&7» &6{leader}",
            "&7Online &l&7» &6{online_members}",
            "&7Offline &l&7» &6{offline_members}",
            "&7Claims &l&7» &6{claims}&7/&6{max-claims}",
            "&7Power &l&7» &6{faction-power}&7/&6{faction-max-power}",
            "&7Allies &l&7» &6{allies}",
            "&7Enemies &l&7» &6{enemies}",
            "&7truce &l&7» &6{truces}",
            "&7Shield Duration &l&7» &6{shield-duration}"
    )

    var whoMemberFormat = "&7{chat-tag}&r{prefix}&r &6{member-name}"

    var fNearRadiusInBlocks = 25.0


    var positionMonitorChunkChanged = "&7Entering &6{tag}&7 - &6{description}."
    var positionMonitorChunkChangedMessage = false
    var positionMonitorChunkChangeTitle = true
    var positionMonitorChunkChangedBossBar = false

    data class PositionMonitorTitle(val title: String, val subtitle: String)
    var positionMonitorChunkChangedTitle = PositionMonitorTitle("{tag}", "{description}")
    var positionMonitorChunkChangedTitleFadeInSeconds = .5
    var positionMonitorChunkChagedTitleStayInSeconds = 1.0
    var positionMonitorChunkChangedTitleFadeOutSeconds = .5

    var positionMonitorChunkChangedBossBarMessage = "&6{tag} &7- &6{description}"

    var warpTeleportWarmupSeconds = 5
    var homeTeleportWarmupSeconds = 3

    var warpLimit = 5
    var allyLimit = 3
    var truceLimit = 3
    var enemyLimit = 5

    var listCommandPageRows = 9

    var factionTagEnforceLength = true
    var factionTagsMinLength = 4
    var factionTagsMaxLength = 12
    var factionTagEnforceAlphaNumeric = true
    var triedToHurtYouFactionNotify = false

    var factionCreationDateFormat = "MMMM dd, yyyy, HH:mm"
    var factionCreationCommandsToExecute = listOf<String>()
    var factionDisbandCommandsToExecutePerPlayer = listOf<String>()
    var factionJoinCommandsToExecute = listOf<String>()
    var factionLeaveCommandsToExecute = listOf<String>()

    var factionStrikeAlerts = true
    var factionStrikeMax = 3
    var factionStrikeActions = mapOf(
            3 to listOf(
                    "[faction:message] &7Your faction has received &6&l3 &7strikes!"
            )
    )

    var territoryDeniedCommands = mapOf(
            Relation.ENEMY to listOf(
                    "/sethome",
                    "/esethome"
            )
    )


    var chatChannelTruce = "truce"
    var chatChannelTruceShortHand = 'T'
    var chatChannelTruceFormat = "&5Truce {tag} &8&l> &r{prefix} {chat-tag}{member-name}: {message}"
    var chatChannelAlly = "ally"
    var chatChannelAllyShortHand = 'A'
    var chatChannelAllyFormat = "&dAlly {tag} &8&l> &r{prefix} {chat-tag}{member-name}: {message}"
    var chatChannelFaction = "faction"
    var chatChannelFactionShortHand = 'F'
    var chatChannelFactionFormat = "&6Faction &8&l> &r{prefix} {chat-tag}{member-name}: {message}"
    var chatChannelPublic = "public"
    var chatChannelPublicShortHand = 'P'

    data class ColorData(val r: Int, val g: Int, val b: Int) {
        fun toColor(): Color {
            return Color.fromRGB(r, g, b)
        }
    }

    var seeChunkWildernessColor = ColorData(1, 153, 51)
    var seeChunkOwnFactionColor = ColorData(1, 255, 0)
    var seeChunkWarzoneColor = ColorData(255, 0, 0)
    var seeChunkSafezoneColor = ColorData(Color.YELLOW.red, Color.YELLOW.green, Color.YELLOW.blue)
    var seeChunkTruceColor = ColorData(Color.PURPLE.red, Color.PURPLE.green, Color.PURPLE.blue)
    var seeChunkEnemyColor = ColorData(Color.RED.red, Color.RED.green, Color.RED.blue)
    var seeChunkNeutralColor = ColorData(Color.WHITE.red, Color.WHITE.green, Color.WHITE.blue)
    var seeChunkAllyColor = ColorData(Color.FUCHSIA.red, Color.FUCHSIA.green, Color.FUCHSIA.blue)


    var teleportFromEnemyClaimOnJoin = false
    var teleportFromEnemyClaimLocation = Bukkit.getWorlds()[0]?.spawnLocation
    var teleportFromClaimOnLeave = true
    var teleportFromClaimOnLeaveLocation = Bukkit.getWorlds()[0]?.spawnLocation

    var nearbyEnemyDisallowedCommandsDistance = 15.0
    var nearbyEnemyDisallowedCommands = listOf("spawn")

    var creditSettings = CreditSettings(true)

    var powerSettings = PowerSettings(powerRegenOffline = false, dtrBased = false, dtrBasedActions = arrayListOf(PlayerAction.BREAK_BLOCK, PlayerAction.PLACE_BLOCK))

    var maxClaimLimit = -1
    var maxClaimRadius = 20
    var claimWorldBorderBuffer = 0
    var setHomeOnClaimIfNotSetYet = false

    var disbandRequireConfirmation = true

    var minFactionMembers = false
    var minFactionMembersToClaim = 2
    var turnBlacklistWorldsToWhitelist = false
    var allowClaimInWorldGuardRegion = false
    var worldGuardRegionAllowedClaimInWorlds = mapOf("example_world" to "myRegion")
    var worldsNoClaiming = listOf("example-no-claiming-world-1", "example-no-claiming-world-2")
    var worldsNoPvpHandling = listOf("example-world-to-ignore-factions-pvp")
    var worldsNoFlyHandling = listOf("example-world-to-not-handle-fly")
    var worldsNoPowerLoss = listOf("example-world-with-no-power-loss")
    var worldsNoInteractionHandling = listOf("example-world-with-no-interact-block-checking-etc")
    var worldsNoChunkMessageHandling = listOf("example-world-with-no-chunk-message-handling")
    var barLength = 49

    data class ConfigurableShield(val durationInSeconds: Long)

    var factionShieldEnabled = true

    var factionShield = ConfigurableShield(28800)
    var factionShieldInfoItem = InterfaceItem(
            false,
            Coordinate(0, 13),
            SerializableItem(
                    XMaterial.WRITABLE_BOOK,
                    "&7Faction Shield Schedule",
                    listOf(
                            "&7Set your shield schedule",
                            "&7Once selected, it cannot be changed",
                            "&7Current Server Time:",
                            "&7{server-time}"
                    ),
                    1
            )
    )
    var factionShieldTimes = Coordinate(1, 0)
    var shieldsGUIRows = 5
    var shieldsGUIName = "&6Faction Shields"
    var shieldsGUIBackgroundItem = SerializableItem(
            XMaterial.BLACK_STAINED_GLASS_PANE,
            "&8",
            emptyList(),
            1
    )

    var shieldTimeLeftInvalid = "N/A"
    var shieldActivePlaceholderTrue = "Yes"
    var shieldActivePlaceholderFalse = "No"

    var shieldTimerItem = SerializableItem(
            XMaterial.CLOCK,
            "&7{time}",
            listOf(
                    "&7Shield timings everyday:",
                    "-> Starts: {time}",
                    "-> Ends: {end}"
            ),
            1
    )

    var shieldMode = "PAGINATION"

    var shieldModeTwoTimerItem = InterfaceItem(
            false, Coordinate(2, 4), shieldTimerItem
    )

    var shieldModeTwoPreviousItem = InterfaceItem(
            false, Coordinate(2, 3), SerializableItem(XMaterial.STONE_BUTTON, "&6< Previous", listOf(), 1)
    )

    var shieldModeTwoNextItem = InterfaceItem(
            false, Coordinate(2, 5), SerializableItem(XMaterial.STONE_BUTTON, "&6Next >", listOf(), 1)
    )


    data class ScoreboardOptions(val enabled: Boolean, val internal: Boolean, val perTerritory: Boolean)
    var scoreboardOptions = ScoreboardOptions(enabled = false, internal = false, perTerritory = false)

    var disableCustomRoleCommands = false

    var chatHandledByAnotherPlugin = false
    var chatFactionPlaceholder = "[FACTION]"
    var chatFactionReplaceString = "[RELATIONAL_COLOR][TAG] [FACTION] "
    var chatNoFactionReplaceString = ""


    var defaultWildernessTag = "&a&lWilderness"
    var defaultWarzoneTag = "&4&lWarzone"
    var defaultSafezoneTag = "&e&lSafezone"

    var factionMemberLimit = 20
    var factionAltLimit = 10

    var altsInvitesFormat = "&6%s"
    var altsInvitesSeparator = "&7, "

    var altsListFormat = "&6%s"
    var altsListSeparator = "&7, "



    var memberActionKick = "kick"
    var memberActionDisband = "disband"
    var memberActionDeInvite = "deinvite"
    var memberActionDemote = "demote"
    var memberActionInvite = "invite"
    var memberActionPrefix = "prefix"
    var memberActionRename = "rename"
    var memberActionUnclaimall = "unclaimall"
    var memberActionFly = "fly"
    var memberActionHome = "home"
    var memberActionSetHome = "set_home"
    var memberActionChangeDescription = "change_description"
    var memberActionClaim = "claim"
    var memberActionUnclaim = "unclaim"
    var memberActionWarp = "warp"
    var memberActionSetWarp = "set_warp"
    var memberActionDelWarp = "del_warp"
    var memberActionViewWarpPassword = "view_warp_password"
    var memberActionRelation = "relation"
    var memberActionPromote = "promote"
    var memberActionOpen = "open"
    var memberActionSetPaypal = "setpaypal"
    var memberActionSetDiscord = "setdiscord"
    var memberActionBankWithdraw = "bank_withdraw"
    var memberActionBankDeposit = "bank_deposit"
    var memberActionBankPay = "bank_pay"
    var memberActionBankLogs = "bank_logs"
    var memberActionAltsInvite = "alts_invite"
    var memberActionAltsKick = "alts_kick"
    var memberActionAltsRevoke = "alts_revoke"
    var memberActionAltsOpen = "alts_open"
    var memberActionAltsClose = "alts_close"
    var memberActionAltsInvites = "alts_invites"
    var memberActionAltsList = "alts_list"
    var memberActionAccessFactions = "access_factions"
    var memberActionAccessPlayers = "access_players"
    var memberActionUpgrade = "upgrade"

    var confirmTimeOutSeconds = 10


    var placeholderCouldntBeParsedValue = "N/A"
    var placeholderNameCoundntBeParsed = "N/A"
    var placeholderNumberInvalidParse = "0"
    var playerRoleTagNoFactionPlaceholder = "None"
    var systemOwnedFactionPlaceholderValue = "System"
    var placeholderFactionNameCustom = " {name}"
    var placeholderFactionNameCustomWilderness = ""
    var placeholderRelationNonePresent = "None"
    var placeholderFactionAltsEmpty = "None"

    var internalPlaceholderRelationsColor = false


    override fun save(instance: FactionsX) {
        Serializer(false, instance.dataFolder, instance.logger)
                .save(Config.instance, File("${instance.dataFolder}/config", "general-config.json"))
    }

    override fun load(factionsx: FactionsX) {
        Serializer(false, factionsx.dataFolder, factionsx.logger)
                .load(Config.instance, Config::class.java, File("${factionsx.dataFolder}/config", "general-config.json"))
        dateCreationFormat.applyPattern(factionCreationDateFormat)
    }


}
