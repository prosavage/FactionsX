package net.prosavage.factionsx.hooks.providers

import com.bgsoftware.wildstacker.api.WildStackerAPI
import net.prosavage.factionsx.hooks.SpawnerProvider
import org.bukkit.block.CreatureSpawner

class WildStackerProvider : SpawnerProvider {
    override val name: String = "WildStacker"

    override fun getAmount(spawner: CreatureSpawner): Int =
        WildStackerAPI.getSpawnersAmount(spawner)
}