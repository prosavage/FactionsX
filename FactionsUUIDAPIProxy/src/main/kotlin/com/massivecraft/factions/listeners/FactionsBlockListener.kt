package com.massivecraft.factions.listeners

import com.massivecraft.factions.perms.PermissibleAction
import net.prosavage.factionsx.util.getFPlayer
import org.bukkit.Location
import org.bukkit.entity.Player

class FactionsBlockListener {
    companion object {
        @JvmStatic
        fun playerCanBuildDestroyBlock(player: Player, location: Location, action: PermissibleAction, bool: Boolean): Boolean {
            println("Proxy: proxying playerCanBuildDestroyBlock call to FactionsX API.")
            return when (action) {
                PermissibleAction.BUILD -> player.getFPlayer().canBuildAt(location)
                PermissibleAction.DESTROY -> player.getFPlayer().canBreakAt(location)
                else -> false
            }
        }

    }
}