package net.prosavage.factionsx.command.factions.cmd.social.discord

import net.prosavage.factionsx.command.engine.CommandInfo
import net.prosavage.factionsx.command.engine.CommandRequirementsBuilder
import net.prosavage.factionsx.command.engine.FCommand
import net.prosavage.factionsx.core.Permission
import net.prosavage.factionsx.persist.Message
import net.prosavage.factionsx.persist.config.EconConfig
import net.prosavage.factionsx.util.MemberAction

class CmdDiscordSet : FCommand() {
    init {
        aliases.add("set")

        requiredArgs.add(Argument("discord", 0, StringArgument()))

        commandRequirements = CommandRequirementsBuilder()
                .withMemberAction(MemberAction.DISCORD_SET)
                .withPermission(Permission.DISCORD_SET)
                .withPrice(EconConfig.fDiscordSetCost)
                .build()
    }

    override fun execute(info: CommandInfo): Boolean {
        info.fPlayer!!.getFaction().discord = info.args[0]
        info.message(Message.commandDiscordSet)
        return true
    }

    override fun getHelpInfo(): String {
        return Message.commandDiscordSetHelp
    }
}