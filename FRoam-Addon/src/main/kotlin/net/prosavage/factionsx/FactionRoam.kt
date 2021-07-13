package net.prosavage.factionsx

import club.rarlab.classicplugin.ClassicPlugin
import club.rarlab.classicplugin.cooldown.BasicCooldown
import club.rarlab.classicplugin.cooldown.CooldownHandler.register
import club.rarlab.classicplugin.cooldown.CooldownHandler.unregister
import club.rarlab.classicplugin.extension.then
import club.rarlab.classicplugin.packet.PacketInjector.inject
import club.rarlab.classicplugin.packet.PacketInjector.uninject
import club.rarlab.classicplugin.utility.listenTo
import net.prosavage.factionsx.FactionsX.Companion.baseCommand
import net.prosavage.factionsx.addonframework.AddonPlugin
import net.prosavage.factionsx.addonframework.StartupResponse
import net.prosavage.factionsx.command.CommandRoam
import net.prosavage.factionsx.command.engine.FCommand
import net.prosavage.factionsx.listener.RoamListener
import net.prosavage.factionsx.packet.PacketReader
import net.prosavage.factionsx.persistence.Storage
import net.prosavage.factionsx.persistence.config.Config
import net.prosavage.factionsx.persistence.config.Message
import org.bukkit.event.EventPriority
import org.bukkit.event.HandlerList
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import java.time.Duration.ofSeconds

/**
 * Main class to handle the Roam addon.
 */
class FactionRoam : AddonPlugin(true) {
    /**
     * [FCommand] instance of [CommandRoam].
     */
    private val commandRoam: FCommand = CommandRoam()

    /**
     * [Listener] instance of [RoamListener].
     */
    private val listenerRoam: Listener = RoamListener()

    // Global Stuff.
    companion object {
        val STORAGE: Storage by lazy { Storage() }
    }

    // Called when the addon enables.
    override fun onStart(): StartupResponse {
        // Base logging.
        logColored("Enabling Roam...")

        // Management preparation.
        ClassicPlugin.PLUGIN.setInstance(FactionsX.instance)

        // Configuration preparation.
        Config.load(this).then { logColored("Loaded options.") }
        Message.load(this).then { logColored("Loaded messages.") }

        // General preparation.
        this.listenToEvents()
        baseCommand.addSubCommand(this.commandRoam)

        // Cooldown preparation.
        val baseDelay = ofSeconds(5)
        register("roam_move_delay", BasicCooldown(baseDelay))
        register("command_roam", BasicCooldown(ofSeconds(Config.executableCooldown.toLong())))
        register("roam_wg_reject", BasicCooldown(baseDelay))

// Force shutdown preparation.
//        Runtime.getRuntime().addShutdownHook(Thread {
//            Bukkit.getOnlinePlayers().forEach(::uninject).then { logColored("Uninjected all online players.") }
//        })
        return StartupResponse.ok()
    }

    // Called when the addon disables.
    override fun onTerminate() {
        // General handling.
        HandlerList.unregisterAll(listenerRoam)
        baseCommand.removeSubCommand(this.commandRoam)

        // Cooldown handling.
        unregister("roam_move_delay")
        unregister("command_roam")
        unregister("roam_wg_reject")

        // Configuration handling.
        Config.save(this)
        Message.save(this)
    }

    // Listen to all general events.
    private fun listenToEvents() = with(FactionsX.instance) {
        // Manual.
        listenTo<PlayerJoinEvent>(EventPriority.LOWEST) { inject(PacketReader(this.player)) }
        listenTo<PlayerQuitEvent>(EventPriority.HIGHEST) { uninject(this.player) }

        // Classes.
        server.pluginManager.registerEvents(listenerRoam, this)
    }
}