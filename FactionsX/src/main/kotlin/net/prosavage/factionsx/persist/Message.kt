package net.prosavage.factionsx.persist

import net.prosavage.baseplugin.serializer.Serializer
import net.prosavage.factionsx.FactionsX
import net.prosavage.factionsx.manager.GridManager
import org.bukkit.ChatColor

object Message : IConfigFile {


    @Transient
    private val instance = this

    var messagePrefix = "&7[&6✕&7] "


    var commandRequirementsNotAPlayer = "&cThis command requires the executor to be a player."
    var commandRequirementsNotAFactionMember =
            "&cThis command requires the executor to be a faction member, create your very own using \"/f create\"."
    var commandRequirementsNotAFactionLeader = "&cThis command requires the executor to be a faction leader."
    var commandRequirementsDontHaveAction = "&cYour faction does not allow you to do this, it requires the action %1\$s."
    var commandRequirementsNotEnoughMoneyInBank = "&cYour bank does not have the $%1\$s required to run this command, deposit more."
    var commandRequirementsPlayerDoesNotHavePermission = "&cThis command requires the permission %1\$s"


    var commandPaidFor = "&7You paid $%1\$s from your bank to run this command."


    var commandBaseHelp = "&7The base command for factions."
    var commandBaseHelpMessage = "&aPlease execute /f help."


    var commandAdminBaseHelp = "&7The base command for server admins."
    var commandAdminBaseHelpMessage = "&aPlease execute /fax help"

    var commandHelpGeneratorPageInvalid = "&cThe page %1\$s does not exist."
    var commandHelpGeneratorFormat = "&6/%1\$s %2\$s &8> &7 %3\$s"
    var commandHelpGeneratorBackgroundColor = ChatColor.GRAY
    var commandHelpGeneratorNotRequired = "&cNo&r"
    var commandHelpGeneratorRequires = "&aYes&r"
    var commandHelpGeneratorFactionRequired = "&7Faction member requirement: %1\$s"
    var commandHelpGeneratorClickMeToPaste = "&7Click me to autocomplete."
    var commandHelpGeneratorGoTopage = "&7Go to page &6%1\$s&7."
    var commandHelpGeneratorPageNavBack = "&6<<<"
    var commandHelpGeneratorPageNavNext = "&6>>>"

    var commandEngineFormatHoverable = "&7&o((Hoverable))&r"
    var commandEngineFormatPrefix = " /%1\$s %2\$s"
    var commandEngineFormatRequiredArg = "<%1\$s>"
    var commandEngineFormatRequiredTooltip = "This argument is required."
    var commandEngineFormatOptionalArg = "(%1\$s)"
    var commandEngineFormatOptionalTooltip = "The argument is optional"

    var positionChangedTeleportWarmup = "&7You moved, teleport cancelled."

    var commandAdminPowerHelp = "manage a player's power"
    var commandAdminPowerSetSuccessfully = "&7Changed &6%1\$s's&7 power to &6%2\$s&7/&6%3\$s&7."
    var commandAdminPowerSetHelp = "set a player's power"

    var commandAdminMaxPowerHelp = "manage a player's max power"
    var commandAdminMaxPowerBoostHelp = "boost a player's max power"
    var commandAdminMaxPowerBoostSuccess = "&7Applied a delta of &6%1\$s&7 to &6%2\$s&7, their max power is now &6%3\$s&7."

    var commandAdminKickSuccess = "&7You have kicked &6%1\$s&7 from their own faction."
    var commandAdminKickNotify = "&7You have been &6kicked&7 from your faction by an administrator."
    var commandAdminKickLeader = "&7The player &6%1\$s&7 is a leader, use &6/fx disband&7 or &6/fx demote&7 first."
    var commandAdminKickNotInFaction = "&7The player &6%1\$s&7 is not part of a faction."
    var commandAdminKickHelp = "kick a player from their own faction."


    var commandShowBarElement = "&8&m="

    var commandUpgradeGlobalUpgraded = "&6%1\$s&7 has upgraded &6%2\$s&7."
    var commandUpgradeTerritoryUpgraded = "&6%1\$s &7has upgraded &6%2\$s &7in territory:&6 %3\$s @ %4\$s, %5\$s&7 in &6%6\$s&7."
    var commandUpgradeClaimRenameMessage = "&7Type the claim's new &6nickname&7, type &ccancel&7 to stop this operation."
    var commandUpgradeClaimRenameCancelled = "&7Claim rename operation was cancelled."
    var commandUpgradeClaimIconAssignMessage = "&7Type the claim's new &6icon material&7, type &ccancel&7 to stop this operation."
    var commandUpgradeClaimIconAssignCancelled = "&7Icon rename operation was cancelled."
    var commandUpgradeTerritoryNotYours = "&7You arent standing in your own territory."
    var commandUpgradeHelp = "view main upgrades menu."

    var commandParsingArgIsNotInt = "&cThis argument is not an integer, please make it one."
    var commandParsingArgIsNotRole = "&7The specified argument is an invalid role."
    var commandParsingArgIsNotDouble = "&cThis argument is not an number, please make it one."
    var commandParsingPlayerDoesNotExist = "&cThis player does not exist."
    var commandParsingArgIsNotBoolean = "&cThis argument is not a boolean, please make it 'true' or 'false'"
    var commandParsingPlayerIsYou = "&cYou cannot reference yourself."
    var commandParsingFactionDoesNotExist = "&cThe specified faction does not exist."
    var commandParsingRelationDoesNotExist = "&cThe specified relation does not exist."
    var commandParsingCannotReferenceYourOwnFaction = "&cYou cannot reference your own faction."

    var genericCommandsTooFewArgs = "&cThis command requires more arguments."
    var genericCommandsTooManyArgs = "&cThis command requires less arguments."
    var genericNotInYourFaction = "&cThe target is not in your faction."
    var genericThisIsASystemFaction = "&cThis is a system faction."
    var genericDoesNotExist = "&7This &6%1\$s &7does not exist"
    var genericActionHasTimedOut = "&cYour action has timed out, please restart the confirmation process."
    var genericTransactionSuccessTake = "&6%1\$s&7 was taken from your account, &6%2\$s&7 left."
    var genericTransactionTakeError = "&7An error occurred: &6%1\$s."
    var genericNoEconomyProvider = "&7No Economy provider was found, please install &6Vault&7."

    var territoryCommandDenied = "&7You &6cannot&7 use this command in this faction territory."

    var commandFlyHelp = "enable/disable factions fly."

    var commandFactionsBypassUpdate = "&7Your bypass mode is now &6%1\$s"
    var commandFactionsBypassTrue = "enabled"
    var commandFactionsBypassFalse = "disabled"
    var commandFactionsBypassHelp = "toggle factions superuser mode."

    var commandAdminRelationFactionsEqual = "&7You must define two unique factions."
    var commandAdminRelationSystemFaction = "&7You cannot force relations upon system factions."
    var commandAdminRelationAlready = "&6%s &7and &6%s &7already stand by relation &6%s&7."
    var commandAdminRelationSet = "&7You have set the relation between &6%s &7and &6%s &7to &6%s&7."
    var commandAdminRelationHelp = "set the relation between two factions"

    var commandAdminReloadHelp = "reload the plugin."
    var commandAdminReloadAddonNotFound = "&7The specified addon was not found."
    var commandAdminReloadStoppedTasks = "&7Aborted all scheduler tasks from FactionsX & Addons."
    var commandAdminReloadAddon = "&7You have reloaded &6%1\$s&7."
    var commandAdminReloadHotReload = "&7Hot reloading jar files &8&o(This is an unsafe operation)&7."
    var commandAdminReloadConfigs = "&7Reloading all configs"

    var commandAdminReloadStartedTasks = "&7Started FactionsX tasks."
    var commandAdminReloadedPlugin = "&7Reloaded &6FactionsX&7. Time Taken &6%1\$sms&7."


    var commandRolesInfoHeader = "&7Information about &6%1\$s"
    var commandRolesInfoChatTag = "&6ChatTag: &7%1\$s &7&o(Click to change)"
    var commandRolesInfoChatTagToolTip = "&7Change role's chatTag"
    var commandRolesInfoRoleTag = "&6RoleTag: &7%1\$s &7&o(Click to change)"
    var commandRolesInfoRoleTagToolTip = "&7Change role's roleTag"
    var commandRolesInfoPermissions = "&6Permissions: &7&o(Click to view / edit in perms menu)"
    var commandRolesInfoPermissionsToolTip = "&7Change role's permissions"
    var commandRolesInfoIconMaterial = "&6Icon Material: &7%1\$s &7&o(Click to change)"
    var commandRolesInfoIconMaterialToolTip = "&7Change role's icon material"
    var commandRolesInfoSendBar = true
    var commandRolesInfoBarElement = "&7&m-"
    var commandRolesInfoHelp = "&7View a role's info."

    var commandRolesListEntry = "&7%1\$s. &6%2\$s &7&o(Click for more info)"
    var commandRolesListMoveUp = " &6▲ "
    var commandRolesListMoveUpTooltip = "&7Move role up."
    var commandRolesListMoveDown = " &6▼ "
    var commandRolesListMoveDownTooltip = "&7Move role down."
    var commandRolesListMoveDelete = " &6⨉ "
    var commandRolesListMoveDeleteTooltip = "&7Delete role"
    var commandRolesListHeader = "&7Listing all faction roles from top to bottom."
    var commandRolesListEntryToolTip = "&7View %1\$s's info."
    var commandRolesListHelp = "&7list all of your faction's roles."
    var commandRolesListSendBar = true
    var commandRolesListBarElement = "&7&m-"
    var commandRolesHelp = "&7view/edit faction roles."


    var commandRolesMoveSameRole = "&7You cannot specify the same roles."
    var commandRolesMoveSuccess = "&7Roles moved."
    var commandRolesMoveHelp = "&7move a role around."

    var commandRolesRemoveApexRole = "&7The role specified is the apex role, and cannot be removed."
    var commandRolesRemoveSuccess = "&7Removed role &6%1\$s"
    var commandRolesRemoveNotEnough = "&7You do not have enough roles in the faction to remove one."
    var commandRolesRemoveHelp = "&7remove a faction role."

    var commandRolesAddSuccess = "&7Added new role to bottom of heirachy."
    var commandRolesAddInvalidXMat = "&7Invalid material specified.\n&7Use &6https://toolkit.savagelabs.net/tools/xmaterial"
    var commandRolesAddHelp = "&7add a role to the faction."

    var commandRolesEditProperties = "&7Invalid property, valid properties are: \n &7%1\$s"
    var commandRolesEditValues = "&7Please add a value to set to."
    var commandRolesEditHelp = "&7edit a faction role."

    var commandAutoClaimToggleOn = "on"
    var commandAutoClaimToggleOff = "off"
    var commandAutoClaimToggle = "&7You have toggled autoclaim &6%1\$s&7."
    var commandAutoClaimHelp = "autoclaim for your faction."

    var commandAdminInviteSent = "&7Target player has been invited to &6%1\$s&7."
    var commandAdminInviteAlreadyHasFaction = "&7The specified player is already part of a faction."
    var commandAdminInviteSystemFaction = "&7A player cannot be invited to a system faction."
    var commandAdminInviteHelp = "&7Invite a player to a faction."

    var commandAdminJoinSuccess = "&7You have joined &6%1\$s&7."
    var commandAdminJoinSystemFaction = "&7You cannot join &6system&7 factions."
    var commandAdminJoinPartOfFaction = "&7You are &6already&7 a faction member, please &6leave&7 your current faction first."
    var commandAdminJoinHelp = "join a specific faction."

    var commandAdminAutoClaimToggle = "&7You have toggled autoclaim &6%1\$s&7 for the faction &6%2\$s&7."
    var commandAdminAutoClaimHelp = "autoclaim for a specifc faction."

    var commandUnClaimMaxRadius = "&7You cannot use a radius higher than &6%1\$s."

    var commandClaimAtExploit = "&7You cannot claim past map borders."
    var commandClaimAtHelp = "claim land for a faction at a specific location"

    var commandClaimHelp = "claim land for faction."
    var commandClaimMaxRadius = "&7You cannot claim higher than a radius of &6%1\$s &7at one time."
    var commandClaimSuccess = "&7You claimed &6%1\$s &7chunks of land successfully"
    var commandClaimSuccessEconEnabled = "&7You claimed &6%1\$s &7chunks of land successfully for &6$%2\$s&7."
    var commandClaimNotEnoughMoney = "&7Your faction bank does not have the &6$%1\$s&7 needed to claim &6%2\$s&7 claims. Use &6/f bank deposit&7."
    var commandClaimFailuresHeader = "&6%1\$s&7 claims failed:"
    var commandClaimFailuresFormat = "&6%1\$sx&7 due to &6%2\$s"

    // Maintaing clone because of lang files having missing entries later on.
    var commandClaimFailureReasons: HashMap<GridManager.ClaimError, String> = GridManager.claimErrors.clone() as HashMap<GridManager.ClaimError, String>
    var commandClaimSuccessFactionNotify = "&6%1\$s&7 has claimed &6%2\$s &7chunks of land."
    var commandClaimSuccessEconEnabledFactionNotify = "&6%1\$s&7 has claimed &6%2\$s &7chunks of land for &6$%1\$s."
    var commandClaimNotEnoughMembers = "&7You need more then &6%1\$s&7 members to claim land."
    var commandClaimHomeAutoSet = "&7Your &6faction's&7 home was automatically set at X:&6%1\$s&7 Y:&6%2\$s&7 Z:&6%3\$s&7."

    var commandUnclaimHelp = "unclaim land for your faction."
    var commandUnclaimSuccess = "&7You have unclaimed &6%1\$s &7chunks of land."
    var commandUnclaimSuccessFactionNotify = "&6%1\$s&7 has unclaimed &6%2\$s &7chunks of land."

    var commandPowerHelp = "view your power related information."
    var commandPowerInfo = "&6%1\$s Power: &6%2\$s&7/&6%3\$s"

    var commandLeaveAsLeader = "&7You are the leader of the faction, &6promote&7 someone else, or &6disband&7 the faction to leave."
    var commandLeaveInform = "&6%1\$s &7has left your faction."
    var commandLeaveSuccess = "&7You have successfully left your faction."
    var commandLeaveHelp = "leave your faction."

    var commandKickNotSameFaction = "&7The target is not in your faction!"
    var commandKickRoleNotHighEnough = "&7You do not have permission to kick the target."
    var commandKickIsAltSuggestion = "&7This player is an ALT. Did you mean '&6/f alts kick <alt>&7'?"
    var commandKickSuccess = "&7Kicked &6%1\$s&7 from your faction."
    var commandKickTargetInform = "&7You have been kicked from &6%1\$s&7 by &6%2\$s&7."
    var commandKickHelp = "kick a player from your faction."

    var commandChatChannelChange = "&7Your chat channel is now &6%1\$s&7."
    var commandChatInvalidChannel = "&7This is an &6invalid&7 channel."
    var commandChatHelp = "change your chat channel."

    var commandAdminClaimNonNegativeRadius = "&7The given radius cannot be negative."
    var commandAdminClaimHelp = "claim land for other factions."

    var commandSeeChunkEnabled = "enabled"
    var commandSeeChunkDisabled = "disabled"
    var commandSeeChunkToggle = "&7Chunk borders are now &6%1\$s&7."
    var commandSeeChunkHelp = "&7toggle particle chunk borders."

    var commandChunkMessageEnabled = "enabled"
    var commandChunkMessageDisabled = "disabled"
    var commandChunkMessage = "&7Chunk change messages are now &6%s&7."
    var commandChunkMessageHelp = "toggle chunk messages"

    var commandAdminUnClaimNonNegativeRadius = "&7The given radius cannot be negative."
    var commandAdminUnClaimHelp = "unclaim land for other factions."

    var relationMax = "&7You cannot set any more &6%1\$s&7 factions. Upgrade &6%1\$s&7 faction limit to get more."
    var relationAlreadySet = "&7You are already %1\$s to %2\$s."
    var commandEnemyHelp = "enemy a faction"
    var commandAllyHelp = "ally a faction"
    var commandNeutralHelp = "neutral a faction"
    var commandTruceHelp = "truce a faction"
    var relationUpdate = "&7Your faction's relation to &6%1\$s &7is now &6%2\$s%3\$s"
    var relationNotif = "&7The faction &6%1\$s&7 has requested to become %2\$s%3\$s&7."
    var relationNotifOrigin = "&7The faction &6%1\$s &7has been requested to become %2\$s%3\$s&7."
    var relationAlreadyRequested = "&7Your faction has already requested to %1\$s%2\$s &6%3\$s&7!"
    var relationIsDisabled = "&7This relation has been disabled!"

    var commandHereHelp = "view current location's faction."
    var commandHereInfo = "&7You are currently standing in &6%1\$s&7's land."

    var commandPermsInfo = "change your faction perms."

    var commandPermsRoleMenuHelp = "&7view a role's submenu."

    var commandAdminPowerBoostHelp = "apply a change to a player's power"
    var commandAdminPowerBoostSuccess = "&7Applied a delta of &6%1\$s&7 to &6%2\$s&7, their power is now &6%3\$s&7."


    var commandCreateHelp = "create a new faction."
    var commandCreateAlreadyHaveFaction = "&7You &6already &7have a faction"
    var commandCreateSuccess = "&7Successfully created a faction called &6%1\$s&7."
    var commandCreateNonAlphaNumeric = "&7This faction tag is not &6alphanumeric&7."
    var commandCreateLength = "&7Faction tag must be between &6%1\$s&7 and &6%2\$s&7 characters long."
    var commandCreateFactionAlreadyExists = "&7The faction named &6%1\$s &7already exists!"
    var commandCreateFactionNotEnoughMoney = "&7You do not have &6$%1\$s &7to spend on creating a faction!"
    var commandCreateAnnouncement = "&6%s &7has created the faction &6%s&7."

    var commandDisbandHelp = "disband a faction."
    var commandDisbandConfirmation = "&7You are about to disband your faction.\n &7If you are sure you want to do that, run the command again in the next &6%1\$s &7seconds."
    var commandDisbandSuccess = "&7Successfully disbanded your faction."

    var commandAdminDisbandSystemFac = "&7You cannot disband a &6system faction&7."
    var commandAdminDisbandNotify = "&7Your faction has been &6disbanded&7 by an admin."
    var commandAdminDisbandHelp = "disband a faction as admin"
    var commandAdminDisbandSuccess = "&7You have successfully disbanded &6%1\$s&7."

    var commandInviteHelp = "Invite a player."
    var commandInviteSent = "&7You have sent the invite to &6%1\$s"
    var commandInviteSentNotify = "&7You have been invited to &6%1\$s &7by &6%2\$s."
    var commandInviteSentNotifyTooltip = "&7Click to paste &6\"/f join %1\$s\""
    var commandInviteFactionMemberLimitReached = "&7You have reached the faction member limit &8(&6%1\$s&8)"
    var commandInviteAlreadyPartOfFaction = "&7Player &6%1\$s &7is already a part of another faction."
    var commandInviteAlreadyPartOfYourFaction = "&7This player is already a part of your faction."

    var commandHelpTitle = "&7Use &6/f help <page>&7 or arrows to navigate."
    var commandHelpHelp = "&7View help commands."
    var commandAdminHelpHelp = "&7View admin help commands."

    var commandJoinHelp = "join a faction."
    var commandJoinAlreadyHave = "&7You already have a faction, leave or disband it first."
    var commandJoinNotInvited = "&7You have not been invited to the faction &6%1\$s."
    var commandJoinSuccess = "&7You have successfully joined &6%1\$s."
    var commandJoinAnnouncement = "&6%1\$s&7 has joined your faction."
    var commandJoinSuggest = "&7If you are trying to claim land for warzone/safezone.\n&7Format: /fx claim &6<faction> <radius>\n&7Ex: /fx claim &6warzone 1"

    var commandMapToggledOnMessage = "on"
    var commandMapToggledOffMessage = "off"
    var commandMapToggled = "&7Faction map has been toggled %1\$s."
    var commandMapHelp = "view claims of factions around you."

    var commandUnClaimAll = "&6%1\$s &7unclaimed ALL of your faction's land."
    var commandUnClaimAllHelp = "unclaim ALL of your faction's land."

    var commandAdminUnclaimAllSuccess = "&7You &6successfully unclaimed &7ALL of &6%1\$s's &7land."
    var commandAdminUnClaimAll = "&6%1\$s &7unclaimed ALL of your faction's land."
    var commandAdminUnClaimAllHelp = "unclaim ALL of a faction's land."

    var commandAdminChatSpyEnabled = "&7You have &6enabled &7your chat spy."
    var commandAdminChatSpyDisabled = "&7You have &6disabled &7your chat spy."
    var commandAdminChatSpyFormat = "&8[&6Chat Spy&8] &6%1\$s &8[&7%2\$s&8]: &7%3\$s"
    var commandAdminChatSpyHelp = "toggle chatspy mode"

    var commandStrikesHelp = "check your faction's strikes"
    var commandAdminStrikesHelp = "manage a faction's strikes."

    var commandStrikesMetMax = "&7The faction &6&n%s&7 has already met the maximum amount of strikes!"
    var commandStrikesNotFoundById = "&7The faction &6&n%s&7 does not have a strike with id of &6&n%s&7!"
    var commandStrikesNoStrikes = "&7The faction &6&n%s&7 does not have any strikes!"
    var commandStrikesFormat = "&7 &7- &6{id}&7) &6{reason}"
    var commandStrikesView = listOf(
            "&6{tag}&7's strikes (&6{strikes}&7/&6{strikes_max}&7):",
            "{strikes_list}"
    )

    var commandStrikesGive = "&7You have given &6&n%s&7 a strike for '&6%s&7'."
    var commandStrikesGiveAlert = "&7Your faction has received a strike for '&6%s&7' &7by &6&n%s&7."
    var commandStrikesGiveHelp = "give a strike to a faction"

    var commandStrikesRemove = "&7You have removed the strike with id &6&n%s&7 from &6&n%s&7."
    var commandStrikesRemoveAlert = "&7Your faction's strike with id &6&n%s&7 has been erased by &6&n%s&7."
    var commandStrikesRemoveHelp = "remove a strike from a faction"

    var commandStrikesEdit = "&7You have edited the strike with id &6&n%s&7 for &6&n%s&7 to '&6%s&7'."
    var commandStrikesEditAlert = "&7Your faction's strike with id &6&n%s&7 has been edited to '&6%s&7' by &6&n%s&7."
    var commandStrikesEditHelp = "edit a strike for a faction"

    var commandStrikesClear = "&7You have cleared &6&n%s&7's strikes."
    var commandStrikesClearAlert = "&7Your faction's strikes has been cleared by &6&n%s&7."
    var commandStrikesClearHelp = "clear the strikes of a faction"

    var commandStrikesCheckHelp = "check the strikes of a faction"

    var commandCreditsAdminHelp = "manage credits for a player."

    var commandCreditsHelp = "manage credits."

    var commandCreditsPay = "&7You have sent &6%1\$s %2\$s &7credit(s)!"
    var commandCreditsPayNotify = "&7You have been sent &6%1\$s &7credit(s) by &6%2\$s!"
    var commandCreditsPayMinAmount = "&7You need to send a minimum of &f%1\$s credits."
    var commandCreditsPayHelp = "pay credits to a player."

    var commandCreditsRemove = "&7You have taken &6%1\$s &7in credits from &6%2\$s!"
    var commandCreditsRemoveNotify = "&7You have had &6%1\$s &7in credits taken!"
    var commandCreditsRemoveHelp = "take credits from a player."

    var commandCreditsGive = "&7You have added &6%1\$s &7credit(s) to &6%2\$s!"
    var commandCreditsGiveNotify = "&7You have been given &6%1\$s &7credit(s)!"
    var commandCreditsGiveHelp = "add credits to a player."

    var commandCreditsReset = "&7You have reset &6%1\$s &7credit's to &6%2\$s!"
    var commandCreditsResetNotify = " &7You have had your credits reset to &6%1\$s!"
    var commandCreditsResetHelp = "reset a player's credits."

    var commandCreditsBalance = "&7You have &6%1\$s &7credit(s)!"
    var commandCreditsBalanceOthers = "&7The player &6%1\$s &7has &6%2\$s &7credit(s)!"
    var commandCreditsBalanceHelp = "check a player's credits balance or your own."

    var commandCreditsMaximumAmount = "&7The player &6%1\$s &7has reached the limit of &6$2\$s credits."

    var commandCreditsNotEnough = "&7You do not have enough credits to send. Balance &6%1\$s credits."
    var commandCreditsNotEnoughSender = "&7This player does not have enough credits to take."

    var commandBankHelp = "manage bank"
    var commandBankUnavailable = "&7This feature is currently disabled!"

    var commandBankBalanceHelp = "check your faction's balance"
    var commandBankBalanceSuccess = "&7Your faction's bank balance: &6%1\$s"

    var commandBankDepositHelp = "deposit money to your faction's bank"
    var commandBankDepositNotEnough = "&7You do not have &6%1\$s &7to deposit!"
    var commandBankDepositSuccessful = "&7You have successfully deposited &6%1\$s &7into your faction's bank. Total: &6%2\$s&7."

    var commandBankWithdrawHelp = "withdraw money from your faction's bank"
    var commandBankWithdrawNotEnough = "&7Your faction's bank does not have &6%1\$s &7for you to withdraw!"
    var commandBankWithdrawSuccessful = "&7You have successfully withdrawn &6%1\$s &7from your faction's bank. Total: &6%2\$s&7."

    var commandBankPayHelp = "pay another faction"
    var commandBankPayNotEnough = "&7Your faction's bank do not have &6%1\$s &7to pay &6%2\$s&7!"
    var commandBankPaySuccessful = "&7You have successfully paid &6%1\$s &7to the faction &6%2\$s&7!"
    var commandBankPaySuccessfulOther = "&7Your faction has received &6%1\$s &7from &6%2\$s&7!"

    var commandBankLogHelp = "check your faction's bank logs"
    var commandBankLogEmpty = "&7Found &60 &7out of &60 &7logs..."
    var commandBankLogPageNotFound = "&7Could not find the page &6%s&7."

    var commandAltsHelp = "manage your faction's alts"

    var commandAltsInviteHelp = "invite an alt to your faction"
    var commandAltsInviteLimitReached = "&7Your faction has met the limit of ALTs."
    var commandAltsInvited = "&7You have invited &6%s &7to become an ALT in your faction."
    var commandAltsInvitedNotify = "&7[&6✕&7] &7You have been invited to become an ALT in &6%s &7by &6%s&7."
    var commandAltsInvitedNotifyTooltip = "&7Click to join &6%s &7as an ALT."

    var commandAltsJoinHelp = "join a faction as an alt"
    var commandAltsJoinNotInvited = "&7You have not been invited to join &6%s &7as an ALT."
    var commandAltsJoinSuccess = "&7You have successfully joined &6%s &7as an ALT."
    var commandAltsJoinAnnouncement = "&6%s &7has joined the faction as an ALT."

    var commandAltsRevokeHelp = "revoke a player's ALT invitation to your faction"
    var commandAltsRevokeAlreadyInFaction = "&6%s &7is already in your faction and you cannot revoke their invite."
    var commandAltsRevokeNotPresent = "&6%s &7has not been invited to your faction."
    var commandAltsRevoked = "&7You have successfully revoked &6%s&7's invitation to your faction."
    var commandAltsRevokedAll = "&6%s&7's invitation to your faction has been revoked by &6%s&7."

    var commandAltsOpenHelp = "open up the faction for public ALTs"
    var commandAltsOpenAlready = "&7Your faction is already opened for public ALTs."
    var commandAltsOpenSuccess = "&7You have successfuly opened up your faction for public ALTs."
    var commandAltsOpenSuccessAll = "&6%s &7has opened up your faction for public ALTs."

    var commandAltsCloseHelp = "close down the faction for public ALTs"
    var commandAltsCloseAlready = "&7Your faction is already closed for public ALTs."
    var commandAltsCloseSuccess = "&7You have successfuly closed down your faction for public ALTs."
    var commandAltsCloseSuccessAll = "&6%s &7has closed down your faction for public ALTs."

    var commandAltsInvitesHelp = "view a list of ALTs invited to your faction"
    var commandAltsInvitesEmpty = "&7There are no present invites."
    var commandAltsInvitesPresent = "&7Invited alts (&6%s&7): %s"

    var commandAltsListHelp = "view a list of current ALTs"
    var commandAltsListEmpty = "&7There are no present ALTs."
    var commandAltsListPresent = "&7Your faction's ALTs (&6%s&7): %s"

    var commandAltsKickHelp = "kick an alt from your faction"
    var commandAltsKickNotInFaction = "&7This player is not in your faction."
    var commandAltsKickMemberSuggestion = "&7This player is not an ALT. Did you mean '&6/f kick <member>&7'?"
    var commandAltsKickSuccess = "&7You have kicked the ALT &6%s &7from your faction."
    var commandAltsKickAll = "&7The ALT &6%s &7has been kicked from your faction by &6%s&7."

    var commandSetHomeNotif = "&6%1\$s&7 has changed your faction home."
    var commandSetHomeSuccess = "&7You have set your &6faction's&7 home."
    var commandSetHomeOwnClaim = "&7You cannot set home outside your own territory."
    var commandSetHomeHelp = "set your faction's home"

    var commandShieldHelp = "&7set or check your faction's shield time"
    var commandShieldNotAllowed = "&7You're not the leader of this faction and cannot set the shield!"
    var commandShieldNotEnabled = "&7Shields are currently not enabled!"

    var commandHomeEnemyLand = "&7You cannot teleport in &4ENEMY&7 territory."
    var commandHomeTeleportDenied = "&7Another plugin does not allow you to teleport to your faction home."
    var commandHomeNotSet = "&7Your faction home has not been set."
    var commandHomeInvalid = "&7Your home does not exist anymore & has been reset."
    var commandHomeTeleporting = "&7Teleporting to home in &6%1\$s&7 seconds... &4DONT MOVE"
    var commandHomeHelp = "go to your faction home"

    var commandAdminSetRoleLeaderNotify = "&7You have been demoted from &6leader&7, by an admin."
    var commandAdminLeaderOnly = "&7Cannot set role, only 1 player in faction."
    var commandAdminSetRoleInvalidRole = "&7The role provided is invalid, valid roles are:"
    var commandAdminSetRoleNotif = "&7Your role has been set to &6%1\$s&7 by an admin."
    var commandAdminSetRoleFactionRequired = "&6%1\$s&7 is not in a faction."
    var commandAdminSetRoleSuccess = "&7You have set &6%1\$s's&7 role to &6%2\$s&7."
    var commandAdminSetRoleHelp = "set a faction member's role."

    var commandPromoteTransferOwnership = "&7You are about to transfer ownership of your faction to &6%1\$s&7.\n &7If you are sure you want to do that, run the command again in the next &6%2\$s&7 seconds."
    var commandPromoteHelp = "promote a member of your faction."
    var commandPromoteSuccess = "&6%1\$s &7promoted you from &6%2\$s -> %3\$s&7."
    var commandPromoteDone = "&7Promoted &6%1\$s &7to &6%2\$s"
    var commandPromoteCouldNotBePromoted = "&7You could &6not&7 be promoted any further."
    var commandPromoteCannotPromoteHigherRole = "&7You cannot &6promote&7 this player to a higher role."
    var commandPromoteCannotPromote = "&7You &6cannot&7 promote this player."
    var commandPromoteIsAlt = "&7You cannot promote an ALT."

    var commandPrefixSet = "&7You have set &6%1\$s's &7prefix to &6%2\$s."
    var commandPrefixRecipientSet = "&7Your prefix has been set to &6%1\$s."
    var commandPrefixRecipientCleared = "&7Your prefix has been cleared."
    var commandPrefixCleared = "&7You cleared &6%1\$s's &7prefix."
    var commandPrefixHelp = "edit a member's faction prefix."

    var commandDemoteCouldNotBeDemoted = "&7You could not be demoted."
    var commandDemoteCannotDemote = "&7You cannot demote this player."
    var commandDemoteSuccess = "&7You have been demoted by &6%1\$s &7from &6%2\$s -> %3\$s&7."
    var commandDemoteWork = "&7Demoted &6%1\$s &7to &6%2\$s."
    var commandDemoteHelp = "demote a member of your faction."

    var commandWhoNoSystemFactions = "&7You cannot view a system faction's information."
    var commandWhoHelp = "view another faction's information"

    var commandRenameFactionNameIsTaken = "&7This faction name is already in use."
    var commandRenameAnnounce = "&7Your faction has been renamed to &6%1\$s."
    var commandRenameSuccess = "&7You have renamed your faction to &6%1\$s."
    var commandRenameHelp = "rename your faction."

    var commandAdminRenameSystemFaction = "&7You cannot rename a &6system faction&7."
    var commandAdminRenameNotify = "&7Your faction's tag has been &6renamed &7by an admin."
    var commandAdminRenameSuccess = "&7You have successfully renamed &6%s &7to '&6%s&7'."

    var commandDeinviteRecipient = "&6%1\$s &7has revoked your invite from &6%2\$s."
    var commandDeinviteSuccess = "&7Revoked invite from &6%1\$s"
    var commandDeinviteNotInvited = "&7This player has &6not&7 been invited to your faction."
    var commandDeinviteHelp = "revoke a invite to your faction."

    var commandInvitesInvitedTo = "&7Faction Invites:"
    var commandInvitesSentNotifyTooltip = "&7Click to paste &6\"/f join %1\$s\""
    var commandInvitesSentNotify = "&7You have been invited to &6%1\$s."
    var commandInvitesHelp = "view faction invites."

    var commandPermsRoleHelp = "edit the permissions for faction roles."
    var commandPermsRoleInvalidStatus = "&7The status argument needs to be either &6true &7or &6false&7."

    var commandPermsRoleActionInvalid = "&7The specified action is invalid."
    var commandPermsRoleSuccess = "&7You have &6%1\$s &7permission for &6%2\$s &7to &6%3\$s."
    var commandPermsRoleAdded = "added"
    var commandPermsRoleRemoved = "removed"

    var commandIgnoreInvalidChannel = "&7This is not a &6chatchannel."
    var commandIgnoreNotPublic = "&7You cannot ignore the &6public&7 chatchannel."
    var commandIgnoreIgnored = "&7You are now ignoring &6%1\$s &7chatchannel"
    var commandIgnoreNotIgnoring = "&7You are no longer ignoring &6%1\$s &7chatchannel"
    var commandIgnoreHelp = "ignore a chatchannel."

    var commandWarpsGoWarpDoesNotExist = "&7This warp does not exist."
    var commandWarpsGoSuccess = "&7Going to warp &6%1\$s &7in &6%2\$s seconds..."
    var commandWarpsGoLocationNoLongerExists = "&7This warp location no longer exists, please delete it."
    var commandWarpTeleportDenied = "&7Another plugin does not allow you to teleport to this warp."
    var commandWarpsInvalidPassword = "&7This password is invalid, you can use &6\"/f warps list\" &7to view passwords if you have permission to."
    var commandWarpsGoRequiresPassword = "&7This warp requires a password."
    var commandWarpsGoHelp = "go to a faction warp."

    var commandNearHeader = "&7Nearby Faction Members."
    var commandNearFormat = "&6%1\$s. &6%2\$s &8&o((&6%3\$s &7blocks away&8&o))"
    var commandNearHelp = "view nearby players."

    var commandListHeader = "&7Faction List"
    var commandListEmpty = "&7There are no factions on the server."
    var commandListTooHighIndex = "&7This page does not exist."
    var commandListTooLow = "&7Please start with a page number of one"
    var commandListFormat = "&7%1\$s. &6%2\$s &8&o((&6%3\$s/%4\$s &7online&8&o))"
    var commandListHelp = "view factions on the server."

    var commandWarpsListHeader = "&7Faction Warps - &8&o((&7hover to view password&8&o))"
    var commandWarpsListFormat = "&7%1\$s. &6%2\$s"
    var commandWarpsNoPasswordToolTipText = "no password"
    var commandWarpsListTooltip = "&7Password: &6%1\$s"
    var commandWarpsListCantViewPasswordTooltip = "&7Your faction does &6not&7 allow you to &6view warp passwords&7."
    var commandWarpsListNone = "&7Your faction has &6no warps&7 set."
    var commandWarpsListHelp = "view faction warps."


    var commandWarpsSetSuccess = "&7Successfully set a warp called &6%1\$s&7."
    var commandWarpsSetPasswordConfirmFailed = "&7Your passwords do not match."
    var commandWarpsConfirmPassword = "&7You need to &6confirm&7 your password."
    var commandWarpsSetLimit = "&7You cannot set more warps, upgrade your faction to get more."
    var commandWarpsSetAlreadyExists = "&7A warp with the name &6\"%1\$s\"&7 already exists."
    var commandWarpsSetNotInOwnClaim = "&7You &6cannot&7 set a warp outside your own claim."
    var commandWarpsSetHelp = "set a faction warp."

    var commandDescSuccess = "&7You have updated your faction's &6description&7."
    var commandDescAnnounce = "&6%1\$s&7 has updated your faction's &6description&7."
    var commandDescHelp = "change your faction description."


    var commandWarpsRemoveDoesNotExist = "&7This warp &6%1\$s&7 does not exist."
    var commandWarpsRemoveSuccess = "&7You have &6successfully&7 removed the warp &6%1\$s."
    var commandWarpsRemoveHelp = "remove a warp"
    var commandWarpsHelp = "use faction warps."

    var commandFactionStatusSwitch = "You have now set your &6faction open status&7 to &6%1\$s."
    var commandFactionsOpen = "open"
    var commandFactionsClosed = "closed"
    var commandFactionsOpenHelp = "open your faction."

    var commandPaypalSet = "&7You have set a paypal for your &6faction! &7Use &6/f paypal view &7to see it!"
    var commandPaypalSetHelp = "set a paypal for your faction."
    var commandPaypalViewYourOwn = "&7The paypal for your faction is &6%1\$s"
    var commandPaypalView = "&7The paypal for the faction &6%1\$s &7is &6%2\$s."
    var commandPaypalViewHelp = "view your faction's paypal."

    var commandDiscordSet = "&7You have set a discord for your &6faction! &7Use &6/f discord &7to see it!"
    var commandDiscordSetHelp = "set a discord for your faction."
    var commandDiscordViewYourOwn = "&7The discord for your faction is &6%1\$s"
    var commandDiscordView = "&7The discord invite for the faction &6%1\$s &7is &6%2\$s."
    var commandDiscordViewHelp = "view your faction's discord"

    var commandDiscordHelp = "the base command for discord help"
    var commandPaypalHelp = "the base command for paypal help"

//    var commandMuteListHelp = "list muted faction members"
//    var commandMuteRemoveRemoved = "&7You have unmuted &6%1\$s&7."
//    var commandMuteRemoveNotif = "&7You have been unmuted from faction chat."
//    var commandMuteRemoveHelp = "unmute a faction member"
//    var commandMuteAddHelp = "mute a faction member"

    var commandPermsRelationHelp = "edit the relational permissions for faction roles."
    var commandPermsRelationOverriden = "&7This permission is &6overriden&7 by the server and &6MUST&7 be &6%1\$s."
    var commandPermsRelationSuccess = "&7Successfully set permission &6%1\$s&7 to &6%2\$s&7 for &6%3\$s&7."

    var commandCoordsHelp = "provide your faction with your current coords."
    var commandCoordsFormat = "&6{location-world}&7, &6{location-x}&7, &6{location-y}&7, &6{location-z}&7."

    var commandAccessHelp = "modify the access for other factions and players in your territories"
    var commandAccessSpecifyName = "&cYou need to specify the name of a faction/player."
    var commandAccessInvalidType = "&cYou need to specify a valid type. Availability: &ffaction&c, &fplayer&c."
    var commandAccessNoFactionFound = "&cCould not find a faction by the name of &f&n%s&c!"
    var commandAccessNoPlayerFound = "&cCould not find a player by the name of &f&n%s&c!"
    var commandAccessYourFaction = "&cYou cannot open up the access menu for your own faction!"
    var commandAccessYourPlayerFaction = "&cYour cannot open up the access menu for a player in your own faction!"

    //var listenerOwnFaction = "&7You cannot hurt %1\$s&7 as they're a member of your &a&lown&7 faction."
    var listenerTriedToHurtYou = "&6%1\$s &7tried to hurt you."
    var listenerPlayerCannotDoThisHere = "&7You aren't allowed to &6%1\$s &7in &6%2\$s."
    var listenerCannotHurtOwnFaction = "&7You aren't allowed to hurt members of your own faction."
    var listenerPlayerCannotHurtRelation = "&7You can't &6hurt&7 this player as they're an &6%1\$s&7 faction."
    var actionDeniedInOtherFactionsLand = "&7You are not allowed to &6%1\$s&7 in the territory of &6%2\$s&7."
    var blockBreakDeniedEnemyNearby = "&7You cannot break &6%1\$s&7 as an enemy is nearby&7."
    var blockChangeDeniedInOtherFactionsLand = "&7You are not allowed to %1\$S &6%2\$s&7 in the territory of &6%3\$s&7."
    var youCannotInteractWithThisEntity = "&7You are not allowed to interact with &6%1\$s&7 in the territory of &6%2\$s&7."
    var youCannotDamageThisGadget = "&7You are not allowed to damage &6%1\$s &7in the territory of &6%2\$s&7."
    var deathPowerUpdate = "&7Your power is now &6%1\$s&7."

    var altUseBucket = "&7You are not allowed to use buckets as an ALT!"
    var altHurtEntities = "&7You are not allowed to hurt entities as an ALT!"
    var altBuildBreak = "&7You are not allowed to place/break blocks as an ALT!"
    var altInteractEntity = "&7You are not allowed to interact with entities as an ALT!"
    var altInteract = "&7You are not allowed to interact as an ALT!"

    var shieldStarted = "&7Your faction's shield has started!"
    var shieldEnded = "&7Your faction's shield has ended!"
    var shieldStartsIn = "&7Your shield starts in: &6%s"
    var shieldEndsIn = "&7Your shield ends in: &6%s."
    var shieldTimeSet = "&7Shield start time set to %1\$s."

    var nearbyEnemyDisallowedCommands = "&cAn enemy is nearby, you cannot execute &4&n%s&c!"


    override fun save(factionsx: FactionsX) {
        Serializer(false).save(instance)
    }

    override fun load(factionsx: FactionsX) {
        Serializer(false).load(instance, Message::class.java, "message")
    }
}

fun color(line: String): String {
    return ChatColor.translateAlternateColorCodes('&', line)
}
