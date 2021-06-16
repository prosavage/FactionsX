package net.prosavage.factionsx.struct

import net.prosavage.factionsx.core.FPlayer
import net.prosavage.factionsx.util.SerializableItem
import org.bukkit.Bukkit

data class ShopItem(
        val displayItem: SerializableItem,
        val row: Int,
        val column: Int,
        val price: Int,
        val commandsOnBuy: List<String>
) {

    fun executeCommandsOnBuy(contextPlayer: FPlayer) {
        commandsOnBuy.forEach { command ->
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.replace("{player}", contextPlayer.name))
        }
    }


}