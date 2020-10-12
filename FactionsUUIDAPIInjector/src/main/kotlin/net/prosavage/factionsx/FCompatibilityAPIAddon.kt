package net.prosavage.factionsx

import com.massivecraft.factions.FPlayers
import com.massivecraft.factions.Factions
import org.bukkit.plugin.java.JavaPlugin

class FCompatibilityAPIAddon : JavaPlugin() {
    override fun onEnable() {
        Factions.setInstance(Factions())
        FPlayers.setInstance(FPlayers())
    }

    override fun onDisable() {
        TODO("Not yet implemented")
    }

}