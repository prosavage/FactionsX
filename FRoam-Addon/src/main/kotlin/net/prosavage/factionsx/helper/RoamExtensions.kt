package net.prosavage.factionsx.helper

import net.prosavage.factionsx.core.FPlayer
import net.prosavage.factionsx.core.RoamAPI

/**
 * Enable an [FPlayer]'s Roam mode.
 * @see [RoamAPI.enable]
 */
fun FPlayer.enableRoam(): Boolean = RoamAPI.enable(this.getPlayer())

/**
 * Enable an [FPlayer]'s Roam mode with a limited time to roam.
 * @see [RoamAPI.enable]
 */
fun FPlayer.enableRoam(timeInSeconds: Int): Boolean = RoamAPI.enable(this.getPlayer(), timeInSeconds)

/**
 * Disable an [FPlayer]'s Roam mode.
 * @see [RoamAPI.disable]
 */
fun FPlayer.disableRoam(): Boolean = RoamAPI.disable(this.getPlayer())

/**
 * Get whether or not an [FPlayer] is in Roam mode.
 * @see [RoamAPI.isRoaming]
 */
fun FPlayer.isRoaming(): Boolean = RoamAPI.isRoaming(this.getPlayer())