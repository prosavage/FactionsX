package net.prosavage.factionsx.command.factions.cmd.perms

import net.prosavage.factionsx.command.engine.CommandInfo
import net.prosavage.factionsx.command.engine.CommandRequirementsBuilder
import net.prosavage.factionsx.command.engine.FCommand
import net.prosavage.factionsx.core.Permission
import net.prosavage.factionsx.gui.perms.PermsRoleMenu
import net.prosavage.factionsx.persist.Message

class CmdPermsRoleMenu : FCommand() {

    init {
        aliases.add("role-menu")

        requiredArgs.add(Argument("role", 0, RolesArgument()))

        commandRequirements = CommandRequirementsBuilder()
                .asLeader(true)
                .withPermission(Permission.PERMS)
                .build()
    }

    override fun execute(info: CommandInfo): Boolean {
        val role = info.getArgAsRole(0) ?: return false
        PermsRoleMenu.getInv(info.faction!!, role)!!.open(info.player)
        return true
    }

    override fun getHelpInfo(): String {
        return Message.commandPermsRoleMenuHelp
    }
}