package net.prosavage.factionsx.command.factions.cmd.alts

import net.prosavage.factionsx.command.engine.CommandInfo
import net.prosavage.factionsx.command.engine.CommandRequirementsBuilder
import net.prosavage.factionsx.command.engine.FCommand
import net.prosavage.factionsx.core.Permission
import net.prosavage.factionsx.persist.Message
import net.prosavage.factionsx.util.MemberAction

class CmdAltsOpen : FCommand() {
    init {
        aliases += "open"

        commandRequirements = CommandRequirementsBuilder()
                .asFactionMember(true)
                .withPermission(Permission.ALTS_OPEN)
                .withMemberAction(MemberAction.ALTS_OPEN)
                .build()
    }

    override fun execute(info: CommandInfo): Boolean {
        val faction = info.faction ?: return false

        if (faction.altsOpen) {
            info.message(Message.commandAltsOpenAlready)
            return false
        }

        faction.altsOpen = true
        info.message(Message.commandAltsOpenSuccess)
        faction.message(
                Message.commandAltsOpenSuccessAll,
                info.player?.name ?: "Unknown",
                excludeFPlayers = listOf(info.fPlayer)
        )

        return true
    }

    override fun getHelpInfo(): String = Message.commandAltsOpenHelp
}