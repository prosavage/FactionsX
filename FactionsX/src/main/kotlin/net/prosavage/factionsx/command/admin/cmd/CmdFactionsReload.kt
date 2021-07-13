package net.prosavage.factionsx.command.admin.cmd

import net.prosavage.factionsx.FactionsX
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

        commandRequirements = CommandRequirementsBuilder()
                .withPermission(Permission.ADMIN_RELOAD)
                .build()
    }

    override fun execute(info: CommandInfo): Boolean {
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
        }

        info.message(Message.commandAdminReloadedPlugin, time.toString())
        return true
    }

    override fun getHelpInfo(): String {
        return Message.commandAdminReloadHelp
    }
}