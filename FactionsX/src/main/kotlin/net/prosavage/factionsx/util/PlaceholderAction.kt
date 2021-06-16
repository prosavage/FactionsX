package net.prosavage.factionsx.util

import net.prosavage.factionsx.core.Faction
import net.prosavage.factionsx.manager.FactionManager
import net.prosavage.factionsx.persist.config.Config
import org.bukkit.Bukkit

enum class PlaceholderAction(val holder: String?, val chars: Int) {
    /** Types of actions. **/
    FACTION_MESSAGE("faction:message", 15),
    FACTION_DISBAND("faction:disband", 15),
    BROADCAST(null, 9);

    /** Utility stuff. **/
    companion object {
        fun trigger(faction: Faction?, action: String, args: Array<out String> = emptyArray()) {
            val handle = byAction(action) ?: return
            val precise = handle.second.format(args)

            when (handle.first) {
                FACTION_MESSAGE -> faction?.message(precise)
                FACTION_DISBAND -> FactionManager.deleteFaction(faction ?: return)
                BROADCAST -> Bukkit.broadcastMessage(precise)
            }
        }

        private fun byAction(action: String): Pair<PlaceholderAction, String>? {
            val type = values().find { type ->
                action.startsWith("[${type.holder ?: type.name.toLowerCase()}] ")
            } ?: return null
            return Pair(type, action.substring(type.chars + 3))
        }

        @JvmStatic
        fun ofValuePercentage(faction: Faction): Int? = Config.factionStrikeActions
                .filter { action -> faction.strikes.size >= action.key }.values
                .valuePercentageLastList()
                .valuePercentageCollect()

        private fun Collection<List<String>>.valuePercentageLastList(): List<String>? =
                lastOrNull { value -> value.any { it.matches(Patterns.VALUE_HOLDER_PERCENTAGE) } }

        private fun List<String>?.valuePercentageCollect(): Int? =
                this?.lastOrNull { it.matches(Patterns.VALUE_HOLDER_PERCENTAGE) }?.substring(16)?.toIntOrNull()
    }
}