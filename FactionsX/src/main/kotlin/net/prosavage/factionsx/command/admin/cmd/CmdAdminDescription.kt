package net.prosavage.factionsx.command.admin.cmd

import net.prosavage.factionsx.command.engine.CommandInfo
import net.prosavage.factionsx.command.engine.CommandRequirementsBuilder
import net.prosavage.factionsx.command.engine.FCommand
import net.prosavage.factionsx.core.Permission
import net.prosavage.factionsx.persist.Message

class CmdAdminDescription : FCommand() {
    init {
        aliases.add("desc")
        aliases.add("description")

        requiredArgs.add(Argument("target", 0, FactionArgument()))
        optionalArgs.add(Argument("description", 1, StringArgument()))
        bypassArgumentCount = true

        commandRequirements = CommandRequirementsBuilder()
            .withPermission(Permission.ADMIN_DESCRIPTION)
            .build()
    }

    override fun execute(info: CommandInfo): Boolean {
        val target = info.getArgAsFaction(0, false) ?: return false
        val description = info.args.drop(1).joinToString(" ")

        if (description.isEmpty()) {
            info.message(Message.commandAdminDescriptionEmpty)
            return false
        }

        target.description = description
        info.message(Message.commandAdminDescriptionSuccess, target.tag, description)
        return true
    }

    override fun getHelpInfo(): String = Message.commandAdminDisbandHelp
}