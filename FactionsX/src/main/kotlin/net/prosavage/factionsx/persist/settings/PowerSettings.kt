package net.prosavage.factionsx.persist.settings

import net.prosavage.factionsx.util.PlayerAction

data class PowerSettings constructor(
        val powerRegenOffline: Boolean,
        val dtrBased: Boolean,
        val dtrBasedActions: ArrayList<PlayerAction>
) {
    val dtrBasedRaidableAt: Double = 0.0
    val maxPlayerPower: Double = 10.0
    val minPlayerPower: Double = -10.0
    val startingPower: Double = 5.0
    val powerOfflineLossPerDay: Double = 0.0
    val powerOfflineLossLimit: Double = 0.0
    val powerPerDeath: Double = 4.0
    val powerRegenPerMinute: Double = 0.2
} 