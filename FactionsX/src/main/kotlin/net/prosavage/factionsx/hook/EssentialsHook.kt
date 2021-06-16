package net.prosavage.factionsx.hook

import net.ess3.api.IEssentials
import net.prosavage.factionsx.util.logColored
import org.bukkit.Bukkit
import org.bukkit.entity.Player

object EssentialsHook : PluginHook {
    override val pluginName: String = "Essentials"

    lateinit var essentials: IEssentials

    override fun isHooked(): Boolean {
        return this::essentials.isInitialized
    }

    override fun load() {
        if (Bukkit.getPluginManager().isPluginEnabled(pluginName))
            this.essentials = Bukkit.getPluginManager().getPlugin(pluginName) as IEssentials
        if (isHooked()) logColored("Hooked into $pluginName.")
    }

    fun playerIsVanished(player: Player): Boolean {
        return isHooked() && essentials.getUser(player).isVanished
    }


}