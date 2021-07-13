package net.prosavage.factionsx.persist

import net.prosavage.factionsx.addonframework.AddonPlugin
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

    fun save(addon: AddonPlugin) {
        addon.configSerializer.save(instance, File(addon.dataFolder, "config.json"))
    }

    fun load(addon: AddonPlugin) {
        populateOptions()
        addon.configSerializer.load(instance, FMobsConfig::class.java, File(addon.dataFolder, "config.json"))
    }
}