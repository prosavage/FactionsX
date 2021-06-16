package net.prosavage.factionsx.manager

import net.prosavage.factionsx.core.FPlayer
import net.prosavage.factionsx.persist.config.Config.creditSettings
import kotlin.math.abs

object CreditManager {
    /**
     * Modify a player's credits.
     *
     * @param fPlayer [FPlayer] Whom's credits that will be modified.
     * @param amount [Double] The amount that the corresponding player's credits should be modified to.
     * @param overrideLimit [Boolean] Whether or not the credits limit should be overriden.
     *
     * @return [Boolean] If the modification was successful or interrupted by limitation.
     */
    fun modify(fPlayer: FPlayer, amount: Double, overrideLimit: Boolean = false): Boolean {
        if (!overrideLimit && amount > creditSettings.maximumCredits) return false
        fPlayer.credits = amount
        return true
    }

    /**
     * Supply a player with a specific amount of credits.
     *
     * @param fPlayer [FPlayer] Whom these credits should be provided with.
     * @param amount [Double] The amount of credits the corresponding player will be supplied with.
     * @param overrideLimit [Boolean] Whether or not the credits that are given can exceed the limitation.
     *
     * @return [Double] If the limit is overriden and our overflow is above 0, the remainder will be returned, otherwise 0.0.
     */
    fun give(fPlayer: FPlayer, amount: Double, overrideLimit: Boolean = false): Double {
        val updated = fPlayer.credits + amount
        val overflow = if (!overrideLimit) updated - creditSettings.maximumCredits else 0.0

        modify(fPlayer, if (overrideLimit || overflow <= 0) updated else updated - overflow, overrideLimit)
        return if (overflow > 0) overflow else 0.0
    }

    /**
     * Take away a specific amount of credits from a player.
     *
     * @param fPlayer [FPlayer] Whom these credits should be taken away from.
     * @param amount [Double] The amount of credits to be taken away from the specified player.
     *
     * @return [Double] If the player does not have enough credits to take, the overflow will be returned, otherwise 0.0.
     */
    fun take(fPlayer: FPlayer, amount: Double): Double {
        val updated = fPlayer.credits - amount
        modify(fPlayer, if (updated < 0) 0.0 else updated)
        return if (updated < 0) abs(updated) else 0.0
    }

    /**
     * Reset a player's credits to the default of [net.prosavage.factionsx.persist.settings.CreditSettings.startingCredits].
     *
     * @param fPlayer [FPlayer] Whom's credits that shall be reset.
     * @param overrideLimit [Boolean] Whether or not the credits can exceed the limitation.
     */
    fun reset(fPlayer: FPlayer, overrideLimit: Boolean = false) {
        modify(fPlayer, creditSettings.startingCredits, overrideLimit)
    }

    /**
     * Check whether or not a player can afford a specific amount of credits.
     *
     * @param fPlayer [FPlayer] Whom's credits to be checked.
     * @param amount [Double] The amount of credits to check if the specified player can afford.
     *
     * @return [Boolean] Whether or not the corresponding player can afford the specified amount.
     */
    fun has(fPlayer: FPlayer, amount: Double): Boolean = fPlayer.credits >= amount

    /**
     * Trigger a payment between two players.
     *
     * @param sender [FPlayer] Whom is sending the amount of credits to another player.
     * @param receiver [FPlayer] Player receiving the credits.
     * @param amount [Double] The amount of credits the sender is providing to the receiver.
     *
     * @return [Double] If the overflow outcome is above 0, the remainder will be returned, otherwise 0.0.
     */
    internal fun pay(sender: FPlayer, receiver: FPlayer, amount: Double): Double {
        if (sender.credits < amount) return -1.0

        val receiverUpdated = receiver.credits + amount
        val overflow = receiverUpdated - creditSettings.maximumCredits

        modify(sender, sender.credits - (if (overflow > 0) amount - overflow else amount))
        modify(receiver, if (overflow > 0) receiverUpdated - overflow else receiverUpdated)

        return if (overflow > 0) overflow else 0.0
    }
}