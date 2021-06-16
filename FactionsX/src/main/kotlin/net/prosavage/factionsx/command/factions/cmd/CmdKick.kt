package net.prosavage.factionsx.command.factions.cmd

import net.prosavage.factionsx.command.engine.CommandInfo
import net.prosavage.factionsx.command.engine.CommandRequirementsBuilder
import net.prosavage.factionsx.command.engine.FCommand
import net.prosavage.factionsx.core.Permission
import net.prosavage.factionsx.persist.Message
import net.prosavage.factionsx.persist.config.EconConfig
import net.prosavage.factionsx.util.MemberAction

class CmdKick : FCommand() {
    init {
        aliases.add("kick")

        requiredArgs.add(Argument("player-to-kick", 0, FactionMemberArgument()))

        commandRequirements = CommandRequirementsBuilder()
                .withPermission(Permission.KICK)
                .withMemberAction(MemberAction.KICK)
                .withPrice(EconConfig.fKickCost)
                .build()
    }

    override fun execute(info: CommandInfo): Boolean {
        val target = info.getArgAsFPlayer(0, offline = true) ?: return false
        if (target.getFaction() != info.faction) {
            info.message(Message.commandKickNotSameFaction)
            return false
        }

        val myRole = info.fPlayer!!.role
        if (info.faction!!.factionRoles.getApexRole() != myRole && info.faction!!.factionRoles.isHigherRole(target.role, myRole)) {
            info.message(Message.commandKickRoleNotHighEnough)
            return false
        }

        if (target.alt) {
            info.message(Message.commandKickIsAltSuggestion)
            return false
        }

        target.getFaction().removeMember(target)
        info.message(Message.commandKickSuccess, target.name)
        target.message(Message.commandKickTargetInform, info.faction!!.tag, info.player!!.name)
        return true
    }

    override fun getHelpInfo(): String {
        return Message.commandKickHelp
    }
}