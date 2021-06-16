package net.prosavage.factionsx.command.factions.cmd

import net.prosavage.factionsx.command.engine.CommandInfo
import net.prosavage.factionsx.command.engine.CommandRequirementsBuilder
import net.prosavage.factionsx.command.engine.FCommand
import net.prosavage.factionsx.core.Permission
import net.prosavage.factionsx.manager.PlaceholderManager
import net.prosavage.factionsx.persist.Message
import net.prosavage.factionsx.persist.config.Config

class CmdCoords : FCommand() {
    init {
        this.aliases.add("coords")

        this.commandRequirements = CommandRequirementsBuilder()
                .asFactionMember(true)
                .withPermission(Permission.COORDS)
                .build()
    }

    override fun execute(info: CommandInfo): Boolean {
        val fPlayer = info.fPlayer!!
        val faction = info.faction!!

        val message = PlaceholderManager.processPlaceholders(
                fPlayer, faction, Config.chatChannelFactionFormat
                .replace("{message}", Message.commandCoordsFormat)
        )

        faction.message(message)
        return true
    }

    override fun getHelpInfo() = Message.commandCoordsHelp
}