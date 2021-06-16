package net.prosavage.factionsx.command.factions.cmd

import net.prosavage.factionsx.command.engine.CommandInfo
import net.prosavage.factionsx.command.engine.CommandRequirementsBuilder
import net.prosavage.factionsx.command.engine.FCommand
import net.prosavage.factionsx.persist.Message
import net.prosavage.factionsx.persist.config.EconConfig
import net.prosavage.factionsx.util.MemberAction

class CmdDesc : FCommand() {
    init {
        aliases.add("desc")
        aliases.add("description")
        aliases.add("change-description")

        optionalArgs.add(Argument("new-description", 0, StringArgument()))

        bypassArgumentCount = true

        commandRequirements = CommandRequirementsBuilder()
                .withMemberAction(MemberAction.CHANGE_DESCRIPTION)
                .withPrice(EconConfig.fDescPrice)
                .build()
    }

    override fun execute(info: CommandInfo): Boolean {
        val faction = info.faction ?: return false
        val fPlayer = info.fPlayer ?: return false
        val description = info.args.joinToString(" ")

        faction.description = description
        info.message(Message.commandDescSuccess)
        faction.message(Message.commandDescAnnounce, fPlayer.name, excludeFPlayers = listOf(fPlayer))
        return true
    }

    override fun getHelpInfo(): String {
        return Message.commandDescHelp
    }
}