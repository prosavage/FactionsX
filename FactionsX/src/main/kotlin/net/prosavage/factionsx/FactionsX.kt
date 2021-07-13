package net.prosavage.factionsx

import fr.minuskube.inv.InventoryManager
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import me.oliwer.bossbar.api.BarController
import me.rarlab.worldguard.WorldGuardLayer
import me.rarlab.worldguard.helper.Fetcher
import net.prosavage.baseplugin.SavagePlugin
import net.prosavage.baseplugin.serializer.Serializer
import net.prosavage.factionsx.command.admin.FactionsAdminBaseCommand
import net.prosavage.factionsx.command.factions.FactionsBaseCommand
import net.prosavage.factionsx.core.registerAllPermissions
import net.prosavage.factionsx.documentation.FactionsXDocumentationProvider
import net.prosavage.factionsx.hook.EssentialsHook
import net.prosavage.factionsx.hook.PlaceholderAPIIntegration
import net.prosavage.factionsx.hook.ShopGUIPlusHook
import net.prosavage.factionsx.hook.combatlog.CombatLogHook
import net.prosavage.factionsx.hook.vault.VaultHook
import net.prosavage.factionsx.listener.*
import net.prosavage.factionsx.manager.*
import net.prosavage.factionsx.manager.PlaceholderManager.isPlaceholderApi
import net.prosavage.factionsx.persist.color
import net.prosavage.factionsx.persist.config.Config
import net.prosavage.factionsx.persist.config.UpgradesConfig
import net.prosavage.factionsx.persist.data.Factions
import net.prosavage.factionsx.persist.data.Grid
import net.prosavage.factionsx.persist.data.Players
import net.prosavage.factionsx.scoreboard.Scoreboard
import net.prosavage.factionsx.scoreboard.implementations.FeatherBoard
import net.prosavage.factionsx.scoreboard.implementations.InternalBoard
import net.prosavage.factionsx.scoreboard.startScoreboardMonitor
import net.prosavage.factionsx.upgrade.UpgradeScope
import net.prosavage.factionsx.util.Migration
import net.prosavage.factionsx.util.logColored
import org.bstats.bukkit.Metrics
import org.bukkit.Bukkit
import org.bukkit.Bukkit.getPluginManager
import org.bukkit.Chunk
import org.bukkit.scheduler.BukkitTask
import java.io.File
import java.util.*
import java.util.concurrent.Callable
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.logging.Logger
import kotlin.io.path.ExperimentalPathApi
import kotlin.io.path.exists
import kotlin.io.path.isDirectory
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

class FactionsX : SavagePlugin() {
    companion object {
        lateinit var instance: FactionsX
        lateinit var baseCommand: FactionsBaseCommand
        lateinit var baseAdminCommand: FactionsAdminBaseCommand
        lateinit var logger: Logger
        lateinit var inventoryManager: InventoryManager
        lateinit var worldGuard: WorldGuardLayer<*>
        var scoreboard: Scoreboard? = null
        val bossBarController: BarController<Chunk> by lazyOf(BarController(emptyMap(), true))
    }

    override fun onLoad() {
        worldGuard = Fetcher.fetch().apply {
            if (!this.hooked) {
                return@apply
            }

            this.registerFlag("no-power-loss", false)
            this.registerFlag("pvp-ignore-relation", false)
            this.registerFlag("destroy-block-safezone", false)
            this.registerFlag("destroy-block-warzone", false)

            logColored("Hooked into WorldGuard")
        }
    }

    @OptIn(ExperimentalPathApi::class)
    @ExperimentalTime
    override fun onEnable() {
        instance = this
        super.onEnable()
        FactionsX.logger = super.getLogger()
        logColored("&6================================================")
        val startupTime = measureTime {
            printLogo()
            ConfigFileManager.setup()
            val dataLoad = measureTime { loadData() }
            logColored("Loaded Data & Configuration files in &6$dataLoad&7.")
            with (Migration) {
                migrateFactions()
                migratePlayers()
            }
            logColored("Migrated Applicable Data.")
            registerAllPermissions(getPluginManager())
            logColored("Registered all permissions.")
            baseCommand = setupCommand()
            baseAdminCommand = setupAdminCommands()
            logColored("Setup factions commands.")
            registerListeners(DataListener(), PlayerListener(), ChatListener(), MiscListener(), FactionListener, bossBarController)
            logColored("Registered Factions Listeners.")
            startPositionMonitor()
            logColored("Started Position Monitor")
            startInventoryManager()
            scoreboard = fetchScoreboard()
            logColored("Scoreboard Running (${if (Config.scoreboardOptions.enabled) "Enabled" else "Disabled"}): ${scoreboard?.type ?: "None"}")
            startScoreboardMonitor(scoreboard)
            logColored("Started Scoreboard Monitor")
            enableMetrics()
            logColored("Enabled Metrics.")
            loadHooks()
            logColored("Loaded all plugin hooks.")
            logColored("Attempting to load addons.")
            baseCommand.initializeSubCommandData()
            baseAdminCommand.initializeSubCommandData()
            logColored("Initialized Command Data")
            registerUpgrades()
            logColored("Registered Upgrades.")
            logColored("Total Upgrades: &6${UpgradeManager.getTotalUpgrades()}&r.")
            logColored("    &aTerritory Upgrades: &r${UpgradeManager.getUpgrades(UpgradeScope.TERRITORY).size}")
            logColored("    &bGlobal Upgrades: &r${UpgradeManager.getUpgrades(UpgradeScope.GLOBAL).size}")
            startSaveTask()
            logColored("Started autosave task.")
            TimerManager.startMonitoring()
            // run the boss bar ticking task
            Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate({
                bossBarController.cache.forEach { (_, bar) -> bar.tick() }
            }, 0, 1, TimeUnit.MILLISECONDS)
        }
        logColored("Startup Completed In &6$startupTime&7.")
        logColored("&6================================================")
    }

    private fun startSaveTask() {
        val autoSaveTaskID = "AUTOSAVE"
        // Remove old autosave task ( for reloads )
        TimerManager.removeTask(autoSaveTaskID)
        if (!Config.autoSave) return
        registerAutoSaveTask(autoSaveTaskID)
    }

    private fun registerAutoSaveTask(taskID: String) {
        TimerManager.registerTimeTask(taskID, TimeTask(Date(Date().time + Config.autoSaveIntervalInMilliseconds), Runnable {
            if (Config.autoSaveBroadcast) Bukkit.broadcastMessage(color(Config.autoSaveBroadcastMessage))
            GlobalScope.launch {
                saveData()
                if (Config.autoSaveBroadcastComplete) Bukkit.broadcastMessage(color(Config.autoSaveBroadcastCompleteMessage))
            }
            registerAutoSaveTask(taskID)
        }))
    }

    private fun fetchScoreboard(): Scoreboard? = when {
        with(getPluginManager().getPlugin("FeatherBoard")) {
            this != null && this.isEnabled
        } -> FeatherBoard()
        Config.scoreboardOptions.internal -> InternalBoard()
        else -> null
    }

    fun startInventoryManager() {
        inventoryManager = InventoryManager(this)
        inventoryManager.init()
    }

    fun registerUpgrades() = UpgradeManager.registerUpgradesFromConfig()

    fun deRegisterUpgrades() = UpgradesConfig.upgrades.forEach { (_, upgrades) ->
        upgrades.forEach { upgrade ->
            UpgradeManager.deRegisterUpgradeByName(upgrade.name)
        }
    }

    private fun loadHooks() {
        ShopGUIPlusHook.load()
        logColored("Loaded ShopGUIPlus Hook")
        VaultHook.load()
        logColored("Loaded Vault Hook.")
        loadPlaceholderAPIHook()
        logColored("Loaded PlaceholderAPI Hook.")
        EssentialsHook.load()
        logColored("Loaded Essentials Hook.")
        // load documentation provider if present
        FactionsXDocumentationProvider().generateDocs()
        logColored("Registered Documentation Provider.")
        CombatLogHook.attemptLoad()?.also {
            CombatLogHook.instance = it
            logColored("Loaded ${it.name} as CombatLog Hook.")
        }
    }

    override fun onDisable() {
        super.onDisable()
        saveData()
        disableTasks()
        deRegisterUpgrades()
        TimerManager.timedTasks.clear()
        PlaceholderManager.unregisterAll()
    }

    private fun loadPlaceholderAPIHook() {
        if (server.pluginManager.isPluginEnabled("PlaceholderAPI")) {
            PlaceholderAPIIntegration().register()
            isPlaceholderApi = true
        }
    }

    private fun enableMetrics() {
        val pluginID = 6967
        val metrics = Metrics(this, pluginID)

        metrics.addCustomChart(Metrics.SingleLineChart("factions", Callable { FactionManager.getFactions().size }))
    }

    fun startPositionMonitor(): BukkitTask = Bukkit.getScheduler().runTaskTimer(this, PositionMonitor(), 0L, 5L)


    fun disableTasks() = Bukkit.getScheduler().cancelTasks(this)


    private fun setupCommand(): FactionsBaseCommand {
        val baseCommand = FactionsBaseCommand()
        val command = this.getCommand("factions")!!
        command.setExecutor(baseCommand)
        command.tabCompleter = baseCommand
        return baseCommand
    }

    private fun setupAdminCommands(): FactionsAdminBaseCommand {
        val adminBaseCommand = FactionsAdminBaseCommand()
        val aCommand = this.getCommand("fx")!!
        aCommand.setExecutor(adminBaseCommand)
        aCommand.tabCompleter = adminBaseCommand
        return adminBaseCommand
    }

    private fun loadData() {
        File(dataFolder, "config/").mkdirs()
        File(dataFolder, "config/GUI/").mkdirs()
        ConfigFileManager.load()
        Players.load()
        Grid.load()
        Factions.load(this)
        logColored("Loaded ${Players.fplayers.size} players.")
        logColored("Loaded ${Factions.factions.size} factions.")
        logColored("Loaded ${Grid.claimGrid.size} claims.")
    }

    fun saveData() {
        File(dataFolder, "config/").mkdirs()
        File(dataFolder, "config/GUI/").mkdirs()
        ConfigFileManager.save()
        Players.save()
        Factions.save()
        Grid.save()
    }

    private fun printLogo() {
        logColored("&l&6FactionsX")
        logColored("By: ProSavage and SavageLabs Team.")
        logColored("&9https://savagelabs.net")
    }
}