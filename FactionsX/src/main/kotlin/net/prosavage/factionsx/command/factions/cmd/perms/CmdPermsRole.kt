package net.prosavage.factionsx.command.factions.cmd.perms

import net.prosavage.factionsx.command.engine.CommandInfo
import net.prosavage.factionsx.command.engine.CommandRequirementsBuilder
import net.prosavage.factionsx.command.engine.FCommand
import net.prosavage.factionsx.core.Permission
import net.prosavage.factionsx.manager.SpecialActionManager
import net.prosavage.factionsx.persist.Message
import net.prosavage.factionsx.util.MemberAction
import net.prosavage.factionsx.util.PlayerAction
import net.prosavage.factionsx.util.SpecialAction
import net.prosavage.factionsx.util.enumValueOrNull

class CmdPermsRole(parentCommand: FCommand) : FCommand() {
    init {
        aliases.add("role")

        requiredArgs.add(Argument("target", 0, RolesArgument()))
        requiredArgs.add(Argument("action", 1, RoleActionArgument()))
        requiredArgs.add(Argument("true/false", 2, BooleanArgument()))

        prefix = parentCommand.prefix

        commandRequirements = CommandRequirementsBuilder()
                .withPermission(Permission.PERMS)
                .asFactionMember(true)
                .asLeader(true)
                .build()
    }

    override fun execute(info: CommandInfo): Boolean {
        val matchedRole = info.getArgAsRole(0) ?: return false
        val actionRaw = info.args[1].toUpperCase()
        val action =
                MemberAction.getFromConfigOptionName(actionRaw) ?: enumValueOrNull<MemberAction>(actionRaw)
                ?: PlayerAction.getFromConfigOptionName(actionRaw) ?: enumValueOrNull<PlayerAction>(actionRaw)
                ?: SpecialActionManager.getActionFromString(actionRaw) ?: run {
                    info.message(Message.commandPermsRoleActionInvalid)
                    return false
                }
        val status = info.getArgAsBoolean(2) ?: run {
            info.message(Message.commandPermsRoleInvalidStatus)
            return false
        }
        info.faction?.factionRoles?.roleHierarchy?.values?.find { role -> role == matchedRole }?.let {
            if (action is MemberAction) if (status) it.allowedMemberActions.add(action) else it.allowedMemberActions.remove(action)
            if (action is PlayerAction) if (status) it.allowedPlayerActions.add(action) else it.allowedPlayerActions.remove(action)
            if (action is SpecialAction) if (status) it.specialActions[action.name] = status
            info.message(Message.commandPermsRoleSuccess, if (status) Message.commandPermsRoleAdded else Message.commandPermsRoleRemoved, it.roleTag, actionRaw)
        } ?: run {
            info.message(Message.commandParsingArgIsNotRole)
            return false
        }
        return true
    }

    override fun getHelpInfo(): String {
        return Message.commandPermsRoleHelp
    }
}