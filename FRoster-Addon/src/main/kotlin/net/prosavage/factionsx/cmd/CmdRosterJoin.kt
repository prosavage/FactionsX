package net.prosavage.factionsx.cmd

import net.prosavage.factionsx.command.engine.CommandInfo
import net.prosavage.factionsx.command.engine.CommandRequirementsBuilder
import net.prosavage.factionsx.command.engine.FCommand
import net.prosavage.factionsx.persist.RosterConfig
import net.prosavage.factionsx.persist.getRoster

class CmdRosterJoin : FCommand() {

    init {
        aliases.add("join")

        requiredArgs.add(Argument("faction", 0, FactionArgument()))

        this.commandRequirements = CommandRequirementsBuilder()
                .asPlayer(true)
                .build()
    }


    override fun execute(info: CommandInfo): Boolean {
        // We do not have a command requirement for factionless players, since we rarely
        // need it.
        val fplayer = info.fPlayer!!
        if (fplayer.hasFaction()) {
            info.message(RosterConfig.rosterJoinAlreadyHave)
            return false
        }

        val faction = info.getArgAsFaction(0) ?: return false

        val roster = faction.getRoster()
        if (roster.hasMember(fplayer).not()) {
            info.message(RosterConfig.rosterJoinNotInRoster)
            return false
        }

        val member = roster.getMember(fplayer)!!

        val factionRoles = faction.factionRoles
        val role = member.getRole(faction) ?: run {
            info.message(RosterConfig.rosterJoinRoleDoesNotExist)
            factionRoles.getMinimumRole()
        }

        // check if faction is full, if not, just add them and move on.
        if (faction.getMembers().size < faction.getMaxMembers()) {
            faction.addMember(fplayer, role)
            info.message(RosterConfig.rosterJoinDefault, faction.tag)
            return true
        }



        for (factionMember in faction.getMembers()) {
            if (factionMember.isOnline() || factionMember.isLeader()) continue
            // this member is okay to be removed since offline and not leader.
            faction.removeMember(factionMember)
            // add our member.
            faction.message(RosterConfig.rosterJoinSuccess, fplayer.name)
            faction.addMember(fplayer, role)
            info.message(RosterConfig.rosterJoinRemoved, factionMember.name)
            return true
        }


        // if we finish the loop there were no eligible replacements
        info.message(RosterConfig.rosterJoinFailed)
        return false
    }

    override fun getHelpInfo(): String {
        return RosterConfig.rosterJoinHelp
    }
}