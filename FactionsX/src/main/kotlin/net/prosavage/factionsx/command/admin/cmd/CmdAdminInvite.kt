package net.prosavage.factionsx.command.admin.cmd

import me.rayzr522.jsonmessage.JSONMessage
import net.prosavage.factionsx.command.engine.CommandInfo
import net.prosavage.factionsx.command.engine.CommandRequirementsBuilder
import net.prosavage.factionsx.command.engine.FCommand
import net.prosavage.factionsx.core.Faction
import net.prosavage.factionsx.core.Permission
import net.prosavage.factionsx.event.FPlayerFactionJoinEvent
import net.prosavage.factionsx.persist.Message
import net.prosavage.factionsx.persist.color
import org.bukkit.Bukkit

class CmdAdminInvite : FCommand() {
    init {
        aliases.add("invite")

        requiredArgs.add(Argument("target-player", 0, PlayerArgument()))
        requiredArgs.add(Argument("faction", 1, FactionPlayerArgument()))

        commandRequirements = CommandRequirementsBuilder()
            .withPermission(Permission.ADMIN_INVITE)
            .build()
    }

    override fun execute(info: CommandInfo): Boolean {
        val fPlayer = info.fPlayer ?: return false

        val targetPlayer = info.getArgAsFPlayer(0, false, informIfNot = true) ?: return false


        val playerFactionTarget = info.getArgAsFPlayer(1, true, informIfNot = false)
        val faction: Faction = if (playerFactionTarget != null && playerFactionTarget.hasFaction()) playerFactionTarget.getFaction() else info.getArgAsFaction(1, cannotReferenceYourSelf = false)
            ?: return false

        if (faction.isSystemFaction()) {
            info.message(Message.commandAdminInviteSystemFaction)
            return false
        }

        if (targetPlayer.hasFaction()) {
            info.message(Message.commandAdminInviteAlreadyHasFaction)
            return false
        }

        faction.inviteMember(targetPlayer)

        info.message(Message.commandAdminInviteSent, faction.tag)

        faction.addMember(info.fPlayer!!, faction.factionRoles.getMinimumRole(), asAdmin = true, silently = fPlayer.isVanished())
        JSONMessage.create(color(String.format(Message.commandInviteSentNotify, faction.tag, info.player?.name)))
            .tooltip(color(String.format(Message.commandInviteSentNotifyTooltip, faction.tag)))
            .suggestCommand("/f join ${faction.tag}")
            .send(targetPlayer.getPlayer() ?: return false)

        return true
    }

    override fun getHelpInfo(): String {
        return Message.commandAdminInviteHelp
    }
}