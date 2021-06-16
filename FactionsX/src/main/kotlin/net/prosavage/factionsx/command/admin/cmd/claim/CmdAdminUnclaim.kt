package net.prosavage.factionsx.command.admin.cmd.claim

import net.prosavage.factionsx.command.engine.CommandInfo
import net.prosavage.factionsx.command.engine.CommandRequirementsBuilder
import net.prosavage.factionsx.command.engine.FCommand
import net.prosavage.factionsx.core.Permission
import net.prosavage.factionsx.manager.GridManager
import net.prosavage.factionsx.persist.Message
import net.prosavage.factionsx.persist.data.getFLocation

class CmdAdminUnclaim : FCommand() {
    init {
        aliases.add("unclaim")

        requiredArgs.add(Argument("radius", 0, IntArgument()))
        requiredArgs.add(Argument("faction", 1, AllFactionsArgument()))

        commandRequirements = CommandRequirementsBuilder()
                .withPermission(Permission.ADMIN_UNCLAIM)
                .asPlayer(true)
                .build()
    }


    override fun execute(info: CommandInfo): Boolean {
        val radius = info.getArgAsInt(0) ?: return false
        if (radius <= 0) {
            info.message(Message.commandAdminUnClaimNonNegativeRadius)
            return false
        }
        val faction = info.getArgAsFaction(1, false) ?: return false
        GridManager.unclaimSpiral(radius, info.fPlayer!!, faction, getFLocation(info.player!!.location), true)
        return true
    }

    override fun getHelpInfo(): String {
        return Message.commandAdminUnClaimHelp
    }
}