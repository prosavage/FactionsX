package net.prosavage.factionsx.command.admin.cmd

import net.prosavage.factionsx.command.engine.CommandInfo
import net.prosavage.factionsx.command.engine.CommandRequirementsBuilder
import net.prosavage.factionsx.command.engine.FCommand
import net.prosavage.factionsx.core.Permission
import net.prosavage.factionsx.persist.Message

class CmdAdminPaypalView : FCommand() {
    init {
        aliases.add("view")

        requiredArgs.add(Argument("faction-name", 0, FactionArgument()))

        commandRequirements = CommandRequirementsBuilder()
                .withPermission(Permission.PAYPAL_ADMIN)
                .build()
    }

    override fun execute(info: CommandInfo): Boolean {
        val faction = info.getArgAsFaction(0) ?: return false
        info.message(Message.commandPaypalView, faction.tag, faction.paypal)
        return true
    }

    override fun getHelpInfo(): String {
        return Message.commandPaypalViewHelp
    }
}