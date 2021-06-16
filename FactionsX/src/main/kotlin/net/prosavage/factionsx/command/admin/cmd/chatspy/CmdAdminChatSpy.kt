package net.prosavage.factionsx.command.admin.cmd.chatspy

import net.prosavage.factionsx.command.engine.CommandInfo
import net.prosavage.factionsx.command.engine.CommandRequirementsBuilder
import net.prosavage.factionsx.command.engine.FCommand
import net.prosavage.factionsx.core.Permission
import net.prosavage.factionsx.persist.Message

class CmdAdminChatSpy : FCommand() {
    init {
        aliases.add("chatspy")

        commandRequirements = CommandRequirementsBuilder()
                .withPermission(Permission.ADMIN_CHATSPY)
                .build()
    }

    override fun execute(info: CommandInfo): Boolean {
        val player = info.fPlayer ?: return false

        val current = player.isChatSpy
        player.isChatSpy = !current

        if (current) {
            player.message(Message.commandAdminChatSpyDisabled)
            return false
        }

        player.message(Message.commandAdminChatSpyEnabled)
        return true
    }

    override fun getHelpInfo(): String = Message.commandAdminChatSpyHelp
}