package net.prosavage.factionsx.command.admin.cmd

import net.prosavage.factionsx.FactionsX
import net.prosavage.factionsx.addonframework.Addon
import net.prosavage.factionsx.addonframework.AddonManager
import net.prosavage.factionsx.command.engine.CommandInfo
import net.prosavage.factionsx.command.engine.CommandRequirementsBuilder
import net.prosavage.factionsx.command.engine.FCommand
import net.prosavage.factionsx.core.Permission
import net.prosavage.factionsx.persist.Message
import net.prosavage.factionsx.scoreboard.startScoreboardMonitor
import java.util.stream.Collectors
import kotlin.system.measureTimeMillis

class CmdFactionsReload : FCommand() {
    init {
        aliases.add("reload")

        requiredArgs.add(Argument("hot-reload-jars/reload-configs", 0, ReloadTypeArgument()))
        requiredArgs.add(Argument("addon/[all]", 1, AddonArgumentType()))

        commandRequirements = CommandRequirementsBuilder()
                .withPermission(Permission.ADMIN_RELOAD)
                .build()
    }

    override fun execute(info: CommandInfo): Boolean {
        val hotReload = info.args.getOrNull(0)?.equals("hot-reload-jars") ?: false

        val time = measureTimeMillis {
            val factionsX = FactionsX.instance
            factionsX.deRegisterUpgrades()
            factionsX.saveData()
            factionsX.registerUpgrades()
            factionsX.disableTasks()
            info.message(Message.commandAdminReloadStoppedTasks)
            factionsX.startPositionMonitor()
            factionsX.startInventoryManager()
            FactionsX.scoreboard?.let { board -> startScoreboardMonitor(board) }
            info.message(Message.commandAdminReloadStartedTasks)
            val addon = info.args.getOrNull(1)
            val addonManager = FactionsX.addonManager
            info.message(if (hotReload) Message.commandAdminReloadHotReload else Message.commandAdminReloadConfigs)
            if (addon == null || addon == "all") {
                addonManager.addOns.stream().forEach { loopAddon -> reloadAddon(hotReload, addonManager, loopAddon) }
                info.message(
                        Message.commandAdminReloadAddon,
                        addonManager.addOns.stream().map { loopAddon -> loopAddon.name }.collect(Collectors.joining(", "))
                )
            } else {
                val searchedAddon = addonManager.addOns.find { loopAddon -> loopAddon.name == addon }
                if (searchedAddon == null) info.message(Message.commandAdminReloadAddonNotFound)
                else {
                    reloadAddon(hotReload, addonManager, searchedAddon)
                    info.message(Message.commandAdminReloadAddon, searchedAddon.name)
                }
            }
        }

        info.message(Message.commandAdminReloadedPlugin, time.toString())
        return true
    }

    override fun getHelpInfo(): String {
        return Message.commandAdminReloadHelp
    }

    fun reloadAddon(hotReload: Boolean, addonManager: AddonManager, addon: Addon) {
        if (hotReload) addonManager.reloadAddon(addon) else addonManager.reloadAddOnConfig(addon)
    }
}