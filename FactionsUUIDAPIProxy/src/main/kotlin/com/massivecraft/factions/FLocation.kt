package com.massivecraft.factions

import net.prosavage.factionsx.persist.data.FLocation
import org.bukkit.Location

class FLocation {
    lateinit var fLocation: FLocation
    constructor(location: Location) {
        FLocation(location.x.toLong().shr(4), location.z.toLong().shr(4), location.world!!.name)
    }

    constructor(fLocation: FLocation) {
        this.fLocation = fLocation
    }
}