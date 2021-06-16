package net.prosavage.factionsx.command.factions.cmd

import net.prosavage.factionsx.command.engine.CommandInfo
import net.prosavage.factionsx.command.engine.CommandRequirementsBuilder
import net.prosavage.factionsx.command.engine.FCommand
import net.prosavage.factionsx.core.Permission
import net.prosavage.factionsx.persist.Message
import net.prosavage.factionsx.persist.config.EconConfig
import net.prosavage.factionsx.util.MemberAction

class CmdDemote : FCommand() {
    init {
        aliases.add("demote")

        requiredArgs.add(Argument("player-name", 0, FactionMemberArgument()))

        commandRequirements = CommandRequirementsBuilder()
                .withMemberAction(MemberAction.DEMOTE)
                .withPermission(Permission.DEMOTE)
                .withPrice(EconConfig.fDemoteCost)
                .build()
    }

    override fun execute(info: CommandInfo): Boolean {
        val target = info.getArgAsFPlayer(0, offline = true) ?: return false

        if (target.getFaction() != info.faction) {
            info.message(Message.genericNotInYourFaction)
            return false
        }

        val factionRoles = info.faction!!.factionRoles
        if (!factionRoles.hasHigherRank(info.fPlayer!!, target)) {
            info.message(Message.commandDemoteCannotDemote)
            return false
        }

        val roleToBeDemotedTo = factionRoles.getDemotionRole(target)

        if (roleToBeDemotedTo == target.role) {
            info.message(Message.commandDemoteCouldNotBeDemoted)
            return false
        }

        val previousRole = target.role
        target.apply { role = roleToBeDemotedTo }
        target.message(Message.commandDemoteSuccess, info.fPlayer!!.name, previousRole.roleTag, roleToBeDemotedTo.roleTag)
        info.message(Message.commandDemoteWork, target.name, roleToBeDemotedTo.roleTag)
        return true
    }

    override fun getHelpInfo(): String {
        return Message.commandDemoteHelp
    }
}