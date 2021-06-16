package net.prosavage.factionsx.command.admin.cmd.credits

import net.prosavage.factionsx.command.engine.CommandInfo
import net.prosavage.factionsx.command.engine.CommandRequirementsBuilder
import net.prosavage.factionsx.command.engine.FCommand
import net.prosavage.factionsx.core.Permission
import net.prosavage.factionsx.persist.Message

class CmdAdminCredits : FCommand() {
    init {
        aliases.add("credits")

        commandRequirements = CommandRequirementsBuilder()
                .withPermission(Permission.ADMIN_CREDITS)
                .build()

        this.addSubCommand(CmdCreditsAdminGive())
        this.addSubCommand(CmdCreditsAdminRemove())
        this.addSubCommand(CmdCreditsAdminReset())
    }

    override fun execute(info: CommandInfo): Boolean {
        generateHelp(1, info.commandSender, info.args)
        return false
    }

    override fun getHelpInfo(): String {
        return Message.commandCreditsAdminHelp
    }
}