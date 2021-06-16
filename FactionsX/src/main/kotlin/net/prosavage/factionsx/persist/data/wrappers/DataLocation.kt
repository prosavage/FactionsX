package net.prosavage.factionsx.persist.data.wrappers

import org.bukkit.Bukkit
import org.bukkit.Location


fun Location.getDataLocation(withYaw: Boolean = false, withPitch: Boolean = false): DataLocation =
    DataLocation(world!!.name, x, y, z, if (withYaw) yaw else -1F, if (withPitch) pitch else -1F)

data class DataLocation(val worldName: String, val x: Double, val y: Double, val z: Double, val yaw: Float = -1F, val pitch: Float = -1F) {
    fun exists(): Boolean {
        return Bukkit.getWorld(worldName) != null
    }

    fun getLocation(): Location = Location(Bukkit.getWorld(worldName), x, y, z).apply {
        val dataYaw = this@DataLocation.yaw
        if (dataYaw != -1F) yaw = dataYaw

        val dataPitch = this@DataLocation.pitch
        if (dataPitch != -1F) pitch = dataPitch
    }
}