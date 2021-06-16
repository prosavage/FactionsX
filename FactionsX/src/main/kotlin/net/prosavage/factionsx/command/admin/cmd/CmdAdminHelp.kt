package net.prosavage.factionsx.command.admin.cmd

import net.prosavage.factionsx.FactionsX
import net.prosavage.factionsx.command.engine.CommandInfo
import net.prosavage.factionsx.command.engine.CommandRequirementsBuilder
import net.prosavage.factionsx.command.engine.FCommand
import net.prosavage.factionsx.core.Permission
import net.prosavage.factionsx.persist.Message

class CmdAdminHelp : FCommand() {
    init {
        aliases.add("help")

        requiredArgs.add(Argument("page-number", 0, IntArgument()))

        commandRequirements = CommandRequirementsBuilder()
                .withPermission(Permission.ADMIN_HELP)
                .build()
    }

    override fun execute(info: CommandInfo): Boolean {
        val page = info.getArgAsInt(0) ?: return false
        FactionsX.baseAdminCommand.generateHelp(page, info.commandSender, info.args)
        return true
    }

    override fun getHelpInfo(): String {
        return Message.commandAdminHelpHelp
    }
}