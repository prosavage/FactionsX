package net.prosavage.factionsx.command.factions.cmd

import net.prosavage.factionsx.command.engine.CommandInfo
import net.prosavage.factionsx.command.engine.CommandRequirementsBuilder
import net.prosavage.factionsx.command.engine.FCommand
import net.prosavage.factionsx.core.Permission
import net.prosavage.factionsx.persist.Message
import net.prosavage.factionsx.util.MemberAction

class CmdPrefix : FCommand() {
    init {
        aliases.add("prefix")
        aliases.add("title")

        requiredArgs.add(Argument("target", 0, PlayerArgument()))
        optionalArgs.add(Argument("prefix", 1, StringArgument()))

        commandRequirements = CommandRequirementsBuilder()
                .withPermission(Permission.PREFIX)
                .withMemberAction(MemberAction.PREFIX)
                .build()
    }

    override fun execute(info: CommandInfo): Boolean {
        val target = info.getArgAsFPlayer(0, false) ?: return false
        if (target.getFaction() != info.faction) {
            info.message(Message.genericNotInYourFaction)
            return false
        }

        val newPrefix = info.args.getOrNull(1) ?: run {
            target.prefix = ""
            info.message(Message.commandPrefixCleared, target.name)
            target.message(Message.commandPrefixRecipientCleared)
            return false
        }

        target.prefix = newPrefix
        info.message(Message.commandPrefixSet, target.name, newPrefix)
        target.message(Message.commandPrefixRecipientSet, newPrefix)
        return true
    }

    override fun getHelpInfo(): String {
        return Message.commandPrefixHelp
    }
}