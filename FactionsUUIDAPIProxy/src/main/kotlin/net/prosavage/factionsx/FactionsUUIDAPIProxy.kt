package net.prosavage.factionsx

import com.massivecraft.factions.FPlayers
import com.massivecraft.factions.Factions
import com.massivecraft.factions.Board
import com.massivecraft.factions.proxy.ProxyBoard
import com.massivecraft.factions.proxy.ProxyFactions
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin

class FactionsUUIDAPIProxy : JavaPlugin() {
    override fun onEnable() {
        Factions.setInstance(ProxyFactions())
        FPlayers.setInstance(FPlayers())
        Board.setInstance(ProxyBoard())
        Bukkit.getPluginManager().registerEvents(ListenerProxy(), this)
        logger.info("Enabled FactionsUUID API Proxy")
        logger.info("This plugin can convert FactionsUUID API calls to FactionsX API calls.")
    }

    override fun onDisable() {
        println("Disabling..")
    }

}