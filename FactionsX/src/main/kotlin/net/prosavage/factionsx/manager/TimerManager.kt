package net.prosavage.factionsx.manager

import net.prosavage.factionsx.FactionsX
import net.prosavage.factionsx.persist.config.Config
import org.bukkit.Bukkit
import org.bukkit.scheduler.BukkitTask
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeUnit.MILLISECONDS

object TimerManager {

    var monitoring: BukkitTask? = null

    val timedTasks = ConcurrentHashMap<String, TimeTask>()

    fun registerTimeTask(id: String, timeTask: TimeTask): TimeTask {
        //println("Registering ${id} -> ${timeTask.time}")
        if (timedTasks[id] != null) {
            throw IllegalArgumentException("An task with this ID is already registered.")
        }

        timedTasks[id] = timeTask
        return timeTask
    }

    fun startMonitoring() {
        monitoring = Bukkit.getScheduler().runTaskTimer(FactionsX.instance, Runnable {
            for (entry in timedTasks) {
                entry.value.hasEnded(entry.key, true)
            }
        }, 0L, 20L)
    }

    fun getTimeTask(id: String): TimeTask? {
        return timedTasks.filterKeys { taskId -> taskId == id }.values.firstOrNull()
    }

    fun removeTask(id: String) {
        timedTasks.remove(id)
    }

}

class TimeTask(val time: Date, val onTime: Runnable) {

    /**
     * This method returns time left in milliseconds.
     */
    fun getTimeLeft(): Long {
        return time.time - Date().time
    }

    fun hasEnded(id: String, executeOnFinish: Boolean = false): Boolean {
        val ended = getTimeLeft() <= 0L
        if (ended && executeOnFinish) {
            finish(id)
        }
        return ended
    }

    fun finish(id: String) {
        TimerManager.timedTasks.remove(id)
        onTime.run()
    }

}

fun formatMillis(millis: Long, format: String = Config.timeFormatted): String {
    return format
            .replace("{days}", MILLISECONDS.toDays(millis).toString())
            .replace("{hours}", (MILLISECONDS.toHours(millis) % TimeUnit.DAYS.toHours(1)).toString())
            .replace("{minutes}", (MILLISECONDS.toMinutes(millis) % TimeUnit.HOURS.toMinutes(1)).toString())
            .replace("{seconds}", (MILLISECONDS.toSeconds(millis) % TimeUnit.MINUTES.toSeconds(1)).toString())
}