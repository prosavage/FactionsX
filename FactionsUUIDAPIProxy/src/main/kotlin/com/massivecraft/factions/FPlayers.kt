package com.massivecraft.factions

import com.massivecraft.factions.proxy.ProxyFPlayer
import net.prosavage.factionsx.manager.PlayerManager
import net.prosavage.factionsx.persist.data.Players
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player
import java.util.*

class FPlayers {
    companion object {
        private lateinit var instance: FPlayers

        fun setInstance(instance: FPlayers) {
            this.instance = instance
        }

        @JvmStatic
        fun getInstance(): FPlayers {
            return instance
        }
    }

    fun getOnlinePlayers(): Collection<FPlayer> {
        return PlayerManager.getOnlineFPlayers().map { ProxyFPlayer(it) }
    }

    fun getByPlayer(player: Player): FPlayer {
        return ProxyFPlayer(PlayerManager.getFPlayer(player))
    }

    fun getAllFPlayers(): Collection<FPlayer?>? {
        return Players.fplayers.values.map { ProxyFPlayer(it) }
    }

    fun getByOfflinePlayer(player: OfflinePlayer?): FPlayer? {
        if (player == null) return null
        return PlayerManager.getFPlayer(player.uniqueId)?.let { ProxyFPlayer(it) }
    }

    fun getById(string: String?): FPlayer? {
        return PlayerManager.getFPlayer(UUID.fromString(string))?.let { ProxyFPlayer(it) }
    }


}