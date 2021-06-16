package net.prosavage.factionsx.command.factions.cmd.alts

import net.prosavage.factionsx.command.engine.CommandInfo
import net.prosavage.factionsx.command.engine.CommandRequirementsBuilder
import net.prosavage.factionsx.command.engine.FCommand
import net.prosavage.factionsx.core.Permission
import net.prosavage.factionsx.persist.Message
import net.prosavage.factionsx.util.MemberAction

class CmdAltsClose : FCommand() {
    init {
        aliases += "close"

        commandRequirements = CommandRequirementsBuilder()
                .asFactionMember(true)
                .withPermission(Permission.ALTS_CLOSE)
                .withMemberAction(MemberAction.ALTS_CLOSE)
                .build()
    }

    override fun execute(info: CommandInfo): Boolean {
        val faction = info.faction ?: return false

        if (!faction.altsOpen) {
            info.message(Message.commandAltsCloseAlready)
            return false
        }

        faction.altsOpen = false
        info.message(Message.commandAltsCloseSuccess)
        faction.message(
                Message.commandAltsCloseSuccessAll,
                info.player?.name ?: "Unknown",
                excludeFPlayers = listOf(info.fPlayer)
        )

        return true
    }

    override fun getHelpInfo(): String = Message.commandAltsCloseHelp
}