package net.prosavage.factionsx.command.engine

import net.prosavage.factionsx.core.Permission
import net.prosavage.factionsx.core.hasPermission
import net.prosavage.factionsx.hook.vault.VaultHook
import net.prosavage.factionsx.persist.Message
import net.prosavage.factionsx.persist.config.Config
import net.prosavage.factionsx.persist.config.EconConfig
import net.prosavage.factionsx.util.MemberAction
import net.prosavage.factionsx.util.SpecialAction
import org.bukkit.command.ConsoleCommandSender
import org.bukkit.entity.Player

class CommandRequirements(
        val permission: Permission?,
        var asPlayer: Boolean,
        var asFactionMember: Boolean,
        var asLeader: Boolean,
        var memberAction: MemberAction?,
        var specialAction: SpecialAction?,
        var rawPermission: String?,
        var price: Double
) {


    fun checkRequirements(info: CommandInfo, informIfNot: Boolean = true): Boolean {
        // Check if the commandSender is a player
        if (asPlayer && !info.isPlayer()) {
            if (informIfNot) {
                info.message(Message.commandRequirementsNotAPlayer)
            }
            return false
        }


        // If the console sends it then its OP
        if (info.commandSender is ConsoleCommandSender) {
            return true
        }


        // Assume executor is player since we checked if they're ConsoleCommandSender above.
        info.commandSender as Player
        if (permission != null && !hasPermission(info.commandSender, permission)
                || (rawPermission != null && !hasPermission(info.commandSender, rawPermission!!))) {
            if (informIfNot) {
                info.message(
                        String.format(
                                Message.commandRequirementsPlayerDoesNotHavePermission,
                                permission?.getFullPermissionNode() ?: rawPermission!!
                        )
                )
            }
            return false
        }

        // Check if the player's got a faction

        if (asFactionMember) {
            if (info.fPlayer == null || !info.fPlayer!!.hasFaction()) {
                if (informIfNot) {
                    info.message(Message.commandRequirementsNotAFactionMember)
                }
                return false
            }
        }

        if (asLeader && info.fPlayer!!.getFaction().getLeader() != info.fPlayer) {
            if (informIfNot) {
                info.message(Message.commandRequirementsNotAFactionLeader)
            }
            return false
        }

        if (memberAction != null && !info.fPlayer!!.isLeader() && !info.fPlayer!!.role.canDoMemberAction(memberAction!!)) {
            if (informIfNot) {
                info.message(Message.commandRequirementsDontHaveAction, memberAction!!.actionName)
            }
            return false
        }

        if (specialAction != null && !info.fPlayer!!.isLeader() && !info.fPlayer!!.role.canDoSpecialAction(specialAction!!)) {
            if (informIfNot) {
                info.message(Message.commandRequirementsDontHaveAction, specialAction!!.name)
            }
            return false
        }

        // if neg price or 0, then we can just skip...
        if (EconConfig.economyEnabled && price > 0) {
            var totalBankBalance = info.faction!!.bank.amount;
            if (EconConfig.useVaultDirectlyIfNoMoneyInBank) {
                totalBankBalance += VaultHook.getBalance(info.fPlayer!!)
            }
            if (totalBankBalance < price) {
                if (informIfNot) {
                    info.message(Message.commandRequirementsNotEnoughMoneyInBank, Config.numberFormat.format(price))
                }
                return false
            }
        }
        // Congrats the command is valid.
        return true
    }


}



