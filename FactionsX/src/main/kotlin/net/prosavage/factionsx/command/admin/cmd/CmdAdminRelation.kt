package net.prosavage.factionsx.command.admin.cmd

import net.prosavage.factionsx.command.engine.CommandInfo
import net.prosavage.factionsx.command.engine.FCommand
import net.prosavage.factionsx.core.Permission
import net.prosavage.factionsx.manager.FactionManager
import net.prosavage.factionsx.persist.Message

/**
 * This class refers to the command '/fx <rel|relation>'.
 */
class CmdAdminRelation : FCommand() {
    /** Initialize the base of the command. **/
    init {
        aliases += "relation"
        aliases += "rel"

        with(FactionArgument()) {
            requiredArgs += Argument("faction", 0, this)
            requiredArgs += Argument("faction", 1, this)
            requiredArgs += Argument("relation", 2, RelationArgument())
        }

        commandRequirements = buildRequirements {
            this.permission = Permission.ADMIN_RELATION
        }
    }

    /** Handle the execution of the command. **/
    override fun execute(info: CommandInfo): Boolean = with(info) {
        val first = getArgAsFaction(0, cannotReferenceYourSelf = false) ?: return false
        val second = getArgAsFaction(1, cannotReferenceYourSelf = false) ?: return false
        val relation = getArgAsRelation(2) ?: return false

        if (first == second) {
            message(Message.commandAdminRelationFactionsEqual)
            return false
        }

        if (first.isSystemFaction() || second.isSystemFaction()) {
            message(Message.commandAdminRelationSystemFaction)
            return false
        }

        val currentRelation = first.getRelationTo(second)
        val firstTag = first.tag
        val secondTag = second.tag
        val relationTag = relation.tagReplacement

        if (currentRelation == relation) {
            message(Message.commandAdminRelationAlready, firstTag, secondTag, relationTag)
            return false
        }

        FactionManager.forceRelation(first, second, relation)
        message(Message.commandAdminRelationSet, firstTag, secondTag, relationTag)
        return true
    }

    /** String (description) to be shown upon help. **/
    override fun getHelpInfo(): String = Message.commandAdminRelationHelp
}