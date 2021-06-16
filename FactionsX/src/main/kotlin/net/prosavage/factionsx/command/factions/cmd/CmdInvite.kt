package net.prosavage.factionsx.command.factions.cmd

import me.rayzr522.jsonmessage.JSONMessage
import net.prosavage.factionsx.command.engine.CommandInfo
import net.prosavage.factionsx.command.engine.CommandRequirementsBuilder
import net.prosavage.factionsx.command.engine.FCommand
import net.prosavage.factionsx.core.Permission
import net.prosavage.factionsx.persist.Message
import net.prosavage.factionsx.persist.color
import net.prosavage.factionsx.persist.config.Config
import net.prosavage.factionsx.persist.config.EconConfig
import net.prosavage.factionsx.util.MemberAction

class CmdInvite : FCommand() {
    init {
        aliases.add("invite")
        aliases.add("inv")

        requiredArgs.add(Argument("player-name", 0, PlayerArgument()))

        commandRequirements = CommandRequirementsBuilder()
                .withPermission(Permission.INVITE)
                .withMemberAction(MemberAction.INVITE)
                .withPrice(EconConfig.fInviteCost)
                .build()
    }

    override fun execute(info: CommandInfo): Boolean {
        val faction = info.faction!!

        if ((Config.factionMemberLimit + faction.memberBoost) <= faction.getMembers().size) {
            info.message(Message.commandInviteFactionMemberLimitReached, Config.factionMemberLimit.toString() + faction.memberBoost)
            return false
        }

        val playerToInvite = info.getArgAsFPlayer(0, offline = true) ?: return false
        if (faction.getMembers().contains(playerToInvite)) {
            info.message(Message.commandInviteAlreadyPartOfYourFaction)
            return false
        }

        if (playerToInvite.hasFaction()) {
            info.message(Message.commandInviteAlreadyPartOfFaction, playerToInvite.name)
            return false
        }

        faction.inviteMember(playerToInvite)
        info.message(Message.commandInviteSent, playerToInvite.name)
        JSONMessage.create(color(String.format(Message.commandInviteSentNotify, faction.tag, info.player?.name)))
                .tooltip(color(String.format(Message.commandInviteSentNotifyTooltip, faction.tag)))
                .suggestCommand("/f join ${faction.tag}")
                .send(playerToInvite.getPlayer() ?: return false)
        return true
    }

    override fun getHelpInfo(): String {
        return Message.commandInviteHelp
    }
}