package net.prosavage.factions.persist

import net.prosavage.factionsx.addonframework.Addon
import java.io.File
import java.util.*

object CoreChunkStore {
    @Transient
    private val instance = this

    private final val fileName = "corechunk-data.json"

    fun save(addon: Addon) {
        addon.configSerializer.save(instance, File(addon.addonDataFolder, fileName))
    }

    fun load(addon: Addon) {
        addon.configSerializer.load(instance, CoreChunkStore::class.java, File(addon.addonDataFolder, fileName))
    }
}