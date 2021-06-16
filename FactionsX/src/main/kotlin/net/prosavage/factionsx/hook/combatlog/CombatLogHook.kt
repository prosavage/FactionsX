package net.prosavage.factionsx.hook.combatlog

import com.SirBlobman.combatlogx.api.ICombatLogX
import net.prosavage.factionsx.core.FPlayer
import org.bukkit.Bukkit

/**
 * This abstraction layer handles the base of every combat log implementation.
 *
 * @param name     [String] the name of our corresponding implementation (plugin).
 * @param instance [Instance] the instance to MOST LIKELY be used for handling [CombatLogHook.isTagged].
 */
abstract class CombatLogHook<Instance> constructor(
    val name: String,
    protected val instance: Instance?
) {
    /**
     * Static stuff.
     */
    companion object {
        /**
         * [CombatLogHook] the one and only instance of our hooked Combat Tag plugin, if any.
         */
        lateinit var instance: CombatLogHook<*>

        /**
         * [Boolean] if the CombatLogHook instance has been initialized -
         * if false, there was no matching implementation found.
         */
        val isHooked: Boolean
            get() = this::instance.isInitialized

        /**
         * Attempt to load a matching Combat Tag implementation by present plugins.
         *
         * @return [CombatLogHook] any matching implementation, otherwise null.
         */
        internal fun attemptLoad(): CombatLogHook<*>? {
            val combatLogX = Bukkit.getPluginManager().getPlugin("CombatLogX")
            return if (combatLogX != null) CombatLogX(combatLogX as ICombatLogX) else null
        }
    }

    /**
     * Check whether or not a faction player is tagged.
     *
     * @param fPlayer [FPlayer] the faction player to check.
     * @return [Boolean] true if tagged, otherwise false.
     */
    abstract fun isTagged(fPlayer: FPlayer): Boolean
}