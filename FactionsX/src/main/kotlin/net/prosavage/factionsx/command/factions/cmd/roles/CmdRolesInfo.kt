package net.prosavage.factionsx.command.factions.cmd.roles

import me.rayzr522.jsonmessage.JSONMessage
import net.prosavage.factionsx.command.engine.CommandInfo
import net.prosavage.factionsx.command.engine.CommandRequirementsBuilder
import net.prosavage.factionsx.command.engine.FCommand
import net.prosavage.factionsx.persist.Message
import net.prosavage.factionsx.persist.color
import net.prosavage.factionsx.util.buildBar

class CmdRolesInfo : FCommand() {

    init {
        aliases.add("info")

        requiredArgs.add(Argument("role", 0, RolesArgument()))

        commandRequirements = CommandRequirementsBuilder().asLeader(true).build()
    }

    override fun execute(info: CommandInfo): Boolean {
        val role = info.getArgAsRole(0) ?: return false
        if (Message.commandRolesInfoSendBar) info.message(buildBar(Message.commandRolesInfoBarElement))
        info.message(Message.commandRolesInfoHeader, role.roleTag)
        JSONMessage.create(color(String.format(Message.commandRolesInfoChatTag, role.chatTag)))
                .tooltip(color(Message.commandRolesInfoChatTagToolTip))
                .suggestCommand("/f roles edit chatTag ")
                .send(info.player)
        JSONMessage.create(color(String.format(Message.commandRolesInfoRoleTag, role.roleTag)))
                .tooltip(color(Message.commandRolesInfoRoleTagToolTip))
                .suggestCommand("/f roles edit roleTag ")
                .send(info.player)
        JSONMessage.create(color(String.format(Message.commandRolesInfoIconMaterial, role.iconMaterial.toString())))
                .tooltip(color(Message.commandRolesInfoIconMaterialToolTip))
                .suggestCommand("/f roles edit iconMaterial ")
                .send(info.player)
        JSONMessage.create(color(Message.commandRolesInfoPermissions))
                .tooltip(color(Message.commandRolesInfoPermissionsToolTip))
                .runCommand("/f perms role-menu ${role.roleTag}")
                .send(info.player)
        if (Message.commandRolesInfoSendBar) info.message(buildBar(Message.commandRolesInfoBarElement))
        return true
    }

    override fun getHelpInfo(): String {
        return Message.commandRolesInfoHelp
    }
}