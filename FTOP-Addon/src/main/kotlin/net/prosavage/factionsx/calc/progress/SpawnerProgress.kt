package net.prosavage.factionsx.calc.progress

import org.bukkit.entity.EntityType

/**
 * This progress implementation works around spawners.
 *
 * @param type [EntityType] the entity type of this spawner.
 */
class SpawnerProgress constructor(
    val type: EntityType,
    ownerThen: Long,
    location: ProgressLocation,
    value: Double,
    tillFinishedMs: Long,
    startedAt: Long = System.currentTimeMillis()
) : BlockProgress(ownerThen, location, value, tillFinishedMs, startedAt)