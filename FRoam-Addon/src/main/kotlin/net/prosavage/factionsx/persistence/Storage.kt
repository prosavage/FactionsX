package net.prosavage.factionsx.persistence

import club.rarlab.classicplugin.extension.colourise
import club.rarlab.classicplugin.nms.GlobalReflection.VERSION_NUMBER
import club.rarlab.classicplugin.nms.entity.FakePlayer
import club.rarlab.classicplugin.utility.XMaterial.Companion.matchXMaterial
import net.prosavage.factionsx.core.RoamPlayer
import net.prosavage.factionsx.manager.TimeTask
import net.prosavage.factionsx.manager.TimerManager
import net.prosavage.factionsx.manager.teleportAsync
import net.prosavage.factionsx.persistence.config.Config
import net.prosavage.factionsx.persistence.config.Message
import net.prosavage.factionsx.util.getFPlayer
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.util.*
import java.util.Collections.unmodifiableMap


/**
 * Class that contains handler functions for Roam.
 */
class Storage internal constructor() {
    /**
     * [IdentityHashMap] of [Player] with their Roam active.
     */
    private val players: IdentityHashMap<Player, RoamPlayer> = IdentityHashMap()

    /**
     * Enable a [Player]'s Roam mode.
     *
     * @param player        whom's Roam mode to enable.
     * @param timeInSeconds how long the player can roam for, < 0 for infinite.
     * @return [Boolean] whether or not the operation was successful.
     */
    fun enable(player: Player?, timeInSeconds: Int = -1): Boolean {
        if (player == null || players.containsKey(player)) return false

        with(FakePlayer.generate(player.uniqueId, generateName(player))) {
            with(Config.skinOptions) skin@{
                if (useOwner) {
                    applySkin(player)
                    return@skin
                }

                applySkin(com.mojang.authlib.properties.Property("textures", textures, signature))
            }

            if (VERSION_NUMBER < 161) {
                equipment {
                    with(Config.equipmentOptions) {
                        val inventory = player.inventory
                        helmet = getCorrespondentItem(helmetType, inventory.helmet)
                        chestPlate = getCorrespondentItem(chestPlateType, inventory.chestplate)
                        leggings = getCorrespondentItem(leggingsType, inventory.leggings)
                        boots = getCorrespondentItem(bootsType, inventory.boots)
                        hand = getCorrespondentItem(handType, inventory.itemInHand)
                    }
                }
            }

            setLocation(player.location)
            showTo(*Bukkit.getOnlinePlayers().toTypedArray())

            players += player to RoamPlayer(player, player.location, this)
            player.gameMode = GameMode.SPECTATOR

            if (timeInSeconds > 0) TimerManager.registerTimeTask("roam_time_${player.name}", TimeTask(
                    Date(System.currentTimeMillis() + (timeInSeconds * 1000)),
                    Runnable {
                        disable(player)
                        player.getFPlayer().message(Message.timeExpired, !Config.messageWithPrefix)
                    }
            ))
        }

        return true
    }

    /**
     * Disable a [Player]'s Roam mode.
     *
     * @param player whom's Roam mode to disable.
     * @return [Boolean] whether or not the operation was successful.
     */
    fun disable(player: Player?): Boolean = with(players.remove(player)) {
        if (player == null || this == null) return@with false

        npc.despawn()
        teleportAsync(player, startLocation)
        player.gameMode = GameMode.SURVIVAL
        TimerManager.removeTask("roam_time_${player.name}")

        return true
    }

    /**
     * Get a [Player]'s [RoamPlayer] object if present.
     *
     * @param player whom's [RoamPlayer] object to fetch.
     * @return [RoamPlayer] corresponding object, nullable.
     */
    fun getRoamPlayer(player: Player): RoamPlayer? = players[player]

    /**
     * Get whether or not a [Player] is in Roam mode.
     *
     * @param player whom to check.
     * @return [Boolean] whether or not the [Player] is in Roam mode.
     */
    fun isRoaming(player: Player?): Boolean = player != null && players.containsKey(player)

    /**
     * Get an immutable [Map] of players in Roam mode.
     *
     * @return [Map] of players that are roaming.
     */
    fun getRoamers(): Map<Player, RoamPlayer> = unmodifiableMap(this.players)

    /**
     * Get the correspondent item by type.
     *
     * @param type     of the item to fetch, OWNER / [Material].
     * @param original itemstack to be used if "OWNER" is not present.
     * @return [ItemStack] nullable correspondent itemstack.
     */
    private fun getCorrespondentItem(type: String, original: ItemStack?): ItemStack? {
        return if (!type.equals("OWNER", ignoreCase = true)) {
            matchXMaterial(type).get().toItem() ?: ItemStack(Material.AIR)
        } else original
    }

    /**
     * Generate the corresponding display name for a [FakePlayer] and it's owner.
     *
     * @param player whom the [FakePlayer] belongs to.
     * @return [String] corresponding generated display name.
     */
    private fun generateName(player: Player): String = Config.displayName.replace("{player}", player.name).colourise()
}