package net.prosavage.factionsx.listener

import net.prosavage.factionsx.core.Faction
import net.prosavage.factionsx.event.FPlayerFactionPreJoinEvent
import net.prosavage.factionsx.event.FPlayerFactionPreLeaveEvent
import net.prosavage.factionsx.event.FactionPreDisbandEvent
import net.prosavage.factionsx.persist.config.Config
import net.prosavage.factionsx.persist.config.Config.factionDisbandCommandsToExecutePerPlayer
import net.prosavage.factionsx.persist.config.Config.factionJoinCommandsToExecute
import net.prosavage.factionsx.persist.config.Config.factionLeaveCommandsToExecute
import net.prosavage.factionsx.persist.config.ProtectionConfig
import net.prosavage.factionsx.persist.data.getFLocation
import net.prosavage.factionsx.util.isActionsWhenOfflineCompatible
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Entity
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.entity.Projectile
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockFromToEvent
import org.bukkit.event.block.BlockPistonExtendEvent
import org.bukkit.event.block.BlockPistonRetractEvent
import org.bukkit.event.entity.CreatureSpawnEvent
import org.bukkit.event.entity.EntityExplodeEvent
import org.bukkit.event.vehicle.VehicleDamageEvent

class MiscListener : Listener {

    @EventHandler
    fun onExplode(event: EntityExplodeEvent) {
        if (ProtectionConfig.disableCreeperExplosionsGlobally && event.entityType == EntityType.CREEPER) {
            event.isCancelled = true
            return
        }

        event.blockList().removeIf {
            val fLocation = getFLocation(it.location)
            val factionAt = fLocation.getFaction()

            if (
                    factionAt.isWilderness() && !ProtectionConfig.allowExplosionsInWilderness
                    || factionAt.isSafezone() && !ProtectionConfig.allowExplosionsInSafeZone
                    || factionAt.isWarzone() && !ProtectionConfig.allowExplosionsInWarZone
                    || !factionAt.isSystemFaction() && ProtectionConfig.disableTntExplosionsInOtherTerritories && event.entityType == EntityType.PRIMED_TNT
            ) return@removeIf true

            val actionsWhenOffline = ProtectionConfig.overrideActionsWhenFactionOffline
            if (!actionsWhenOffline.allowTntExplosions && factionAt.isActionsWhenOfflineCompatible(actionsWhenOffline)) {
                return@removeIf true
            }

            if (!Config.factionShieldEnabled) return@removeIf false
            factionAt.shielded
        }
    }

    /**
     * Called when a [org.bukkit.entity.Creature] spawns in
     * and only handled if the [CreatureSpawnEvent.SpawnReason] is NATURAL.
     */
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    private fun CreatureSpawnEvent.onNaturalSpawn() {
        if (spawnReason !== CreatureSpawnEvent.SpawnReason.NATURAL) return
        val factionAt = getFLocation(this.location).getFaction()
        if (factionAt.isWilderness() && !Config.allowMobsNaturalSpawnInWilderness
                || factionAt.isWarzone() && !Config.allowMobsNaturalSpawnInWarZone
                || factionAt.isSafezone() && !Config.allowMobsNaturalSpawnInSafeZone
                || !factionAt.isSystemFaction() && !Config.allowMobsNaturalSpawnInFactionTerritories
        ) this.isCancelled = true
    }

    // Check if piston is extending into another's land.
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    fun onBlockPistonExtend(event: BlockPistonExtendEvent) {
        val factionAt: Faction = getFLocation(event.block.location).getFaction()
        val targetBlock = event.block.getRelative(event.direction, event.length + 1)
        val factionTo = getFLocation(targetBlock.location).getFaction()
        val targetBlockIsValid = targetBlock.isEmpty || targetBlock.isLiquid
        if (targetBlockIsValid && !canMoveToBlock(factionAt, factionTo) && !(!factionTo.isWarzone() && !factionTo.isSafezone() && ProtectionConfig.allowPistonExtensionsInOtherFaction)) event.isCancelled = true
    }

    // This will check if someone tries to steal using a sticky piston from another's land.
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    fun onPistonRetract(event: BlockPistonRetractEvent) {
        if (!event.isSticky) return
        val retractLocationBlock = event.block.getRelative(event.direction, -1)
        val factionRetractedTo = getFLocation(retractLocationBlock.location).getFaction()
        val factionAt = getFLocation(event.block.getRelative(event.direction, -2).location).getFaction()
        if (canMoveToBlock(factionRetractedTo, factionAt) || !factionAt.isWarzone() && !factionAt.isSafezone() && ProtectionConfig.allowPistonRetractionsInOtherFaction) return
        event.isCancelled = true
    }


    // Blocks water or lava from flowing to another faction's land -- protects against stuff like Cobblemonsters and more.
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    fun onLiquidFlow(event: BlockFromToEvent) {
        if (!event.block.isLiquid) return
        val factionAt = getFLocation(event.block.location).getFaction()
        val factionTo = getFLocation(event.toBlock.location).getFaction()
        if (canMoveToBlock(factionAt, factionTo)) return
        event.isCancelled = true
    }


    private fun canMoveToBlock(factionAt: Faction, factionTo: Faction): Boolean {
        return when {
            factionAt == factionTo -> true
            factionTo.isWilderness() -> true
            factionTo.isSafezone() || factionTo.isWarzone() -> false
            else -> false
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    fun onVehicleDamage(event: VehicleDamageEvent) {
        val vehicle = event.vehicle
        var damager = event.attacker

        if (damager is Projectile && damager.shooter is Entity) {
            damager = damager.shooter as Entity
        }

        if (damager is Player && vehicle.type.toString().contains("MINECART")) {
            val location = vehicle.location
            event.isCancelled = !PlayerListener.processBlockChangeInFactionLocation(
                getFLocation(location).getFaction(), damager,
                PlayerListener.BlockChangeAction.BREAK,
                Material.MINECART, true, location
            )
        }
    }

    @EventHandler
    private fun FactionPreDisbandEvent.onPreDisband() {
        // make sure event is not cancelled
        if (this.isCancelled) {
            return
        }

        // execute per player
        val console = Bukkit.getConsoleSender()
        factionDisbandCommandsToExecutePerPlayer.forEach {
            for (player in this.faction.getMembers())
                Bukkit.dispatchCommand(console, it.format(player.name))
        }
    }

    @EventHandler
    private fun FPlayerFactionPreJoinEvent.onPreJoin() {
        // make sure event is not cancelled
        if (this.isCancelled) {
            return
        }

        // execute commands
        val console = Bukkit.getConsoleSender()
        factionJoinCommandsToExecute.forEach {
            Bukkit.dispatchCommand(console, it.format(fPlayer.name))
        }
    }

    @EventHandler
    private fun FPlayerFactionPreLeaveEvent.onPreLeave() {
        // make sure event is not cancelled
        if (this.isCancelled) {
            return
        }

        // execute commands
        val console = Bukkit.getConsoleSender()
        factionLeaveCommandsToExecute.forEach {
            Bukkit.dispatchCommand(console, it.format(fPlayer.name))
        }
    }
}