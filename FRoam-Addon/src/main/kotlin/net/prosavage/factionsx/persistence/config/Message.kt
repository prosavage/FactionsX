package net.prosavage.factionsx.persistence.config

import net.prosavage.factionsx.addonframework.AddonPlugin
import java.io.File

/**
 * Class to handle the Roam messages.
 */
object Message {
    /**
     * [Message] instance to be used for saving & loading.
     */
    @Transient
    private val instance = this

    /**
     * [String] this message will be sent to players when they enable their Roam.
     */
    var roamEnabled = "&7Your roam mode has been &6enabled&7."

    /**
     * [String] this message will be sent to players when they disable their Roam.
     */
    var roamDisabled = "&7Your roam mode has been &6disabled&7."

    /**
     * [String] this message will be sent to players when they meet the maximum distance in roam.
     */
    var maximumDistance = "&7You have met the maximum distance!"

    /**
     * [String] this message will be sent to players when they execute a command that is forbidden.
     */
    var commandIsDisallowed = "&7This command is forbidden in roam!"

    /**
     * [String] this message will be sent to players when they execute the base command.
     */
    var commandCooldown = "&7You cannot yet enable roam!"

    /**
     * [String] this message will be sent to players when their time in roam has expired.
     */
    var timeExpired = "&7Your time in Roam has expired!"

    /**
     * [String] this message will be sent to players when they either tries to execute the command
     * or tries to fly in a worldguard region.
     */
    var worldGuardReject = "&7Roam is not allowed in worldguard regions!"

    /**
     * Save the configuration.
     *
     * @param addon instance.
     */
    fun save(addon: AddonPlugin) {
        addon.configSerializer.save(instance, File(addon.dataFolder, "messages.json"))
    }

    /**
     * Load the configuration.
     *
     * @param addon instance.
     */
    fun load(addon: AddonPlugin) {
        addon.configSerializer.load(instance, Message::class.java, File(addon.dataFolder, "messages.json"))
    }
}