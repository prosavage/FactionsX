package net.prosavage.factionsx.command.factions.cmd.claim

import net.prosavage.factionsx.command.engine.CommandInfo
import net.prosavage.factionsx.command.engine.CommandRequirementsBuilder
import net.prosavage.factionsx.command.engine.FCommand
import net.prosavage.factionsx.core.Permission
import net.prosavage.factionsx.manager.GridManager
import net.prosavage.factionsx.persist.Message
import net.prosavage.factionsx.persist.config.MapConfig
import net.prosavage.factionsx.persist.data.FLocation
import net.prosavage.factionsx.persist.data.getFLocation
import net.prosavage.factionsx.util.MemberAction

class CmdClaimAt : FCommand() {
    init {
        aliases.add("claimat")

        this.requiredArgs.add(Argument("x", 0, IntArgument()))
        this.requiredArgs.add(Argument("z", 1, IntArgument()))

        commandRequirements = CommandRequirementsBuilder()
                .withPermission(Permission.CLAIM_AT)
                .withMemberAction(MemberAction.CLAIM)
                .build()
    }

    override fun execute(info: CommandInfo): Boolean {
        val x = info.getArgAsInt(0) ?: return false
        val z = info.getArgAsInt(1) ?: return false
        val here = getFLocation(info.player!!.location)
        val mapRowsToUse = MapConfig.mapRows / 2
        val mapWidthToUse = MapConfig.mapWidth / 2
        val start = FLocation(here.x - mapWidthToUse, here.z + mapRowsToUse, here.world)
        val end = FLocation(here.x + mapWidthToUse, here.z - mapRowsToUse, here.world)
        val xStart = if (start.x > end.x) end.x else start.x
        val zStart = if (start.z > end.z) end.z else start.z
        val xEnd = if (start.x < end.x) end.x else start.x
        val zEnd = if (start.z < end.z) end.z else start.z
        if (x < xStart || x > xEnd || z < zStart || z > zEnd) {
            info.message(Message.commandClaimAtExploit)
            return false
        }
        val beforeSize = info.faction!!.claimAmt
        GridManager.claimLand(info.faction!!, FLocation(x.toLong(), z.toLong(), info.player!!.world.name), fplayer = info.fPlayer!!)
        // Only update if success.
        if (info.faction!!.claimAmt > beforeSize) info.fPlayer!!.sendMap()
        return true
    }

    override fun getHelpInfo(): String {
        return Message.commandClaimAtHelp
    }
}