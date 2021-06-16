package net.prosavage.factionsx.persist

import com.cryptomorin.xseries.XMaterial
import net.prosavage.factionsx.addonframework.Addon
import org.bukkit.entity.EntityType
import java.io.File

object BlockWorth {

    @Transient
    private val instance = this

    var values = mutableMapOf(
            XMaterial.DIAMOND_BLOCK to 100.0
    )

    var spawnerValues = mutableMapOf(
            EntityType.CREEPER to 50000.0,
            EntityType.ZOMBIE to 10000.0,
            EntityType.SKELETON to 25000.0
    )

    fun save(addon: Addon) {
        addon.configSerializer.save(instance, File(addon.addonDataFolder, "blockvalues.json"))
    }

    fun load(addon: Addon) {
        EntityType.values().forEach { entityType -> spawnerValues.putIfAbsent(entityType, 100.0) }
        XMaterial.values().forEach { xmat -> values.putIfAbsent(xmat, 0.0) }
        addon.configSerializer.load(instance, BlockWorth::class.java, File(addon.addonDataFolder, "blockvalues.json"))
    }

}