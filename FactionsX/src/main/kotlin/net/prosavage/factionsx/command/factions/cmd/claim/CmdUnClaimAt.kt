package net.prosavage.factionsx.command.factions.cmd.claim

import net.prosavage.factionsx.command.engine.CommandInfo
import net.prosavage.factionsx.command.engine.CommandRequirementsBuilder
import net.prosavage.factionsx.command.engine.FCommand
import net.prosavage.factionsx.core.Permission
import net.prosavage.factionsx.manager.GridManager
import net.prosavage.factionsx.persist.Message
import net.prosavage.factionsx.persist.data.FLocation
import net.prosavage.factionsx.util.MemberAction

class CmdUnClaimAt : FCommand() {
    init {
        aliases.add("unclaimat")

        this.requiredArgs.add(Argument("x", 0, IntArgument()))
        this.requiredArgs.add(Argument("z", 1, IntArgument()))

        commandRequirements = CommandRequirementsBuilder()
                .withPermission(Permission.UNCLAIM)
                .withMemberAction(MemberAction.UNCLAIM)
                .build()
    }

    override fun execute(info: CommandInfo): Boolean {
        val fPlayer = info.fPlayer ?: return false
        val faction = info.faction ?: return false

        val x = info.getArgAsInt(0) ?: return false
        val z = info.getArgAsInt(1) ?: return false
        val beforeSize = info.faction!!.claimAmt

        GridManager.unClaimLand(faction, FLocation(x.toLong(), z.toLong(), info.player!!.world.name), fplayer = fPlayer)
        if (beforeSize < faction.claimAmt) fPlayer.sendMap()
        return true
    }

    override fun getHelpInfo(): String {
        return Message.commandClaimAtHelp
    }
}