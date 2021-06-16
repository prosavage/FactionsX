package net.prosavage.factionsx.scoreboard.implementations

import me.clip.placeholderapi.PlaceholderAPI.setPlaceholders
import net.prosavage.factionsx.core.FPlayer
import net.prosavage.factionsx.manager.PlaceholderManager.isPlaceholderApi
import net.prosavage.factionsx.persist.config.ScoreboardConfig.scoreboards
import net.prosavage.factionsx.scoreboard.Scoreboard
import net.prosavage.factionsx.scoreboard.boardTypeAt
import net.prosavage.factionsx.util.multiColor
import org.bukkit.entity.Player

/**
 * Class to handle the [Scoreboard] implementation of internal.
 */
class InternalBoard : Scoreboard {
    override val type: String = "Internal"

    override fun show(fPlayer: FPlayer) {
        fPlayer.scoreboardActive = true
        val player: Player = fPlayer.getPlayer() ?: return

        fPlayer.internalBoard?.run {
            val data = scoreboards[fPlayer.boardTypeAt()] ?: return@run
            this.updateTitle(multiColor(if (isPlaceholderApi) setPlaceholders(player, data.title) else data.title))
            this.updateLines(data.lines.map(::multiColor).mapIf(isPlaceholderApi) { setPlaceholders(player, it) })
        }
    }

    override fun hide(fPlayer: FPlayer) {
        fPlayer.scoreboardActive = false
        fPlayer.internalBoard?.delete()
    }

    /**
     * Map a [List] if predicate was met.
     */
    private fun List<String>.mapIf(predicate: Boolean, mapping: (String) -> String): List<String> {
        return if (predicate) this.map(mapping) else this
    }
}