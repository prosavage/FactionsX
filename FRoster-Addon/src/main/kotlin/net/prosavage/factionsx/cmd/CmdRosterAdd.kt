package net.prosavage.factionsx.cmd

import net.prosavage.factionsx.command.engine.CommandInfo
import net.prosavage.factionsx.command.engine.CommandRequirementsBuilder
import net.prosavage.factionsx.command.engine.FCommand
import net.prosavage.factionsx.persist.RosterConfig
import net.prosavage.factionsx.persist.getRoster

class CmdRosterAdd : FCommand() {
    init {
        aliases.add("add")

        requiredArgs.add(Argument("player", 0, PlayerArgument()))

        requiredArgs.add(Argument("role", 1, RolesArgument()))

        commandRequirements = CommandRequirementsBuilder()
                .asFactionMember(true)
                .asLeader(true)
                .withRawPermission(RosterConfig.rosterAddPermission)
                .asPlayer(true)
                .build()
    }

    override fun execute(info: CommandInfo): Boolean {
        val member = info.getArgAsFPlayer(0) ?: return false
        val role = info.getArgAsRole(1) ?: return false
        // Deny adding leaders.
        val faction = info.faction!!
        if (faction.factionRoles.getApexRole() == role) {
            info.message(RosterConfig.rosterAddApex, role.roleTag)
            return false
        }
        val roster = faction.getRoster()
        if (!roster.canAddMember()) {
            info.message(RosterConfig.rosterAddFull)
            return false
        }
        if (roster.hasMember(member)) {
            info.message(RosterConfig.rosterAddAdded, member.name)
            return false
        }
        roster.addMember(member, role)
        info.message(RosterConfig.rosterAddMessage, member.name, role.roleTag)
        member.message(RosterConfig.rosterAddHaveBeenAdded, faction.tag)
        return true
    }

    override fun getHelpInfo(): String {
        return RosterConfig.rosterAddHelp
    }

}