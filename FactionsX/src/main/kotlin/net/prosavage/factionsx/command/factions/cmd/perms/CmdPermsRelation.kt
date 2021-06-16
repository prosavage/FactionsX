package net.prosavage.factionsx.command.factions.cmd.perms

import net.prosavage.factionsx.command.engine.CommandInfo
import net.prosavage.factionsx.command.engine.CommandRequirementsBuilder
import net.prosavage.factionsx.command.engine.FCommand
import net.prosavage.factionsx.core.Permission
import net.prosavage.factionsx.persist.Message
import net.prosavage.factionsx.util.PlayerAction
import net.prosavage.factionsx.util.Relation
import net.prosavage.factionsx.util.enumValueOrNull

class CmdPermsRelation(parentCommand: FCommand) : FCommand() {
    init {
        aliases.add("relation")

        requiredArgs.add(Argument("target", 0, PermsRelationArgument()))
        requiredArgs.add(Argument("action", 1, PermsActionArgument()))
        requiredArgs.add(Argument("true/false", 2, BooleanArgument()))

        prefix = parentCommand.prefix

        commandRequirements = CommandRequirementsBuilder()
                .withPermission(Permission.PERMS)
                .asFactionMember(true)
                .asLeader(true)
                .build()
    }

    override fun execute(info: CommandInfo): Boolean {
        val target = info.args[0].toUpperCase()
        val relation = enumValueOrNull<Relation>(target) ?: run {
            info.message(Message.genericDoesNotExist, target)
            return false
        }
        val actionRaw = info.args[1].toUpperCase()
        val action = PlayerAction.getFromConfigOptionName(actionRaw) ?: enumValueOrNull(actionRaw) ?: run {
            info.message(Message.genericDoesNotExist, info.args[1])
            return false
        }
        val status = info.getArgAsBoolean(2) ?: return false


        if (action.isLocked(relation)) {
            info.message(Message.commandPermsRelationOverriden, info.faction!!.relationPerms.getPermForRelation(relation, action).toString())
            return false
        }
        info.faction?.relationPerms?.setPermForRelation(relation, action, status)
        info.message(Message.commandPermsRelationSuccess, action.name, status.toString(), relation.name)
        return true
    }

    override fun getHelpInfo(): String {
        return Message.commandPermsRelationHelp
    }
}