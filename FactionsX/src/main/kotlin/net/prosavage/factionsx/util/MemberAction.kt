package net.prosavage.factionsx.util

import com.cryptomorin.xseries.XMaterial
import net.prosavage.factionsx.persist.config.Config
import net.prosavage.factionsx.persist.config.gui.PermsGUIConfig

enum class MemberAction(val actionName: String, val icon: XMaterial) {
    KICK(Config.memberActionKick, PermsGUIConfig.kickActionIcon),
    DISBAND(Config.memberActionDisband, PermsGUIConfig.disbandActionIcon),
    INVITE(Config.memberActionInvite, PermsGUIConfig.inviteActionIcon),
    DEINVITE(Config.memberActionDeInvite, PermsGUIConfig.deInviteActionIcon),
    DEMOTE(Config.memberActionDemote, PermsGUIConfig.demoteActionIcon),
    PREFIX(Config.memberActionPrefix, PermsGUIConfig.prefixActionIcon),
    RENAME(Config.memberActionRename, PermsGUIConfig.renameActionIcon),
    UNCLAIMALL(Config.memberActionUnclaimall, PermsGUIConfig.unClaimAllActionIcon),
    CHANGE_DESCRIPTION(Config.memberActionChangeDescription, PermsGUIConfig.changeDescriptionActionIcon),
    FLY(Config.memberActionFly, PermsGUIConfig.flyActionIcon),
    HOME(Config.memberActionHome, PermsGUIConfig.homeActionIcon),
    SETHOME(Config.memberActionSetHome, PermsGUIConfig.setHomeActionIcon),
    CLAIM(Config.memberActionClaim, PermsGUIConfig.claimActionIcon),
    UNCLAIM(Config.memberActionUnclaim, PermsGUIConfig.unClaimActionIcon),
    WARP(Config.memberActionWarp, PermsGUIConfig.warpActionIcon),
    SET_WARP(Config.memberActionSetWarp, PermsGUIConfig.setWarpActionIcon),
    DEL_WARP(Config.memberActionDelWarp, PermsGUIConfig.delWarpActionIcon),
    VIEW_WARP_PASSWORD(Config.memberActionViewWarpPassword, PermsGUIConfig.viewWarpPasswordIcon),
    RELATION(Config.memberActionRelation, PermsGUIConfig.relationActionIcon),
    OPEN(Config.memberActionOpen, PermsGUIConfig.openActionIcon),
    PAYPAL_SET(Config.memberActionSetPaypal, PermsGUIConfig.paypalActionIcon),
    DISCORD_SET(Config.memberActionSetDiscord, PermsGUIConfig.discordActionIcon),
    PROMOTE(Config.memberActionPromote, PermsGUIConfig.promoteActionIcon),
    BANK_WITHDRAW(Config.memberActionBankWithdraw, PermsGUIConfig.bankWithdrawIcon),
    BANK_DEPOSIT(Config.memberActionBankDeposit, PermsGUIConfig.bankDepositIcon),
    BANK_PAY(Config.memberActionBankPay, PermsGUIConfig.bankPayIcon),
    BANK_LOGS(Config.memberActionBankLogs, PermsGUIConfig.bankLogsIcon),
    ALTS_INVITE(Config.memberActionAltsInvite, PermsGUIConfig.altsInviteIcon),
    ALTS_KICK(Config.memberActionAltsKick, PermsGUIConfig.altsKickIcon),
    ALTS_REVOKE(Config.memberActionAltsRevoke, PermsGUIConfig.altsRevokeIcon),
    ALTS_OPEN(Config.memberActionAltsOpen, PermsGUIConfig.altsOpenIcon),
    ALTS_CLOSE(Config.memberActionAltsClose, PermsGUIConfig.altsCloseIcon),
    ALTS_INVITES(Config.memberActionAltsInvites, PermsGUIConfig.altsInvitesIcon),
    ALTS_LIST(Config.memberActionAltsList, PermsGUIConfig.altsListIcon),
    ACCESS_FACTIONS(Config.memberActionAccessFactions, PermsGUIConfig.accessFactionsIcon),
    ACCESS_PLAYERS(Config.memberActionAccessPlayers, PermsGUIConfig.accessPlayersIcon),
    UPGRADE(Config.memberActionUpgrade, PermsGUIConfig.upgradeIcon);

    companion object {
        fun getFromConfigOptionName(name: String): MemberAction? {
            for (action in values()) {
                if (action.actionName.equals(name, true)) {
                    return action
                }
            }
            return null
        }
    }
}