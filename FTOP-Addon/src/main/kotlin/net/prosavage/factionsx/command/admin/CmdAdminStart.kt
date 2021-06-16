package net.prosavage.factionsx.command.admin

import net.prosavage.factionsx.FTOPAddon
import net.prosavage.factionsx.calc.ValueCalculatorTask
import net.prosavage.factionsx.command.engine.CommandInfo
import net.prosavage.factionsx.command.engine.CommandRequirementsBuilder
import net.prosavage.factionsx.command.engine.FCommand
import net.prosavage.factionsx.persist.FTOPConfig
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin

class CmdAdminStart(private val instance: JavaPlugin) : FCommand() {
    init {
        aliases.add("start")

        commandRequirements = CommandRequirementsBuilder()
                .withRawPermission(FTOPConfig.ftopAdminStartPermission)
                .build()
    }

    override fun execute(info: CommandInfo): Boolean {
        if (FTOPAddon.calculating) {
            info.message(FTOPConfig.commandAdminTopStartAlreadyCalculating)
            return false
        }

        info.message(FTOPConfig.commandAdminStartStarted)
        Bukkit.getScheduler().runTaskAsynchronously(instance, ValueCalculatorTask) // TRY ADDON IN THE MORNING
        return true
    }

    override fun getHelpInfo(): String {
        return FTOPConfig.commandAdminStartHelp
    }
}