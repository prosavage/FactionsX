package net.prosavage.factionsx.persist

import com.cryptomorin.xseries.XMaterial
import net.prosavage.factionsx.addonframework.AddonPlugin
import net.prosavage.factionsx.calc.progress.BlockProgress
import java.io.File
import java.util.*

object ProgressStorage {
    @Transient private val instance = this

    var cache = hashMapOf<Long, HashMap<XMaterial, HashSet<BlockProgress>>>()

    fun save(addon: AddonPlugin) {
        addon.dataSerializer.save(instance, File(addon.dataFolder, "blockprogress.json"))
    }

    fun load(addon: AddonPlugin) {
        addon.dataSerializer.load(instance, ProgressStorage::class.java, File(addon.dataFolder, "blockprogress.json"))
    }
}