package net.prosavage.factionsx.command.factions.cmd

import net.prosavage.factionsx.command.engine.CommandInfo
import net.prosavage.factionsx.command.engine.CommandRequirementsBuilder
import net.prosavage.factionsx.command.engine.FCommand
import net.prosavage.factionsx.core.Permission
import net.prosavage.factionsx.event.FactionUpdateHomeEvent
import net.prosavage.factionsx.manager.GridManager
import net.prosavage.factionsx.persist.Message
import net.prosavage.factionsx.persist.config.EconConfig
import net.prosavage.factionsx.persist.data.wrappers.getDataLocation
import net.prosavage.factionsx.util.MemberAction
import org.bukkit.Bukkit

class CmdSetHome : FCommand() {
    init {
        aliases.add("sethome")

        commandRequirements = CommandRequirementsBuilder()
                .withMemberAction(MemberAction.SETHOME)
                .withPrice(EconConfig.fSetHomeCost)
                .withPermission(Permission.SETHOME)
                .build()
    }

    override fun execute(info: CommandInfo): Boolean {
        val player = info.player!!
        val factionAt = GridManager.getFactionAt(player.location.chunk)
        val faction = info.faction!!

        if (factionAt != faction) {
            info.message(Message.commandSetHomeOwnClaim)
            return false
        }

        val location = player.location
        val homeLoc = location.getDataLocation(withYaw = true, withPitch = true)

        val event = FactionUpdateHomeEvent(faction, location)
        Bukkit.getPluginManager().callEvent(event)

        if (event.isCancelled) return false
        faction.home = homeLoc

        info.message(Message.commandSetHomeSuccess)
        val fplayer = info.fPlayer!!
        faction.message(Message.commandSetHomeNotif, fplayer.name, excludeFPlayers = listOf(fplayer))
        return true
    }

    override fun getHelpInfo(): String {
        return Message.commandSetHomeHelp
    }
}