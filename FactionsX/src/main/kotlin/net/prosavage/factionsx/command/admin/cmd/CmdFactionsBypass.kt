package net.prosavage.factionsx.command.admin.cmd

import net.prosavage.factionsx.command.engine.CommandInfo
import net.prosavage.factionsx.command.engine.CommandRequirementsBuilder
import net.prosavage.factionsx.command.engine.FCommand
import net.prosavage.factionsx.core.Permission
import net.prosavage.factionsx.persist.Message

class CmdFactionsBypass : FCommand() {
    init {
        aliases.add("bypass")

        commandRequirements = CommandRequirementsBuilder()
                .asPlayer(true)
                .withPermission(Permission.ADMIN_BYPASS)
                .build()
    }

    override fun execute(info: CommandInfo): Boolean {
        info.fPlayer!!.inBypass = !info.fPlayer!!.inBypass
        return true
    }

    override fun getHelpInfo(): String {
        return Message.commandFactionsBypassHelp
    }
}