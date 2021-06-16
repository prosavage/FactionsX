package net.prosavage.factionsx.command.factions.cmd.roles

import com.cryptomorin.xseries.XMaterial
import net.prosavage.factionsx.command.engine.CommandInfo
import net.prosavage.factionsx.command.engine.CommandRequirementsBuilder
import net.prosavage.factionsx.command.engine.FCommand
import net.prosavage.factionsx.core.CustomRole
import net.prosavage.factionsx.persist.Message

class CmdRolesAdd : FCommand() {

    init {
        aliases.add("add")

        requiredArgs.add(Argument("chatTag", 0, StringArgument()))
        requiredArgs.add(Argument("roleTag", 1, StringArgument()))
        requiredArgs.add(Argument("iconMaterial", 2, XMaterialArgument()))

        commandRequirements = CommandRequirementsBuilder().asLeader(true).build()
    }

    override fun execute(info: CommandInfo): Boolean {
        val chatTag = info.args[0]
        val roleTag = info.args[1]
        val material = info.args[2]
        val iconMaterial = XMaterial.matchXMaterial(material)
        if (!iconMaterial.isPresent) {
            info.message(Message.commandRolesAddInvalidXMat)
            return false
        }
        val factionRoles = info.faction!!.factionRoles
        val minimumRole = factionRoles.getMinimumRole()
        factionRoles.addRole(CustomRole(
                chatTag,
                roleTag,
                minimumRole.allowedPlayerActions,
                minimumRole.allowedMemberActions,
                minimumRole.specialActions,
                iconMaterial.get()
        ))

        info.message(Message.commandRolesAddSuccess)
        return true
    }

    override fun getHelpInfo(): String {
        return Message.commandRolesAddHelp
    }

}