package net.prosavage.factionsx.command.factions.cmd

import net.prosavage.factionsx.command.engine.CommandInfo
import net.prosavage.factionsx.command.engine.CommandRequirementsBuilder
import net.prosavage.factionsx.command.engine.FCommand
import net.prosavage.factionsx.core.ChatChannel
import net.prosavage.factionsx.core.Permission
import net.prosavage.factionsx.persist.Message
import net.prosavage.factionsx.util.enumValueOrNull

class CmdIgnore : FCommand() {
    init {
        aliases.add("ignore")

        requiredArgs.add(Argument("channel", 0, IgnoreChannelsArgument()))

        commandRequirements = CommandRequirementsBuilder()
                .asFactionMember(true)
                .withPermission(Permission.IGNORE)
                .build()
    }

    override fun execute(info: CommandInfo): Boolean {
        val fPlayer = info.fPlayer ?: return false
        val channel = enumValueOrNull<ChatChannel>(info.args[0].toUpperCase())
                ?: run {
                    info.message(Message.commandIgnoreInvalidChannel)
                    return false
                }

        if (channel == ChatChannel.PUBLIC) {
            info.message(Message.commandIgnoreNotPublic)
            return false
        }

        if (fPlayer.isIgnoringChannel(channel)) {
            fPlayer.unignoreChannel(channel)
            info.message(Message.commandIgnoreNotIgnoring, channel.channelName)
            return false
        }

        fPlayer.ignoreChannel(channel)
        info.message(Message.commandIgnoreIgnored, channel.channelName)
        return true
    }

    override fun getHelpInfo(): String {
        return Message.commandIgnoreHelp
    }
}