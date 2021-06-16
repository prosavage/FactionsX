package net.prosavage.factionsx.command.factions.cmd

import net.prosavage.factionsx.command.engine.CommandInfo
import net.prosavage.factionsx.command.engine.CommandRequirementsBuilder
import net.prosavage.factionsx.command.engine.FCommand
import net.prosavage.factionsx.core.Permission
import net.prosavage.factionsx.persist.Message
import net.prosavage.factionsx.persist.data.getFLocation

class CmdHere : FCommand() {
    init {
        aliases.add("here")
        aliases.add("at")

        commandRequirements = CommandRequirementsBuilder()
                .asPlayer(true)
                .withPermission(Permission.HERE)
                .build()
    }

    override fun execute(info: CommandInfo): Boolean {
        info.message(Message.commandHereInfo, getFLocation(info.player!!.location).getFaction().tag)
        return true
    }

    override fun getHelpInfo(): String {
        return Message.commandHereHelp
    }
}