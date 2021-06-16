package net.prosavage.factionsx

import me.clip.placeholderapi.expansion.PlaceholderExpansion
import net.prosavage.factionsx.FTOPAddon.Companion.getRank
import net.prosavage.factionsx.manager.FactionManager
import net.prosavage.factionsx.manager.PlaceholderManager
import net.prosavage.factionsx.manager.PlayerManager
import net.prosavage.factionsx.persist.FTOPConfig.priceFormat
import net.prosavage.factionsx.persist.FTOPConfig.unknownValuePlaceholder
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin

class PlaceholderAPIIntegration(val instance: JavaPlugin) : PlaceholderExpansion() {
    override fun getIdentifier(): String {
        return "ftopaddon"
    }

    override fun getAuthor(): String {
        return "ProSavage"
    }

    override fun getVersion(): String {
        return instance.description.version
    }

    override fun persist(): Boolean {
        return true
    }

    override fun onPlaceholderRequest(player: Player, string: String): String? {
        val fPlayer = PlayerManager.getFPlayer(player)
        val faction = fPlayer.getFaction()

        if (string.contains("::")) {
            val split = string.split("::")
            val index = split[1].toLongOrNull() ?: return null
            when (split[0]) {
                "rank" -> return FTOPAddon.factionValues[index]?.toString() ?: unknownValuePlaceholder
                "value" -> return FTOPAddon.factionValues[index]?.latestCalculatedValue?.toString() ?: unknownValuePlaceholder
                "value_formatted" -> return FTOPAddon.factionValues[index]?.latestCalculatedValue?.let { priceFormat.format(it) } ?: unknownValuePlaceholder
                "tag" -> return FTOPAddon.factionValues[index]?.factionObject?.tag ?: unknownValuePlaceholder
                "relational_tag" -> return with(FTOPAddon.factionValues[index]) {
                    if (this == null) return unknownValuePlaceholder
                    val preciseFaction = FactionManager.getFaction(this.faction)
                    PlaceholderManager.getRelationPrefix(faction, preciseFaction) + unknownValuePlaceholder
                }
            }
        }

        return when (string) {
            // Player
            "faction_rank" -> faction.getRank()?.toString() ?: unknownValuePlaceholder
            "faction_worth" -> priceFormat.format(FTOPAddon.factionValues[faction.id]?.latestCalculatedValue ?: 0)
            else -> null
        }
    }
}