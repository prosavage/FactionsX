package net.prosavage.factionsx.command.factions.cmd

import net.prosavage.factionsx.command.engine.CommandInfo
import net.prosavage.factionsx.command.engine.CommandRequirementsBuilder
import net.prosavage.factionsx.command.engine.FCommand
import net.prosavage.factionsx.persist.Message

class CmdChunkMessage : FCommand() {
    init {
        aliases += "chunkmessage"
        aliases += "chunkmsg"

        commandRequirements = CommandRequirementsBuilder()
                .asPlayer(true)
                .build()
    }

    override fun execute(info: CommandInfo): Boolean {
        val fPlayer = info.fPlayer ?: return false
        fPlayer.enabledChunkMessage = !fPlayer.enabledChunkMessage

        fPlayer.message(Message.commandChunkMessage, if (fPlayer.enabledChunkMessage) {
            Message.commandChunkMessageEnabled
        } else {
            Message.commandChunkMessageDisabled
        })
        return true
    }

    override fun getHelpInfo(): String = Message.commandChunkMessageHelp
}