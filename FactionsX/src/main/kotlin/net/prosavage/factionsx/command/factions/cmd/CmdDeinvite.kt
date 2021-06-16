package net.prosavage.factionsx.command.factions.cmd

import net.prosavage.factionsx.command.engine.CommandInfo
import net.prosavage.factionsx.command.engine.CommandRequirementsBuilder
import net.prosavage.factionsx.core.Permission
import net.prosavage.factionsx.persist.Message
import net.prosavage.factionsx.persist.config.EconConfig
import net.prosavage.factionsx.util.MemberAction

class CmdDeinvite : net.prosavage.factionsx.command.engine.FCommand() {
    init {
        aliases.add("deinvite")

        requiredArgs.add(Argument("target", 0, PlayerArgument()))

        commandRequirements = CommandRequirementsBuilder()
                .withPermission(Permission.DEINVITE)
                .withMemberAction(MemberAction.DEINVITE)
                .withPrice(EconConfig.fDeinviteCost)
                .build()
    }

    override fun execute(info: CommandInfo): Boolean {
        val target = info.getArgAsFPlayer(0, offline = true) ?: return false

        if (!target.isInvitedToFaction(info.faction!!)) {
            info.message(Message.commandDeinviteNotInvited)
            return false
        }

        info.faction!!.deinviteMember(target)
        info.message(Message.commandDeinviteSuccess, target.name)
        target.message(Message.commandDeinviteRecipient, info.fPlayer!!.name, info.faction!!.tag)
        return true
    }

    override fun getHelpInfo(): String {
        return Message.commandDeinviteHelp
    }
}