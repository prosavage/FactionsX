package net.prosavage.factionsx.persist

import net.prosavage.factionsx.addonframework.Addon
import org.bukkit.entity.EntityType
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason
import java.io.File

object FMobsConfig {

    @Transient
    private val instance = this

    data class CustomMobHealth(val enabled: Boolean, val health: Double, val spawnReasons: Set<SpawnReason>)

    var customMobHealthEnabled = false
    var customMobHealth = hashMapOf<EntityType, CustomMobHealth>()
    var customMobHealthWorldBlackList = listOf("world-name")

    var pigMenAlwaysAggro = false
    var antiBlazeDrown = false

    private fun populateOptions() {
        EntityType.values().forEach { entityType ->
            if (entityType != EntityType.PLAYER)
                customMobHealth[entityType] = CustomMobHealth(false, 1.0, SpawnReason.values().toSet())
        }
    }

    fun save(addon: Addon) {
        addon.configSerializer.save(instance, File(addon.addonDataFolder, "config.json"))
    }

    fun load(addon: Addon) {
        populateOptions()
        addon.configSerializer.load(instance, FMobsConfig::class.java, File(addon.addonDataFolder, "config.json"))
    }
}