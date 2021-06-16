package net.prosavage.factionsx.core

import net.prosavage.factionsx.FactionRoam.Companion.STORAGE
import org.bukkit.entity.Player

/**
 * Singleton that contains all API related functions.
 */
object RoamAPI {
    /**
     * Enable a [Player]'s Roam mode.
     *
     * @param player        whom's Roam mode to enable.
     * @param timeInSeconds limited time in seconds the player is able to roam for until disabling.
     * @return [Boolean] whether or not the operation was successful.
     */
    fun enable(player: Player?, timeInSeconds: Int = -1): Boolean = STORAGE.enable(player, timeInSeconds)

    /**
     * Disable a [Player]'s Roam mode.
     *
     * @param player whom's Roam mode to disable.
     * @return [Boolean] whether or not the operation was successful.
     */
    fun disable(player: Player?): Boolean = STORAGE.disable(player)

    /**
     * Get whether or not a [Player] is in Roam mode.
     *
     * @param player whom to check.
     * @return [Boolean] whether or not the [Player] is in Roam mode.
     */
    fun isRoaming(player: Player?): Boolean = STORAGE.isRoaming(player)

    /**
     * Get a [Player]'s [RoamPlayer] object if present.
     *
     * @param player whom's [RoamPlayer] object to fetch.
     * @return [RoamPlayer] corresponding object, nullable.
     */
    fun getRoamPlayer(player: Player): RoamPlayer? = STORAGE.getRoamPlayer(player)

    /**
     * Get an immutable [Map] of players in Roam mode.
     *
     * @return [Map] of players that are roaming.
     */
    fun getRoamers(): Map<Player, RoamPlayer> = STORAGE.getRoamers()
}