package net.prosavage.factionsx.cmd

import net.prosavage.factionsx.command.engine.CommandInfo
import net.prosavage.factionsx.command.engine.CommandRequirementsBuilder
import net.prosavage.factionsx.command.engine.FCommand
import net.prosavage.factionsx.persist.RosterConfig
import net.prosavage.factionsx.persist.getRoster

class CmdRosterKick : FCommand() {
    init {
        aliases.add("kick")

        requiredArgs.add(Argument("player", 0, PlayerArgument()))

        commandRequirements = CommandRequirementsBuilder()
                .asFactionMember(true)
                .asLeader(true)
                .withRawPermission(RosterConfig.rosterRemovePermission)
                .asPlayer(true)
                .build()
    }

    override fun execute(info: CommandInfo): Boolean {
        val member = info.getArgAsFPlayer(0) ?: return false
        info.faction!!.getRoster().removeMember(member)
        info.message(RosterConfig.rosterRemoveMessage, member.name)
        return true
    }

    override fun getHelpInfo(): String {
        return RosterConfig.rosterAddHelp
    }

}