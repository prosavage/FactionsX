package net.prosavage.factionsx

import net.prosavage.factionsx.addonframework.Addon
import net.prosavage.factionsx.events.TweaksListener
import net.prosavage.factionsx.persist.FMobsConfig
import org.bukkit.Bukkit

class FMobAddon : Addon() {

    override fun onEnable() {
        logColored("Enabling MobTweaks Addon")
        FMobsConfig.load(this)
        logColored("Loaded configuration files.")
        Bukkit.getServer().pluginManager.registerEvents(TweaksListener, factionsXInstance)
    }

    override fun onDisable() {
        logColored("Disabling MobTweaks Addon")
        FMobsConfig.load(this)
        FMobsConfig.save(this)
        logColored("Saved configuration files.")
    }
}