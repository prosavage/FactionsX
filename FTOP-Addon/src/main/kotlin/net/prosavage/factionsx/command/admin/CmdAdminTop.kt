package net.prosavage.factionsx.command.admin

import net.prosavage.factionsx.command.engine.CommandInfo
import net.prosavage.factionsx.command.engine.CommandRequirementsBuilder
import net.prosavage.factionsx.command.engine.FCommand
import net.prosavage.factionsx.persist.FTOPConfig
import org.bukkit.plugin.java.JavaPlugin

class CmdAdminTop(instance: JavaPlugin) : FCommand() {
    init {
        aliases.add("top")

        addSubCommand(CmdAdminStart(instance))
        addSubCommand(CmdAdminEnd(instance))

        commandRequirements = CommandRequirementsBuilder()
                .withRawPermission(FTOPConfig.ftopAdminPermission)
                .build()
    }


    override fun execute(info: CommandInfo): Boolean {
        generateHelp(1, info.commandSender, info.args)
        return true
    }

    override fun getHelpInfo(): String {
        return FTOPConfig.commandAdminTopHelp
    }
}