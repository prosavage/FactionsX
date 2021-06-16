package net.prosavage.factionsx.cmd

import net.prosavage.factionsx.command.engine.CommandInfo
import net.prosavage.factionsx.command.engine.CommandRequirementsBuilder
import net.prosavage.factionsx.command.engine.FCommand
import net.prosavage.factionsx.persist.RosterConfig
import net.prosavage.factionsx.persist.getRoster

class CmdRosterList : FCommand() {
    init {
        aliases.add("list")

        commandRequirements = CommandRequirementsBuilder()
                .asFactionMember(true)
                .withRawPermission(RosterConfig.rosterListPermission)
                .asPlayer(true)
                .build()
    }

    override fun execute(info: CommandInfo): Boolean {
        info.message(RosterConfig.rosterListHeader)
        for ((index, member) in info.faction!!.getRoster().getMembers().withIndex()) {
            info.message(RosterConfig.rosterListEntry, index.toString(), member.getFPlayer().name, member.roleTag)
        }
        return true
    }

    override fun getHelpInfo(): String {
        return RosterConfig.rosterListHelp
    }

}