package net.prosavage.factionsx.command.admin.cmd.claim

import net.prosavage.factionsx.command.engine.CommandInfo
import net.prosavage.factionsx.command.engine.CommandRequirementsBuilder
import net.prosavage.factionsx.command.engine.FCommand
import net.prosavage.factionsx.core.Permission
import net.prosavage.factionsx.manager.GridManager
import net.prosavage.factionsx.persist.Message
import net.prosavage.factionsx.persist.data.getFLocation

class CmdAdminClaim : FCommand() {
    init {
        aliases.add("claim")

        requiredArgs.add(Argument("faction", 0, AllFactionsArgument()))
        optionalArgs.add(Argument("radius", 1, IntArgument()))

        commandRequirements = CommandRequirementsBuilder()
                .withPermission(Permission.ADMIN_CLAIM)
                .asPlayer(true)
                .build()
    }


    override fun execute(info: CommandInfo): Boolean {
        val radius = if (info.args.size < 2) 1 else info.getArgAsInt(1) ?: return false

        if (radius <= 0) {
            info.message(Message.commandAdminClaimNonNegativeRadius)
            return false
        }

        val faction = info.getArgAsFaction(0, false) ?: return false
        GridManager.claimSpiral(radius, info.fPlayer!!, faction, getFLocation(info.player!!.location), true)
        return true
    }

    override fun getHelpInfo(): String {
        return Message.commandAdminClaimHelp
    }
}