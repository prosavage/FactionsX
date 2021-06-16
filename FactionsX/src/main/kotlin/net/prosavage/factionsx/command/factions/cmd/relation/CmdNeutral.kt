package net.prosavage.factionsx.command.factions.cmd.relation

import net.prosavage.factionsx.command.engine.CommandInfo
import net.prosavage.factionsx.command.engine.CommandRequirementsBuilder
import net.prosavage.factionsx.command.engine.FCommand
import net.prosavage.factionsx.core.Permission
import net.prosavage.factionsx.persist.Message
import net.prosavage.factionsx.persist.config.EconConfig
import net.prosavage.factionsx.util.MemberAction
import net.prosavage.factionsx.util.Relation

class CmdNeutral : FCommand() {
    init {
        aliases.add("neutral")

        requiredArgs.add(Argument("faction", 0, FactionPlayerArgument()))

        commandRequirements = CommandRequirementsBuilder()
                .withPermission(Permission.NEUTRAL)
                .withMemberAction(MemberAction.RELATION)
                .withPrice(EconConfig.fNeutralCost)
                .build()
    }

    override fun execute(info: CommandInfo): Boolean {
        val trigFaction = info.faction ?: return false
        val faction = info.getArgAsFaction(0, true, informIfNot = false)
                ?: info.getArgAsFPlayer(0, false, informIfNot = false)?.getFaction()
                ?: run {
                    info.message(Message.commandParsingFactionDoesNotExist)
                    return false
                }

        if (faction == info.faction) {
            info.message(Message.commandParsingCannotReferenceYourOwnFaction)
            return false
        }

        if (faction.isSystemFaction()) {
            info.message(Message.genericThisIsASystemFaction)
            return false
        }

        if (trigFaction.getRelationTo(faction) == Relation.NEUTRAL) {
            info.message(Message.relationAlreadySet, Relation.NEUTRAL.tagReplacement, faction.tag)
            return false
        }

        trigFaction.handleRelation(faction, Relation.NEUTRAL, info.fPlayer ?: return false)
        return true
    }

    override fun getHelpInfo(): String {
        return Message.commandNeutralHelp
    }
}