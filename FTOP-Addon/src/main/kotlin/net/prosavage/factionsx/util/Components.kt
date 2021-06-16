package net.prosavage.factionsx.util

import org.bukkit.Location

internal operator fun Location.component1(): Int = this.blockX
internal operator fun Location.component2(): Int = this.blockY
internal operator fun Location.component3(): Int = this.blockZ