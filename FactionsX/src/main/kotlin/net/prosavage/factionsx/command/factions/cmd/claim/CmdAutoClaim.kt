package net.prosavage.factionsx.command.factions.cmd.claim

import net.prosavage.factionsx.command.engine.CommandInfo
import net.prosavage.factionsx.command.engine.CommandRequirementsBuilder
import net.prosavage.factionsx.command.engine.FCommand
import net.prosavage.factionsx.core.Permission
import net.prosavage.factionsx.persist.Message

class CmdAutoClaim : FCommand() {
    init {
        aliases.add("autoclaim")

        commandRequirements = CommandRequirementsBuilder()
                .asFactionMember(true)
                .withPermission(Permission.AUTOCLAIM)
                .build()
    }

    override fun execute(info: CommandInfo): Boolean {
        val player = info.fPlayer ?: return false
        player.isAutoClaiming = !player.isAutoClaiming
        info.message(Message.commandAutoClaimToggle, if (player.isAutoClaiming) Message.commandAutoClaimToggleOn else Message.commandAutoClaimToggleOff)
        return true
    }

    override fun getHelpInfo(): String {
        return Message.commandAutoClaimHelp
    }
}