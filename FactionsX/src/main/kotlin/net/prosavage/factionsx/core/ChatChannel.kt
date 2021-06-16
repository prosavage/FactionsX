package net.prosavage.factionsx.core

import net.prosavage.factionsx.persist.config.Config

enum class ChatChannel(val channelName: String, val shortHand: Char) {
    TRUCE(Config.chatChannelTruce, Config.chatChannelTruceShortHand),
    ALLY(Config.chatChannelAlly, Config.chatChannelAllyShortHand),
    FACTION(Config.chatChannelFaction, Config.chatChannelFactionShortHand),
    PUBLIC(Config.chatChannelPublic, Config.chatChannelPublicShortHand)
}