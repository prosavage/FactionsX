package net.prosavage.factionsx.command.factions.cmd

import net.prosavage.factionsx.FactionsX
import net.prosavage.factionsx.command.engine.CommandInfo
import net.prosavage.factionsx.command.engine.CommandRequirementsBuilder
import net.prosavage.factionsx.command.engine.FCommand
import net.prosavage.factionsx.command.factions.cmd.alts.CmdAlts
import net.prosavage.factionsx.command.factions.cmd.alts.CmdAltsJoin
import net.prosavage.factionsx.core.Faction
import net.prosavage.factionsx.core.Permission
import net.prosavage.factionsx.event.FPlayerFactionJoinEvent
import net.prosavage.factionsx.event.FPlayerFactionPreJoinEvent
import net.prosavage.factionsx.persist.Message
import org.bukkit.Bukkit

class CmdJoin : FCommand() {
    init {
        aliases.add("join")

        requiredArgs.add(Argument("faction-tag", 0, FactionPlayerArgument()))

        commandRequirements = CommandRequirementsBuilder()
                .asPlayer(true)
                .withPermission(Permission.JOIN)
                .build()
    }

    override fun execute(info: CommandInfo): Boolean {
        val fPlayer = info.fPlayer ?: return false

        if (fPlayer.hasFaction()) {
            info.message(Message.commandJoinAlreadyHave)
            return false
        }

        val player = info.getArgAsFPlayer(0, true, informIfNot = false)
        val faction: Faction = if (player != null && player.hasFaction()) player.getFaction() else info.getArgAsFaction(0)
                ?: return false

        if (faction.isSystemFaction()) {
            info.message(Message.genericThisIsASystemFaction)
            info.message(Message.commandJoinSuggest)
            return false
        }

        if (faction.isClosed() && faction.isInvited(fPlayer).not()) {
            info.message(Message.commandJoinNotInvited, faction.tag)
            return false
        }


        val asAlt = faction.isInvitedAsAlt(fPlayer)
        if (asAlt != null && asAlt) {
            // we are invited as an alt, not a member, reroute command.
            FactionsX.baseCommand.subCommands.find { it is CmdAlts }!!
                    .subCommands.find { it is CmdAltsJoin }!!.execute(info)
            return false
        }

        FPlayerFactionPreJoinEvent(fPlayer, faction, false).let {
            Bukkit.getPluginManager().callEvent(it)
            if (it.isCancelled) return false
        }

        faction.addMember(fPlayer, faction.factionRoles.getMinimumRole())
        fPlayer.deInviteFromFaction(faction)
        Bukkit.getPluginManager().callEvent(FPlayerFactionJoinEvent(fPlayer, faction, false))

        info.message(Message.commandJoinSuccess, faction.tag)
        faction.message(Message.commandJoinAnnouncement, info.player!!.name, excludeFPlayers = listOf(fPlayer))
        return true
    }

    override fun getHelpInfo(): String {
        return Message.commandJoinHelp
    }
}