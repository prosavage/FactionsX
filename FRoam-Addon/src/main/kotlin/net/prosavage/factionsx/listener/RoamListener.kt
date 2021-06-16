package net.prosavage.factionsx.listener

import club.rarlab.classicplugin.extension.hasCooldown
import club.rarlab.classicplugin.extension.setCooldown
import club.rarlab.classicplugin.extension.then
import club.rarlab.classicplugin.task.schedule
import net.prosavage.factionsx.core.RoamAPI
import net.prosavage.factionsx.helper.getCorners
import net.prosavage.factionsx.helper.isAllowedInRegion
import net.prosavage.factionsx.helper.isRoaming
import net.prosavage.factionsx.persist.data.getFLocation
import net.prosavage.factionsx.persistence.config.Config
import net.prosavage.factionsx.persistence.config.Config.messageWithPrefix
import net.prosavage.factionsx.persistence.config.Message
import net.prosavage.factionsx.util.getFPlayer
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.*
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause.SPECTATE

/**
 * Class to handle base Roam events.
 */
class RoamListener : Listener {
    /**
     * This event is triggered when a [org.bukkit.entity.Player] joins.
     * Used to show all [club.rarlab.classicplugin.nms.entity.FakePlayer] to the corresponding player.
     */
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    private fun PlayerJoinEvent.onRoam() {
        schedule(async = true, then = {
            for (roamPlayer in RoamAPI.getRoamers().values) {
                roamPlayer.npc.showTo(this.player)
            }
        })
    }

    /**
     * This event is triggered when a [org.bukkit.entity.Player] quits.
     * Used to disable a [org.bukkit.entity.Player]'s Roam if active.
     */
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    private fun PlayerQuitEvent.onRoam() = RoamAPI.disable(this.player)

    /**
     * This event is triggered when a [org.bukkit.entity.Player] teleports.
     * Used to cancel a [org.bukkit.entity.Player]'s teleportation if Roam is active.
     */
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    private fun PlayerTeleportEvent.onRoam() {
        if (this.cause !== SPECTATE || !RoamAPI.isRoaming(this.player)) return
        this.isCancelled = true
    }

    /**
     * This event is triggered when a [org.bukkit.entity.Player] moves.
     * Used to monitor a [org.bukkit.entity.Player]'s location to check if they've reached the maximum distance allowed.
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    private fun PlayerMoveEvent.onRoam() {
        val fPlayer = player.getFPlayer()
        if (!fPlayer.isRoaming() || from.blockX == to?.blockX && from.blockY == to?.blockY && from.blockZ == to?.blockZ) return

        val location = RoamAPI.getRoamPlayer(player)?.startLocation ?: return
        val (firstCorner, secondCorner) = location.getCorners(Config.maximumDistance, Config.maximumYDistance)

        if (to?.toVector()?.isInAABB(firstCorner.toVector(), secondCorner.toVector()) == true) {
            if (player.isAllowedInRegion(getFLocation(to ?: return))) return
            this.setTo(this.from).then {
                if (player.hasCooldown("roam_wg_reject")) return@then
                fPlayer.message(Message.worldGuardReject, !messageWithPrefix)
                player.setCooldown("roam_wg_reject")
            }
            return
        }

        this.setTo(this.from).then {
            if (player.hasCooldown("roam_move_delay")) return@then
            fPlayer.message(Message.maximumDistance, !messageWithPrefix)
            player.setCooldown("roam_move_delay")
        }
    }

    /**
     * This event is triggered when a [org.bukkit.entity.Player] processes a command.
     * Used to cancel commands being executed that are disallowed.
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    private fun PlayerCommandPreprocessEvent.onRoam() {
        if (!RoamAPI.isRoaming(player) || Config.allowedCommands.any { message.startsWith(it) }) return
        this.isCancelled = true
        player.getFPlayer().message(Message.commandIsDisallowed, !messageWithPrefix)
    }
}