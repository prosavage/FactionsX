package net.prosavage.factions.persist

import net.prosavage.factionsx.addonframework.Addon
import java.io.File

object CoreChunkConfig {
    @Transient
    private val instance = this

    private final val fileName = "pricing-config.json"

    fun save(addon: Addon) {
        addon.configSerializer.save(instance, File(addon.addonDataFolder, fileName))
    }

    fun load(addon: Addon) {
        addon.configSerializer.load(
            instance,
            CoreChunkConfig::class.java,
            File(addon.addonDataFolder, fileName)
        )

        save(addon)
    }
}