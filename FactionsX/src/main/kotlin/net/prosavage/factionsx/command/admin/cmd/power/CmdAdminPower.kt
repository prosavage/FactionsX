package net.prosavage.factionsx.command.admin.cmd.power

import net.prosavage.factionsx.command.engine.CommandInfo
import net.prosavage.factionsx.command.engine.CommandRequirementsBuilder
import net.prosavage.factionsx.command.engine.FCommand
import net.prosavage.factionsx.core.Permission
import net.prosavage.factionsx.persist.Message

class CmdAdminPower : FCommand() {
    init {
        aliases.add("power")

        addSubCommand(CmdAdminPowerSet())
        addSubCommand(CmdAdminPowerBoost())

        commandRequirements = CommandRequirementsBuilder()
                .withPermission(Permission.ADMIN_POWER)
                .build()
    }

    override fun execute(info: CommandInfo): Boolean {
        generateHelp(1, info.commandSender, info.args)
        return true
    }

    override fun getHelpInfo(): String {
        return Message.commandAdminPowerHelp
    }
}