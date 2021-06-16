package net.prosavage.factionsx.packet

import club.rarlab.classicplugin.packet.PacketHandler
import club.rarlab.classicplugin.task.schedule
import io.netty.channel.ChannelHandlerContext
import net.prosavage.factionsx.core.RoamAPI
import net.prosavage.factionsx.persistence.config.Config
import net.prosavage.factionsx.persistence.config.Message
import net.prosavage.factionsx.util.getFPlayer
import org.bukkit.GameMode
import org.bukkit.entity.Player

/**
 * Packet handler for hitting fake players.
 */
class PacketReader(private val player: Player) : PacketHandler(player) {
    override fun channelRead(context: ChannelHandlerContext, msg: Any) {
        val packet = msg::class.java
        super.channelRead(context, msg)

        if (packet.simpleName == "PacketPlayInUseEntity") {
            val action = packet.getDeclaredField("action").also { it.isAccessible = true }[msg] as Enum<*>

            val roamers = RoamAPI.getRoamers().values
            if (action.name != "ATTACK" || player.gameMode == GameMode.SPECTATOR || roamers.isEmpty()) return

            val id = packet.getDeclaredField("a").also { it.isAccessible = true }[msg] as Int
            val fakePlayer = roamers.find { it.npc.getEntityId() == id } ?: return

            fakePlayer.player.let {
                schedule(async = false, then = Runnable { RoamAPI.disable(it) })
                it.getFPlayer().message(Message.roamDisabled, !Config.messageWithPrefix)
            }
        }
    }
}