package net.prosavage.factionsx.command.factions.cmd.social

import net.prosavage.factionsx.command.engine.CommandInfo
import net.prosavage.factionsx.command.engine.CommandRequirementsBuilder
import net.prosavage.factionsx.command.engine.FCommand
import net.prosavage.factionsx.command.factions.cmd.social.discord.CmdDiscordSet
import net.prosavage.factionsx.command.factions.cmd.social.discord.CmdDiscordView
import net.prosavage.factionsx.core.Permission
import net.prosavage.factionsx.persist.Message

class CmdDiscord : FCommand() {
    init {
        aliases.add("discord")

        commandRequirements = CommandRequirementsBuilder()
                .asPlayer(true)
                .withPermission(Permission.DISCORD_HELP)
                .build()

        this.addSubCommand(CmdDiscordSet())
        this.addSubCommand(CmdDiscordView())
    }

    override fun execute(info: CommandInfo): Boolean {
        generateHelp(1, info.commandSender, info.args)
        return true
    }

    override fun getHelpInfo(): String {
        return Message.commandDiscordHelp
    }
}