package net.prosavage.factionsx.command.factions.cmd.claim

import net.prosavage.factionsx.command.engine.CommandInfo
import net.prosavage.factionsx.command.engine.CommandRequirementsBuilder
import net.prosavage.factionsx.command.engine.FCommand
import net.prosavage.factionsx.core.Permission
import net.prosavage.factionsx.manager.GridManager
import net.prosavage.factionsx.persist.Message
import net.prosavage.factionsx.persist.config.Config
import net.prosavage.factionsx.persist.data.getFLocation
import net.prosavage.factionsx.util.MemberAction

class CmdUnClaim : FCommand() {
    init {
        aliases.add("unclaim")

        optionalArgs.add(Argument("radius", 0, IntArgument()))

        commandRequirements = CommandRequirementsBuilder()
                .withMemberAction(MemberAction.UNCLAIM)
                .withPermission(Permission.UNCLAIM)
                .build()
    }

    override fun execute(info: CommandInfo): Boolean {
        val location = info.player!!.location
        val fplayer = info.fPlayer!!
        val faction = info.faction!!
        if (info.args.isEmpty()) {
            GridManager.unClaimLand(faction, getFLocation(location), fplayer = fplayer)
            return false
        }
        val radius = info.getArgAsInt(0) ?: return false
        if (radius > Config.maxClaimRadius) {
            info.message(Message.commandUnClaimMaxRadius, Config.maxClaimRadius.toString())
            return false
        }
        GridManager.unclaimSpiral(radius, fplayer, faction, getFLocation(location))
        return true
    }

    override fun getHelpInfo(): String {
        return Message.commandUnclaimHelp
    }
}