package net.prosavage.factionsx.command.factions.cmd

import net.prosavage.factionsx.command.engine.CommandInfo
import net.prosavage.factionsx.command.engine.CommandRequirementsBuilder
import net.prosavage.factionsx.command.engine.FCommand
import net.prosavage.factionsx.persist.Message

class CmdSeeChunk : FCommand() {
    init {
        aliases.add("seechunk")
        aliases.add("sc")

        commandRequirements = CommandRequirementsBuilder()
                .asPlayer(true)
                .build()
    }

    override fun execute(info: CommandInfo): Boolean {
        val fPlayer = info.fPlayer!!
        fPlayer.showChunkBorders = !fPlayer.showChunkBorders
        info.message(Message.commandSeeChunkToggle, if (fPlayer.showChunkBorders) Message.commandSeeChunkEnabled else Message.commandSeeChunkDisabled)
        return true
    }

    override fun getHelpInfo(): String {
        return Message.commandSeeChunkHelp
    }
}