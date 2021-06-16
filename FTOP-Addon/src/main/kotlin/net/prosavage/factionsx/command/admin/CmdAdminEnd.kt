package net.prosavage.factionsx.command.admin

import net.prosavage.factionsx.FTOPAddon
import net.prosavage.factionsx.command.engine.CommandInfo
import net.prosavage.factionsx.command.engine.CommandRequirementsBuilder
import net.prosavage.factionsx.command.engine.FCommand
import net.prosavage.factionsx.persist.FTOPConfig
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin

class CmdAdminEnd(private val instance: JavaPlugin) : FCommand() {
    init {
        aliases.add("abort")
        aliases.add("end")

        commandRequirements = CommandRequirementsBuilder()
                .withRawPermission(FTOPConfig.ftopAdminEndPermission)
                .build()
    }

    override fun execute(info: CommandInfo): Boolean {
        FTOPAddon.calculating = false
        info.message(FTOPConfig.commandAdminTopEndAborting)
        Bukkit.getScheduler().runTaskLater(instance, Runnable {
            info.message(FTOPConfig.commandAdminTopEndAborted)
        }, (FTOPConfig.intervalPeriodMillis * 2 / 1000) * 20)
        return true
    }

    override fun getHelpInfo(): String {
        return FTOPConfig.commandAdminTopEndHelp
    }
}