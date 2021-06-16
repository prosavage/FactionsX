package net.prosavage.factionsx.hook.combatlog

import com.SirBlobman.combatlogx.api.ICombatLogX
import net.prosavage.factionsx.core.FPlayer

/**
 * 'CombatLogX' implementation of our Combat Tag Hook.
 */
internal class CombatLogX constructor(
    initial: ICombatLogX
) : CombatLogHook<ICombatLogX>("CombatLogX", initial) {
    override fun isTagged(fPlayer: FPlayer): Boolean = if (fPlayer.isOffline()) false else {
        instance?.combatManager?.isInCombat(fPlayer.getPlayer()) == true
    }
}