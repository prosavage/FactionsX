package net.prosavage.factionsx.core

import club.rarlab.classicplugin.nms.entity.FakePlayer
import org.bukkit.Location
import org.bukkit.entity.Player

/**
 * Data class to hold information about a [Player]'s Roam status.
 *  |- player - whom's RoamPlayer object this belongs to.
 *  |- startLocation - where the [Player] started off / enabled Roam from.
 *  |- npc - the [Player]'s shadowed entity.
 */
data class RoamPlayer(val player: Player, val startLocation: Location, val npc: FakePlayer)