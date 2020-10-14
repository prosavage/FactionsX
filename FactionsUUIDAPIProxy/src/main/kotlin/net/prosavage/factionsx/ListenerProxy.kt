package net.prosavage.factionsx

import com.massivecraft.factions.event.PowerLossEvent
import net.prosavage.factionsx.event.FPlayerPowerLossEvent
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

class ListenerProxy : Listener {

    @EventHandler
    fun onFactionsXFPlayerPowerLoss(event: FPlayerPowerLossEvent) {
        println("Proxy: proxying PowerLossEvent")
        val powerLossEvent = PowerLossEvent(event.fPlayer, event.loss)
        Bukkit.getPluginManager().callEvent(powerLossEvent)
        if (powerLossEvent.isCancelled) event.isCancelled = true
    }

}