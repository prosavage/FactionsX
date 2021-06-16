package net.prosavage.factionsx.hooks

import org.bukkit.block.CreatureSpawner

interface SpawnerProvider {
    /**
     * [String] name of the provider.
     */
    val name: String

    /**
     * Get the amount of stacked spawners by a [CreatureSpawner].
     */
    fun getAmount(spawner: CreatureSpawner): Int
}