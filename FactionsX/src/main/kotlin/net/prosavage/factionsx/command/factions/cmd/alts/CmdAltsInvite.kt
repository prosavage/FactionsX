package net.prosavage.factionsx.command.factions.cmd.alts

import me.rayzr522.jsonmessage.JSONMessage
import net.prosavage.factionsx.command.engine.CommandInfo
import net.prosavage.factionsx.command.engine.CommandRequirementsBuilder
import net.prosavage.factionsx.command.engine.FCommand
import net.prosavage.factionsx.core.Permission
import net.prosavage.factionsx.persist.Message
import net.prosavage.factionsx.persist.color
import net.prosavage.factionsx.persist.config.Config
import net.prosavage.factionsx.util.MemberAction

class CmdAltsInvite : FCommand() {
    init {
        aliases += "invite"
        aliases += "inv"

        requiredArgs += Argument("player", 0, PlayerArgument())

        commandRequirements = CommandRequirementsBuilder()
                .asFactionMember(true)
                .withPermission(Permission.ALTS_INVITE)
                .withMemberAction(MemberAction.ALTS_INVITE)
                .build()
    }

    override fun execute(info: CommandInfo): Boolean {
        val faction = info.faction ?: return false

        if (Config.factionAltLimit <= faction.getAlts().size) {
            info.message(Message.commandAltsInviteLimitReached)
            return false
        }

        val invited = info.getArgAsFPlayer(0) ?: return false
        if (invited in faction.getMembers() || invited in faction.getAlts()) {
            info.message(Message.commandInviteAlreadyPartOfYourFaction)
            return false
        }

        if (invited.hasFaction()) {
            info.message(Message.commandInviteAlreadyPartOfFaction, invited.name)
            return false
        }

        faction.inviteMember(invited, true)
        info.message(Message.commandAltsInvited, invited.name)

        JSONMessage
                .create(color(Message.commandAltsInvitedNotify.format(faction.tag, info.player?.name)))
                .tooltip(color(Message.commandAltsInvitedNotifyTooltip.format(faction.tag)))
                .suggestCommand("/f alts join ${faction.tag}")
                .send(invited.getPlayer() ?: return false)
        return true
    }

    override fun getHelpInfo(): String = Message.commandAltsInviteHelp
}