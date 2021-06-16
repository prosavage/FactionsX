package net.prosavage.factionsx

import me.clip.placeholderapi.expansion.PlaceholderExpansion
import net.prosavage.factionsx.manager.PlayerManager
import net.prosavage.factionsx.persist.PrinterConfig
import org.bukkit.entity.Player

class PrinterPlaceholderAPI : PlaceholderExpansion() {
    override fun getIdentifier(): String {
        return "printeraddon"
    }

    override fun canRegister(): Boolean {
        return true
    }

    override fun persist(): Boolean {
        return true
    }

    override fun getAuthor(): String {
        return "ProSavage"
    }

    override fun getVersion(): String {
        return "1.0.9-RC"
    }

    override fun onPlaceholderRequest(player: Player, string: String): String? {
        val fPlayer = PlayerManager.getFPlayer(player)
        return when (string) {
            // Player
            "printer_mode" -> if (fPlayer.isInPrinter()) PrinterConfig.printerEnabledPlaceholder else PrinterConfig.printerDisabledPlaceholder
            else -> null
        }
    }
}