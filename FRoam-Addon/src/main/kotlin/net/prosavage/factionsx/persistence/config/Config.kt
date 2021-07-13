package net.prosavage.factionsx.persistence.config

import net.prosavage.factionsx.addonframework.AddonPlugin
import java.io.File

/**
 * Class to handle the base Roam config.
 */
object Config {
    /**
     * [Config] instance to be used for saving & loading.
     */
    @Transient
    private val instance = this

    /**
     * [Boolean] whether or not the messages that are sent will be sent with the base prefix.
     */
    var messageWithPrefix = true

    /**
     * [Boolean] whether or not the Roam mode can be toggled within a worldguard region.
     */
    var toggleWithinWorldGuardRegion = false

    /**
     * [Double] maximum distance that a player can reach while in Roam mode.
     */
    var maximumDistance = 100.0

    /**
     * [Double] maximum distance that a player can reach (in Y) while in Roam mode.
     */
    var maximumYDistance = 25.0

    /**
     * [Int] the amount of time in seconds a player can roam.
     */
    var timeTillExpire = 30

    /**
     * [List] of allowed commands in Roam mode.
     */
    var allowedCommands = listOf("/f roam", "/msg", "/pay")

    /**
     * [Int] cooldown to be applied to a player upon command execution.
     */
    var executableCooldown = 10

    /**
     * [String] display name to be applied to our [club.rarlab.classicplugin.nms.entity.FakePlayer] entities.
     */
    var displayName = "&7[&6Roam&7] &e{player}"

    /**
     * [EquipmentOptions] of all available options to be used during
     * [club.rarlab.classicplugin.nms.entity.FakePlayer]'s equipment setup.
     */
    var equipmentOptions = EquipmentOptions("OWNER", "OWNER", "OWNER", "OWNER", "OWNER")

    /**
     * [SkinOptions] of all available options to be used during
     * [club.rarlab.classicplugin.nms.entity.FakePlayer]'s skin setup.
     */
    var skinOptions = SkinOptions(true, "textures_here", "signature_here")

    /**
     * Save the configuration.
     *
     * @param addon instance.
     */
    fun save(addon: AddonPlugin) = addon.configSerializer.save(instance, File(addon.dataFolder, "config.json"))

    /**
     * Load the configuration.
     *
     * @param addon instance.
     * @return [Config] corresponding configuration.
     */
    fun load(addon: AddonPlugin) = addon.configSerializer.load(instance, Config::class.java, File(addon.dataFolder, "config.json"))

    /**
     * A data class to determine options for all
     * [club.rarlab.classicplugin.nms.entity.FakePlayer]'s equipment.
     */
    data class EquipmentOptions internal constructor(
            val helmetType: String, val chestPlateType: String,
            val leggingsType: String, val bootsType: String, val handType: String
    )

    /**
     * A data class to determine options for all
     * [club.rarlab.classicplugin.nms.entity.FakePlayer]'s skins.
     */
    data class SkinOptions internal constructor(
            val useOwner: Boolean, val textures: String,
            val signature: String
    )
}