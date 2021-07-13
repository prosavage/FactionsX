package net.prosavage.factionsx.persist

import net.prosavage.factionsx.addonframework.AddonPlugin
import net.prosavage.factionsx.core.FactionTNTData
import java.io.File

object TNTAddonData {
    @Transient
    private val instance = this

    var tntData = FactionTNTData(hashMapOf())

    fun save(addon: AddonPlugin) {
        addon.configSerializer.save(instance, File(addon.dataFolder, "tnt-data.json"))
    }

    fun load(addon: AddonPlugin) {
        addon.configSerializer.load(instance, TNTAddonData::class.java, File(addon.dataFolder, "tnt-data.json"))
    }
}