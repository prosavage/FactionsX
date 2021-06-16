package net.prosavage.factionsx.manager

import io.papermc.lib.PaperLib
import me.oliwer.bossbarav.BossBarWorker
import net.prosavage.factionsx.FactionsX
import net.prosavage.factionsx.FactionsX.Companion.bossBarController
import net.prosavage.factionsx.core.Faction
import net.prosavage.factionsx.event.FPlayerTerritoryChangeEvent
import net.prosavage.factionsx.manager.PlaceholderManager.processPlaceholders
import net.prosavage.factionsx.persist.Message
import net.prosavage.factionsx.persist.color
import net.prosavage.factionsx.persist.config.Config
import net.prosavage.factionsx.persist.config.FlyConfig
import net.prosavage.factionsx.persist.data.getFLocation
import net.prosavage.factionsx.util.Relation
import net.prosavage.factionsx.util.multiColor
import org.bukkit.Bukkit
import org.bukkit.Color
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerTeleportEvent

class PositionMonitor : Runnable {

    override fun run() {
        Bukkit.getOnlinePlayers().forEach { player ->
            val fPlayer = PlayerManager.getFPlayer(player)
            val location = player.location
            fPlayer.showChunkBorder(player, location)
            if (fPlayer.lastLocation != null && hasMoved(location, fPlayer.lastLocation!!)) {
                var enemyNearBy = false
                if (FlyConfig.factionsFly) {
                    // This checks to make sure some other plugin has not disabled our fly, if they have, we disable it internally to not show particles etc.
                    val oldFlyStatus = fPlayer.isFFlying
                    if (fPlayer.isFFlying && !player.allowFlight) fPlayer.setFly(false)
                    enemyNearBy = fPlayer.runFlyEnemyNearByCheck(FactionsX.instance, player)
                    if (oldFlyStatus && !fPlayer.isFFlying) fPlayer.showFlyParticle(player, location)
                }
                if (location.chunk != fPlayer.lastLocation!!.chunk) {
                    // We changed chunks
                    val currentFLoc = getFLocation(location)
                    val factionAt = currentFLoc.getFaction()
                    val faction = fPlayer.getFaction()
                    // Fly checks - enables and disables auto.
                    if (FlyConfig.factionsFly
                            && !enemyNearBy
                            && ((FlyConfig.factionsFlyAutoEnable && !fPlayer.isFFlying) || fPlayer.isFFlying)) {
                            fPlayer.runFlyChecks(player, factionAt,
                                    preCheckNotification = false,
                                    notify = true
                            )
                    }
                    val lastFactionAt = getFLocation(fPlayer.lastLocation!!).getFaction()
                    if (fPlayer.mapToggle) fPlayer.sendMap()
                    if (fPlayer.chunkBorderColor == null) fPlayer.chunkBorderColor = getChunkBorderColor(faction, factionAt).toColorData()
                    if (fPlayer.hasFaction() && fPlayer.isAutoClaiming) {
                        GridManager.claimLand(fPlayer.autoClaimTargetFaction
                                ?: faction, currentFLoc, fplayer = fPlayer, asAdmin = (fPlayer.inBypass || fPlayer.autoClaimTargetFaction != null))
                    }
                    // Use else if to prevent sending faction update message when using autoclaim.
                    else if (factionAt != lastFactionAt) {
                        if (Config.positionMonitorChunkChangedBossBar) {
                            fPlayer.lastLocation?.let { bossBarController.expose(player, it.chunk) }
                            bossBarController.lookupOrInsert(location.chunk, BossBarWorker.create(
                                location.world,
                                100f,
                                multiColor(processPlaceholders(fPlayer, factionAt, Config.positionMonitorChunkChangedBossBarMessage))
                            )).show(player)
                        }

                        val chunkMessageInWorld = !Config.worldsNoChunkMessageHandling.contains(location.world?.name)
                        if (Config.positionMonitorChunkChangedMessage && fPlayer.enabledChunkMessage && chunkMessageInWorld) {
                            fPlayer.message(processPlaceholders(fPlayer, factionAt, Config.positionMonitorChunkChanged))
                        }

                        if (Config.positionMonitorChunkChangeTitle && fPlayer.enabledChunkMessage && chunkMessageInWorld) {
                            player.sendTitle(color(processPlaceholders(fPlayer, factionAt, Config.positionMonitorChunkChangedTitle.title)),
                                    color(processPlaceholders(fPlayer, factionAt, Config.positionMonitorChunkChangedTitle.subtitle)))
                        }

                        fPlayer.chunkBorderColor = getChunkBorderColor(faction, factionAt).toColorData()
                        Bukkit.getPluginManager().callEvent(FPlayerTerritoryChangeEvent(fPlayer, lastFactionAt, factionAt))
                    }
                }
            }
            fPlayer.lastLocation = location
        }


    }

    fun Color.toColorData(): Config.ColorData {
        return Config.ColorData(red, green, blue)
    }


    fun getChunkBorderColor(faction: Faction, factionAt: Faction): Color {
        val relation = faction.getRelationTo(factionAt)
        return when {
            factionAt.isWilderness() -> Config.seeChunkWildernessColor.toColor()
            factionAt == faction -> Config.seeChunkOwnFactionColor.toColor()
            factionAt.isWarzone() -> Config.seeChunkWarzoneColor.toColor()
            factionAt.isSafezone() -> Config.seeChunkSafezoneColor.toColor()
            relation == Relation.TRUCE -> Config.seeChunkTruceColor.toColor()
            relation == Relation.ENEMY -> Config.seeChunkEnemyColor.toColor()
            relation == Relation.NEUTRAL -> Config.seeChunkNeutralColor.toColor()
            relation == Relation.ALLY -> Config.seeChunkAllyColor.toColor()
            else -> Color.GRAY
        }
    }


}


private fun hasMoved(location: Location, lastLocation: Location): Boolean {
    return (location.world != lastLocation.world
            || location.x != lastLocation.x
            || location.y != lastLocation.y
            || location.z != lastLocation.z)
}

fun teleportAsync(player: Player, location: Location) {
    PaperLib.teleportAsync(player, location, PlayerTeleportEvent.TeleportCause.PLUGIN)
}


fun teleportAsyncWithWarmup(player: Player, location: Location, delay: Long, then: Runnable = Runnable {}) {
    val fplayer = PlayerManager.getFPlayer(player)
    val loc = player.location
    Bukkit.getScheduler().runTaskLater(FactionsX.instance, Runnable {
        if (hasMoved(player.location, loc)) {
            fplayer.message(Message.positionChangedTeleportWarmup)
            return@Runnable
        }
        teleportAsync(player, location)
        then.run()
    }, delay)
}