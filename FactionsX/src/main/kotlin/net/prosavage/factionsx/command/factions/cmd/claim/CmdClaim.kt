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

class CmdClaim : FCommand() {
    init {
        aliases.add("claim")

        optionalArgs.add(Argument("radius", 0, IntArgument()))

        commandRequirements = CommandRequirementsBuilder()
                .withPermission(Permission.CLAIM)
                .withMemberAction(MemberAction.CLAIM)
                .build()
    }

    override fun execute(info: CommandInfo): Boolean {
        val location = info.player!!.location
        val fplayer = info.fPlayer!!
        val faction = info.faction!!
        if (info.args.isEmpty()) {
            GridManager.claimLand(faction, getFLocation(location), fplayer = fplayer)
            return false
        }
        val radius = info.getArgAsInt(0) ?: return false
        if (fplayer.inBypass.not() && radius > Config.maxClaimRadius) {
            info.message(Message.commandClaimMaxRadius, Config.maxClaimRadius.toString())
            return false
        }
        GridManager.claimSpiral(radius, fplayer, faction, getFLocation(location))
        return true
    }

    override fun getHelpInfo(): String {
        return Message.commandClaimHelp
    }
}