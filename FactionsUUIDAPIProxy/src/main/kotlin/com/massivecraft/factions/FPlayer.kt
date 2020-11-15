package com.massivecraft.factions

import net.prosavage.factionsx.core.FPlayer
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

interface FPlayer {
    abstract val fplayer: FPlayer

    fun login() {

    }

    // We dont need to do anything here.
    fun logout()

    fun getFaction(): Faction?

    fun getFactionId(): String?

    fun hasFaction(): Boolean

    fun setFaction(faction: Faction?)

    fun isVanished(): Boolean

    fun isAdminBypassing(): Boolean

    fun setIsAdminBypassing(`val`: Boolean)

    fun getLastLoginTime(): Long

    fun setLastLoginTime(lastLoginTime: Long)

    fun isMapAutoUpdating(): Boolean

    fun setMapAutoUpdating(mapAutoUpdating: Boolean)

    fun getTitle(): String?

    fun setTitle(sender: CommandSender?, title: String?)

    fun getName(): String?

    fun getTag(): String?

    fun getChatTag(): String?

    fun getPower(): Double

    fun alterPower(delta: Double)

    fun getPowerMax(): Double

    fun getPowerMin(): Double

    fun getPowerRounded(): Int

    fun getPowerMaxRounded(): Int

    fun getPowerMinRounded(): Int

    fun updatePower()

    fun isInOwnTerritory(): Boolean

    fun isInOthersTerritory(): Boolean

    fun isInAllyTerritory(): Boolean

    fun isInNeutralTerritory(): Boolean

    fun isInEnemyTerritory(): Boolean

    fun leave(makePay: Boolean)

    fun getId(): String?

    fun getPlayer(): Player?

    fun isOnline(): Boolean

    fun sendMessage(message: String?)

    fun sendMessage(messages: List<String?>?)

    fun getMapHeight(): Int


    fun isOffline(): Boolean

    fun isFlying(): Boolean

    fun setFlying(fly: Boolean)

    fun setFlying(fly: Boolean, damage: Boolean)

    fun isSeeingChunk(): Boolean

    fun setSeeingChunk(seeingChunk: Boolean)
}

