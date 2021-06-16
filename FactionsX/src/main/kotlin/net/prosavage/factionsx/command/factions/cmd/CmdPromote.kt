package net.prosavage.factionsx.command.factions.cmd

import net.prosavage.factionsx.FactionsX
import net.prosavage.factionsx.command.engine.CommandInfo
import net.prosavage.factionsx.command.engine.CommandRequirementsBuilder
import net.prosavage.factionsx.command.engine.ConfirmAction
import net.prosavage.factionsx.command.engine.FCommand
import net.prosavage.factionsx.core.Permission
import net.prosavage.factionsx.event.ConfirmationEvent
import net.prosavage.factionsx.manager.PlayerManager
import net.prosavage.factionsx.persist.Message
import net.prosavage.factionsx.persist.config.Config
import net.prosavage.factionsx.persist.config.EconConfig
import net.prosavage.factionsx.util.MemberAction
import org.bukkit.Bukkit

class CmdPromote : FCommand() {
    init {
        aliases.add("promote")

        requiredArgs.add(Argument("player-name", 0, FactionMemberArgument()))
        optionalArgs.add(Argument("role", 1, PromotableRoleArgument()))

        commandRequirements = CommandRequirementsBuilder()
                .withPermission(Permission.PROMOTE)
                .withMemberAction(MemberAction.PROMOTE)
                .withPrice(EconConfig.fPromoteCost)
                .build()
    }

    override fun execute(info: CommandInfo): Boolean {
        val fPlayer = info.fPlayer!!
        val target = info.getArgAsFPlayer(0, offline = true) ?: return false

        if (target.getFaction() != info.faction) {
            info.message(Message.genericNotInYourFaction)
            return false
        }

        val factionRoles = info.faction!!.factionRoles
        if (!factionRoles.hasHigherRank(fPlayer, target)) {
            info.message(Message.commandPromoteCannotPromote)
            return false
        }

        val roleToBePromotedTo = factionRoles.getRoleFromString(info.args.getOrNull(1))
                ?: factionRoles.getPromotionRole(target)

        // We checked above if the target is of a higher rank.
        // So we just need to check and make sure the target isn't promoting them to a rank higher than their own
        if (!fPlayer.isLeader() && factionRoles.isHigherRole(roleToBePromotedTo, fPlayer.role)) {
            info.message(Message.commandPromoteCannotPromoteHigherRole)
            return false
        }

        // alts cannot be promoted so let's make sure to send a message and return
        // if that were to be the case
        if (target.alt) {
            info.message(Message.commandPromoteIsAlt)
            return false
        }

        if (roleToBePromotedTo == factionRoles.getApexRole()
                && !fPlayer.confirmAction.hasConfirmedAction(ConfirmAction.PROMOTE)) {
            info.message(Message.commandPromoteTransferOwnership, target.name, Config.confirmTimeOutSeconds.toString())

            val confirmationEvent = ConfirmationEvent(info.faction!!, fPlayer, ConfirmAction.PROMOTE)
            Bukkit.getPluginManager().callEvent(confirmationEvent)

            fPlayer.startConfirmProcess(FactionsX.instance, ConfirmAction.PROMOTE)
            return false
        }

        if (!fPlayer.isLeader() && roleToBePromotedTo == fPlayer.role) {
            info.message(Message.commandPromoteCouldNotBePromoted)
            return false
        }

        val previousRole = target.role
        target
                .apply { role = roleToBePromotedTo }
                .takeIf { it.role == factionRoles.getApexRole() }
                ?.let {
                    FactionsX.instance.logger.info("Transferring ownership of ${info.faction?.tag} from ${info.faction?.getLeader()?.name} to ${it.name}")
                    it.getFaction().setLeader(PlayerManager.getFPlayer(it.uuid)!!)
                    it.getFaction()
                    fPlayer.role = factionRoles.getDemotionRole(fPlayer)
                }

        fPlayer.confirmAction.clearConfirmation()
        target.message(Message.commandPromoteSuccess, fPlayer.name, previousRole.roleTag, roleToBePromotedTo.roleTag)
        info.message(Message.commandPromoteDone, target.name, roleToBePromotedTo.roleTag)
        return true
    }

    override fun getHelpInfo(): String {
        return Message.commandPromoteHelp
    }
}