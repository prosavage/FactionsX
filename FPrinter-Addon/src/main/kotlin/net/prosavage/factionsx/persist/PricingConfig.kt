package net.prosavage.factionsx.persist

import net.prosavage.factionsx.addonframework.AddonPlugin
import org.bukkit.Material
import java.io.File

object PricingConfig {

    @Transient
    private val instance = this

    var blockValues = hashMapOf<Material, Double>()


    fun save(addon: AddonPlugin) {
        addon.configSerializer.save(instance, File(addon.dataFolder, "pricing-config.json"))
    }

    fun load(addon: AddonPlugin) {
        addon.configSerializer.load(
                instance,
                PricingConfig::class.java,
                File(addon.dataFolder, "pricing-config.json")
        )
        Material.values().forEach { material ->
            blockValues.putIfAbsent(material, 100.0)
        }
        save(addon)
    }


}