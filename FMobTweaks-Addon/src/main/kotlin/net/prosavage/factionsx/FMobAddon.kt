package net.prosavage.factionsx

import net.prosavage.factionsx.addonframework.AddonPlugin
import net.prosavage.factionsx.addonframework.StartupResponse
import net.prosavage.factionsx.events.TweaksListener
import net.prosavage.factionsx.persist.FMobsConfig
import org.bukkit.Bukkit

class FMobAddon : AddonPlugin(true) {
    override fun onStart(): StartupResponse {
        logColored("Enabling MobTweaks Addon")
        FMobsConfig.load(this)

        logColored("Loaded configuration files.")
        Bukkit.getServer().pluginManager.registerEvents(TweaksListener, FactionsX.instance)

        return StartupResponse.ok()
    }

    override fun onTerminate() {
        logColored("Disabling MobTweaks Addon")
        FMobsConfig.load(this)

        FMobsConfig.save(this)
        logColored("Saved configuration files.")
    }
}