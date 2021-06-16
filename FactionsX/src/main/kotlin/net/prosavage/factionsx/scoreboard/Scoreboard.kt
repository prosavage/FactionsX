package net.prosavage.factionsx.scoreboard

import net.prosavage.factionsx.FactionsX
import net.prosavage.factionsx.core.FPlayer
import net.prosavage.factionsx.manager.PlayerManager
import net.prosavage.factionsx.persist.config.Config.scoreboardOptions
import net.prosavage.factionsx.scoreboard.ScoreboardType.*
import net.prosavage.factionsx.scoreboard.ScoreboardType.Companion.byRelation
import net.prosavage.factionsx.util.Relation
import org.bukkit.Bukkit
import org.bukkit.entity.Player

/**
 * Interface to handle Scoreboard implementations.
 */
interface Scoreboard {
    /**
     * Name of the implementation.
     */
    val type: String

    /**
     * Show a [Scoreboard] to a [Player].
     */
    fun show(fPlayer: FPlayer)

    /**
     * Hide a [Player]'s Scoreboard.
     */
    fun hide(fPlayer: FPlayer)
}

/**
 * Enumeration to hold all [Scoreboard] types.
 */
enum class ScoreboardType {
    FACTIONLESS, DEFAULT, SYSTEM, OWN,
    ALLY, ENEMY, TRUCE, NEUTRAL;

    /**
     * Get the name of the [Scoreboard] type.
     */
    override fun toString(): String = this.name.toLowerCase()

    /**
     * Static stuff.
     */
    companion object {
        /**
         * Get the [ScoreboardType] correspondent to a [Relation].
         */
        fun byRelation(relation: Relation) = values().find { it.name == relation.name }
    }
}

/**
 * Start monitoring the [Scoreboard].
 */
internal fun startScoreboardMonitor(scoreboard: Scoreboard?) {
    Bukkit.getScheduler().runTaskTimerAsynchronously(FactionsX.instance, Runnable {
        if (!scoreboardOptions.enabled || !scoreboardOptions.internal || scoreboard == null) return@Runnable
        PlayerManager.getOnlineFPlayers().filter(FPlayer::scoreboardActive).forEach(scoreboard::show)
    }, 20, 5)
}

/**
 * Get the [ScoreboardType] at the location of an [FPlayer].
 */
internal fun FPlayer.boardTypeAt(): ScoreboardType {
    if (!this.hasFaction()) {
        return FACTIONLESS
    }

    return if (!scoreboardOptions.perTerritory) DEFAULT else {
        val factionAt = this.getFactionAt()
        when {
            factionAt.isSystemFaction() -> SYSTEM
            this.getFaction() == factionAt -> OWN
            else -> byRelation(getFaction().getRelationTo(getFactionAt())) ?: DEFAULT
        }
    }
}