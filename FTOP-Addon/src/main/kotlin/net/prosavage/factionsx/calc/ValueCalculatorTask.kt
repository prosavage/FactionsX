package net.prosavage.factionsx.calc

import net.prosavage.factionsx.FTOPAddon
import net.prosavage.factionsx.FTOPAddon.Companion.calculating
import net.prosavage.factionsx.persist.FTOPConfig
import net.prosavage.factionsx.persist.ProgressStorage
import net.prosavage.factionsx.persist.color
import org.bukkit.Bukkit

object ValueCalculatorTask : Runnable {
    /**
     * Run the calculator task.
     */
    override fun run() = calculate(false)

    /**
     * Calculate all factions.
     */
    internal fun calculate(startup: Boolean) {
        calculating = true

        recalculateAll { time, values ->
            if (startup) values.forEach {
                val newProgressions = it.progressive
                val iterator = ProgressStorage.cache[it.faction]?.iterator() ?: return@forEach

                while (iterator.hasNext()) {
                    val (material, progressions) = iterator.next()
                    val new = newProgressions[material] ?: continue

                    new.removeAll(progressions)
                    new.addAll(progressions)
                }

                it.recalculate()
                iterator.remove()
            }

            sortValues(values)

            if (FTOPConfig.broadcastCompletion) {
                Bukkit.broadcastMessage(color(FTOPConfig.broadcastMessage
                    .replace("{time}", "%.2f".format(time))
                    .replace("{factions}", values.size.toString())
                ))
            }

            calculating = false
        }
    }

    /**
     * Sort all values and reassign [FTOPAddon.factionValues] with the
     * corresponding [Response] map.
     */
    fun sortValues(values: Collection<Response>) {
        FTOPAddon.factionValues = values
            .sortedByDescending(Response::latestCalculatedValue)
            .mapIndexed { index, info -> (index + 1).toLong() to info }
            .toMap()
    }
}