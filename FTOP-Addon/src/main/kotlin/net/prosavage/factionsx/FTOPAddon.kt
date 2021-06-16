package net.prosavage.factionsx

import com.cryptomorin.xseries.XMaterial
import net.prosavage.factionsx.addonframework.Addon
import net.prosavage.factionsx.calc.Response
import net.prosavage.factionsx.calc.ValueCalculatorTask
import net.prosavage.factionsx.calc.progress.BlockProgress
import net.prosavage.factionsx.command.CmdCalc
import net.prosavage.factionsx.command.CmdTop
import net.prosavage.factionsx.command.admin.CmdAdminTop
import net.prosavage.factionsx.core.Faction
import net.prosavage.factionsx.hooks.SpawnerProvider
import net.prosavage.factionsx.hooks.providers.WildStackerProvider
import net.prosavage.factionsx.listener.ProgressListener
import net.prosavage.factionsx.manager.PlaceholderManager
import net.prosavage.factionsx.manager.TimeTask
import net.prosavage.factionsx.manager.TimerManager
import net.prosavage.factionsx.persist.BlockWorth
import net.prosavage.factionsx.persist.FTOPConfig
import net.prosavage.factionsx.persist.MenuConfig
import net.prosavage.factionsx.persist.ProgressStorage
import net.prosavage.factionsx.util.logColored
import org.bukkit.Bukkit
import java.text.DecimalFormat
import java.time.Instant
import java.util.*

class FTOPAddon : Addon() {
    companion object {
        var calculating = false
        var factionValues = mapOf<Long, Response>()
        var latestCalc: Long = 0
        var task: TimeTask? = null
        val calculatorTask: ValueCalculatorTask by lazy { ValueCalculatorTask }
        var spawnerProvider: SpawnerProvider? = null
            set(value) { field = value; logColored("Loaded Spawner Provider: ${value?.name}") }

        fun Faction.getRank(): Long? = factionValues.entries
            .find { it.value.faction == this.id }?.key
    }

    override fun onEnable() {
        logColored("Enabling FactionsTop Addon")
        loadFiles()
        logColored("Loaded Configuration Files.")
        FactionsX.baseCommand.addSubCommand(CmdTop())
        FactionsX.baseCommand.addSubCommand(CmdCalc())
        FactionsX.baseAdminCommand.addSubCommand(CmdAdminTop(factionsXInstance))
        logColored("Injected commands.")

        loadPlaceholderAPIHook().let {
            if (it) logColored("Hooked into PlaceholderAPI.")
        }

        Bukkit.getPluginManager().registerEvents(ProgressListener, this.factionsXInstance)

        if (Bukkit.getPluginManager().getPlugin("WildStacker") != null) {
            spawnerProvider = WildStackerProvider()
        }

        if (FTOPConfig.autoCalc) {
            if (task == null) startFTOPAutoCalcTask()
            logColored("Started Automatic Calculation Task with an interval of ${FTOPConfig.autoCalcIntervalSeconds} seconds.")
        }

        // calculate with startup algo
        calculatorTask.calculate(true)

        val repeatTimeValueRecalc = 20L * FTOPConfig.progressiveValueUpdateEverySeconds
        Bukkit.getScheduler().runTaskTimerAsynchronously(this.factionsXInstance, Runnable {
            factionValues.values.forEach(Response::recalculate)
        }, repeatTimeValueRecalc, repeatTimeValueRecalc)

        PlaceholderManager.register("ftopaddon_faction_rank") { _, faction ->
            faction.getRank()?.toString() ?: FTOPConfig.unknownValuePlaceholder
        }
    }

    private fun startFTOPAutoCalcTask() {
        task = TimerManager.registerTimeTask("ftop", TimeTask(
                Date.from(Instant.now().plusSeconds(FTOPConfig.autoCalcIntervalSeconds.toLong())),
                Runnable {
                    calculatorTask.run()
                    startFTOPAutoCalcTask()
                }
        ))
    }

    override fun onDisable() {
        logColored("Disabling FactionsTop Addon.")
        calculating = false
        logColored("Disabled calculation status.")
        FactionsX.baseCommand.removeSubCommand(CmdTop())
        FactionsX.baseCommand.removeSubCommand(CmdCalc())
        FactionsX.baseAdminCommand.removeSubCommand(CmdAdminTop(factionsXInstance))
        logColored("Unregistered Commands.")
        ProgressStorage.cache = factionValues.values
            .filter { it.progressive.isNotEmpty() }
            .associate { it.faction to it.progressive.toMap() } as HashMap<Long, HashMap<XMaterial, HashSet<BlockProgress>>>
        saveFiles()
        logColored("Saved Configuration Files.")
    }

    private fun loadPlaceholderAPIHook(): Boolean {
        return Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI").let {
            if (it) PlaceholderAPIIntegration(factionsXInstance).register(); it
        }
    }

    private fun loadFiles() {
        FTOPConfig.load(this)
        BlockWorth.load(this)
        MenuConfig.load(this)
        ProgressStorage.load(this)
    }

    private fun saveFiles() {
        FTOPConfig.load(this)
        FTOPConfig.save(this)
        BlockWorth.load(this)
        BlockWorth.save(this)
        MenuConfig.load(this)
        MenuConfig.save(this)
        ProgressStorage.save(this)
    }
}