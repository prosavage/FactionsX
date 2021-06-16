package net.prosavage.factionsx.command.factions.cmd

import me.rayzr522.jsonmessage.JSONMessage
import net.prosavage.factionsx.command.engine.CommandInfo
import net.prosavage.factionsx.command.engine.CommandRequirementsBuilder
import net.prosavage.factionsx.command.engine.FCommand
import net.prosavage.factionsx.core.Permission
import net.prosavage.factionsx.manager.FactionManager
import net.prosavage.factionsx.persist.Message
import net.prosavage.factionsx.persist.color

class CmdInvites : FCommand() {
    init {
        aliases.add("invites")

        commandRequirements = CommandRequirementsBuilder()
                .withPermission(Permission.INVITES)
                .asPlayer(true)
                .build()
    }

    override fun execute(info: CommandInfo): Boolean {
        info.message(Message.commandInvitesInvitedTo)
        info.fPlayer?.factionsInvitedTo?.keys?.forEach { id ->
            val faction = FactionManager.getFaction(id)
            JSONMessage.create(color(String.format(Message.commandInvitesSentNotify, faction.tag)))
                    .tooltip(color(String.format(Message.commandInvitesSentNotifyTooltip, faction.tag)))
                    .suggestCommand("/f join ${faction.tag}")
                    .send(info.player)
        }
        return true
    }

    override fun getHelpInfo(): String {
        return Message.commandInvitesHelp
    }
}