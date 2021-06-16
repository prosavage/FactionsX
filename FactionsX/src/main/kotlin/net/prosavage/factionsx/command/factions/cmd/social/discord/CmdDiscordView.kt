package net.prosavage.factionsx.command.factions.cmd.social.discord

import net.prosavage.factionsx.command.engine.CommandInfo
import net.prosavage.factionsx.command.engine.CommandRequirementsBuilder
import net.prosavage.factionsx.command.engine.FCommand
import net.prosavage.factionsx.core.Faction
import net.prosavage.factionsx.core.Permission
import net.prosavage.factionsx.persist.Message

class CmdDiscordView : FCommand() {
    init {
        aliases.add("view")

        optionalArgs.add(Argument("faction-name", 0, FactionArgument()))

        commandRequirements = CommandRequirementsBuilder()
                .asPlayer(true)
                .withPermission(Permission.DISCORD_VIEW)
                .build()
    }

    override fun execute(info: CommandInfo): Boolean {
        if (info.args.isEmpty()) info.message(Message.commandDiscordViewYourOwn, info.fPlayer!!.getFaction().discord)
        if (info.args.isNotEmpty()) {
            val faction = info.getArgAsFaction(0) ?: return false
            checkDiscord(faction, info)
        }
        return true
    }

    override fun getHelpInfo(): String {
        return Message.commandDiscordViewHelp
    }

    private fun checkDiscord(faction: Faction?, info: CommandInfo) {
        info.message(Message.commandDiscordView, faction!!.tag, faction.discord)
    }
}