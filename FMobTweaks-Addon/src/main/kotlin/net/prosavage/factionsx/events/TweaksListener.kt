package net.prosavage.factionsx.events

import net.prosavage.factionsx.persist.FMobsConfig
import org.bukkit.World
import org.bukkit.entity.EntityType
import org.bukkit.entity.PigZombie
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.CreatureSpawnEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.EntityDamageEvent.DamageCause

object TweaksListener : Listener {

    @EventHandler
    fun onMobSpawn(e: CreatureSpawnEvent) {
        if (!FMobsConfig.customMobHealthEnabled
                || FMobsConfig.customMobHealthWorldBlackList.contains(e.entity.location.world!!.name)
                || !FMobsConfig.customMobHealth[e.entity.type]!!.enabled
                || !FMobsConfig.customMobHealth[e.entity.type]!!.spawnReasons.contains(e.spawnReason)
        )
            return
        e.entity.health = 1.0
    }

    @EventHandler
    fun onPigMenSpawn(e: CreatureSpawnEvent) {
        if (!FMobsConfig.pigMenAlwaysAggro
                || e.entity.world.environment != World.Environment.NETHER
                || e.entityType != EntityType.ZOMBIFIED_PIGLIN)
            return
        val entity = e.entity as PigZombie
        entity.isAngry = true
    }

    @EventHandler
    fun onBlazeDrown(e: EntityDamageEvent) {
        if (!FMobsConfig.antiBlazeDrown || e.entityType != EntityType.BLAZE && e.cause != DamageCause.DROWNING
                || e.entity.type != EntityType.BLAZE) return
        if (e.cause == DamageCause.DROWNING) e.damage = 0.0
    }
}