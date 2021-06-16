package net.prosavage.factionsx.command.factions.cmd.warp

import me.rayzr522.jsonmessage.JSONMessage
import net.prosavage.factionsx.command.engine.CommandInfo
import net.prosavage.factionsx.command.engine.CommandRequirementsBuilder
import net.prosavage.factionsx.persist.Message
import net.prosavage.factionsx.util.MemberAction
import net.prosavage.factionsx.util.color

class CmdWarpList : net.prosavage.factionsx.command.engine.FCommand() {
    init {
        aliases.add("list")

        commandRequirements = CommandRequirementsBuilder()
                .asFactionMember(true)
                .build()
    }

    override fun execute(info: CommandInfo): Boolean {
        val allWarps = info.faction!!.getAllWarps()
        if (allWarps.isNullOrEmpty()) {
            info.message(Message.commandWarpsListNone)
            return false
        }
        info.message(Message.commandWarpsListHeader)
        allWarps.forEachIndexed { index, warp ->
            JSONMessage.create(color(String.format(Message.commandWarpsListFormat, index + 1, warp.name))).tooltip(color(
                    if (info.fPlayer!!.canDoMemberAction(MemberAction.VIEW_WARP_PASSWORD)) (String.format(Message.commandWarpsListTooltip, warp.password
                            ?: Message.commandWarpsNoPasswordToolTipText))
                    else Message.commandWarpsListCantViewPasswordTooltip))
                    .send(info.player)
        }
        return true
    }

    override fun getHelpInfo(): String {
        return Message.commandWarpsListHelp
    }
}