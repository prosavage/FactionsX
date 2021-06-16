package net.prosavage.factionsx.persist

import com.cryptomorin.xseries.XMaterial
import net.prosavage.factionsx.addonframework.Addon
import net.prosavage.factionsx.calc.progress.BlockProgress
import java.io.File
import java.util.*

object ProgressStorage {
    @Transient private val instance = this

    var cache = hashMapOf<Long, HashMap<XMaterial, HashSet<BlockProgress>>>()

    fun save(addon: Addon) {
        addon.dataSerializer.save(instance, File(addon.addonDataFolder, "blockprogress.json"))
    }

    fun load(addon: Addon) {
        addon.dataSerializer.load(instance, ProgressStorage::class.java, File(addon.addonDataFolder, "blockprogress.json"))
    }
}