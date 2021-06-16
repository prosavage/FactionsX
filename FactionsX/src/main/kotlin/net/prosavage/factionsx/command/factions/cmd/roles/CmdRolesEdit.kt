package net.prosavage.factionsx.command.factions.cmd.roles

import com.cryptomorin.xseries.XMaterial
import net.prosavage.factionsx.command.engine.CommandInfo
import net.prosavage.factionsx.command.engine.CommandRequirementsBuilder
import net.prosavage.factionsx.command.engine.FCommand
import net.prosavage.factionsx.gui.perms.PermsRoleMenu
import net.prosavage.factionsx.persist.Message

class CmdRolesEdit : FCommand() {

    init {
        aliases.add("edit")

        requiredArgs.add(Argument("role", 0, RolesArgument()))
        requiredArgs.add(Argument("property", 1, RolePropertyTypeArgument()))
        optionalArgs.add(Argument("value", 2, StringArgument()))
        commandRequirements = CommandRequirementsBuilder()
                .asLeader(true)
                .build()
    }

    override fun execute(info: CommandInfo): Boolean {
        val role = info.getArgAsRole(0) ?: return false
        val roleProperty = RolePropertyType::class.java.enumConstants
                .firstOrNull { it.tag == info.args[1] } ?: run {
            info.message(Message.commandRolesEditProperties, RolePropertyType.values().joinToString(", ") { it.tag })
            return false
        }
        val stringProperty = info.args.getOrNull(2) ?: run {
            info.message(Message.commandRolesEditValues)
            return false
        }
        when (roleProperty) {
            RolePropertyType.CHAT_TAG -> role.chatTag = stringProperty
            RolePropertyType.ROLE_TAG -> role.roleTag = stringProperty
            RolePropertyType.ICON_MATERIAL -> role.iconMaterial = XMaterial.matchXMaterial(stringProperty).get()
            RolePropertyType.PERMISSIONS -> PermsRoleMenu.getInv(info.faction!!, role)?.open(info.player)
        }
        return true
    }

    override fun getHelpInfo(): String {
        return Message.commandRolesEditHelp
    }
}


