package net.prosavage.factionsx.command.factions.cmd

import net.prosavage.factionsx.command.engine.CommandInfo
import net.prosavage.factionsx.command.engine.CommandRequirementsBuilder
import net.prosavage.factionsx.command.engine.FCommand
import net.prosavage.factionsx.core.ChatChannel
import net.prosavage.factionsx.core.Permission
import net.prosavage.factionsx.persist.Message
import net.prosavage.factionsx.util.enumValueOrNull
import java.util.stream.Collectors

class CmdChat : FCommand() {
    init {
        aliases.add("chat")
        aliases.add("c")

        optionalArgs.add(Argument("channel", 0, ChatChannelArgument()))
        optionalArgs.add(Argument("message", 1, StringArgument()))

        bypassArgumentCount = true
        commandRequirements = CommandRequirementsBuilder()
                .asFactionMember(true)
                .withPermission(Permission.CHAT)
                .build()
    }

    override fun execute(info: CommandInfo): Boolean {
        if (info.args.isEmpty()) {
            // this is for channel cycling.
            val fplayer = info.fPlayer!!
            val cycleChannel = when (fplayer.chatChannel) {
                ChatChannel.ALLY -> ChatChannel.FACTION
                ChatChannel.FACTION -> ChatChannel.PUBLIC
                ChatChannel.PUBLIC -> ChatChannel.TRUCE
                ChatChannel.TRUCE -> ChatChannel.ALLY
            }
            fplayer.chatChannel = cycleChannel
            info.message(Message.commandChatChannelChange, cycleChannel.channelName)
            return false
        }
        val channel = processChatChannel(info.args[0]) ?: run {
            info.message(Message.commandChatInvalidChannel)
            return false
        }
        if (info.args.size > 1) {
            val oldChannel = info.fPlayer!!.chatChannel
            val messageArgs = info.args
            messageArgs.removeAt(0)
            val message = messageArgs.stream().collect(Collectors.joining(" "))
            info.fPlayer!!.chatChannel = channel
            info.player!!.chat(message)
            info.fPlayer!!.chatChannel = oldChannel
            return false
        }
        info.fPlayer!!.chatChannel = channel
        info.message(Message.commandChatChannelChange, channel.channelName)
        return true
    }

    private fun processChatChannel(name: String): ChatChannel? {
        val normalizedChatChannelName = normalizeChatChannelName(name.toUpperCase())
        return enumValueOrNull(normalizedChatChannelName)
    }

    private fun normalizeChatChannelName(name: String): String {
        if (name.length == 1) {
            ChatChannel.values().forEach { channel -> if (name.first() == channel.shortHand) return channel.name }
        }
        return name
    }

    override fun getHelpInfo(): String {
        return Message.commandChatHelp
    }
}