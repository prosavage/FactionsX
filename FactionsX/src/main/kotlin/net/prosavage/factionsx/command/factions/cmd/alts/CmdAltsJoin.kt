package net.prosavage.factionsx.command.factions.cmd.alts

import net.prosavage.factionsx.command.engine.CommandInfo
import net.prosavage.factionsx.command.engine.CommandRequirementsBuilder
import net.prosavage.factionsx.command.engine.FCommand
import net.prosavage.factionsx.core.Faction
import net.prosavage.factionsx.core.Permission
import net.prosavage.factionsx.event.FPlayerFactionJoinEvent
import net.prosavage.factionsx.persist.Message
import org.bukkit.Bukkit

class CmdAltsJoin : FCommand() {
    init {
        aliases += "join"

        requiredArgs += Argument("faction", 0, FactionPlayerArgument())

        commandRequirements = CommandRequirementsBuilder()
                .asPlayer(true)
                .withPermission(Permission.ALTS_JOIN)
                .build()
    }

    override fun execute(info: CommandInfo): Boolean {
        val fPlayer = info.fPlayer ?: return false

        if (fPlayer.hasFaction()) {
            info.message(Message.commandJoinAlreadyHave)
            return false
        }

        val player = info.getArgAsFPlayer(0, true, informIfNot = false)
        val faction: Faction = if (player != null && player.hasFaction()) player.getFaction() else info.getArgAsFaction(0) ?: return false

        if (faction.isSystemFaction()) {
            info.message(Message.genericThisIsASystemFaction)
            return false
        }

        if (!faction.altsOpen && faction.getInvite(fPlayer) != true) {
            info.message(Message.commandAltsJoinNotInvited, faction.tag)
            return false
        }

        faction.addAlt(fPlayer)
        fPlayer.deInviteFromFaction(faction)
        Bukkit.getPluginManager().callEvent(FPlayerFactionJoinEvent(fPlayer, faction, true))

        info.message(Message.commandAltsJoinSuccess, faction.tag)
        faction.message(Message.commandAltsJoinAnnouncement, info.player?.name ?: "Unknown", excludeFPlayers = listOf(fPlayer))
        return true
    }

    override fun getHelpInfo(): String = Message.commandAltsJoinHelp
}