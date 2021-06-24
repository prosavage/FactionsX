package net.prosavage.factionsx.command.admin.cmd.maxpower

import net.prosavage.factionsx.command.engine.CommandInfo
import net.prosavage.factionsx.command.engine.CommandRequirementsBuilder
import net.prosavage.factionsx.command.engine.FCommand
import net.prosavage.factionsx.core.Permission
import net.prosavage.factionsx.persist.Message

class CmdAdminMaxPower : FCommand() {
    init {
        aliases.add("maxpower")

        this.subCommands += CmdAdminMaxPowerBoost()

        commandRequirements = CommandRequirementsBuilder()
            .withPermission(Permission.ADMIN_MAX_POWER)
            .build()
    }

    override fun execute(info: CommandInfo): Boolean = generateHelp(1, info.commandSender, info.args).let { true }
    override fun getHelpInfo(): String = Message.commandAdminMaxPowerHelp
}