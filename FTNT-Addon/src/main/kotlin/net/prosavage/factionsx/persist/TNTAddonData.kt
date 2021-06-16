package net.prosavage.factionsx.persist

import net.prosavage.factionsx.addonframework.Addon
import net.prosavage.factionsx.core.FactionTNTData
import java.io.File

object TNTAddonData {

    @Transient
    private val instance = this


    var tntData = FactionTNTData(hashMapOf())


    fun save(addon: Addon) {
        addon.configSerializer.save(instance, File(addon.addonDataFolder, "tnt-data.json"))
    }

    fun load(addon: Addon) {
        addon.configSerializer.load(instance, TNTAddonData::class.java, File(addon.addonDataFolder, "tnt-data.json"))
    }
}