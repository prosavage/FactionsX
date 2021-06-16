package net.prosavage.factionsx.command.admin.cmd.claim

import net.prosavage.factionsx.command.engine.CommandInfo
import net.prosavage.factionsx.command.engine.CommandRequirementsBuilder
import net.prosavage.factionsx.command.engine.FCommand
import net.prosavage.factionsx.core.Permission
import net.prosavage.factionsx.event.FactionUnClaimAllEvent
import net.prosavage.factionsx.manager.GridManager
import net.prosavage.factionsx.persist.Message
import org.bukkit.Bukkit


class CmdAdminUnclaimAll : FCommand() {
    init {
        aliases.add("unclaimall")

        requiredArgs.add(Argument("faction", 0, AllFactionsArgument()))

        commandRequirements = CommandRequirementsBuilder()
                .asPlayer(true)
                .withPermission(Permission.ADMIN_UNCLAIMALL)
                .build()
    }

    override fun execute(info: CommandInfo): Boolean {
        val faction = info.getArgAsFaction(0, false) ?: return false
        val allClaims = GridManager.getAllClaims(faction)
        allClaims.forEach { claim -> GridManager.unclaim(faction, claim) }
        Bukkit.getPluginManager().callEvent(FactionUnClaimAllEvent(faction, allClaims, info.fPlayer!!, true))
        faction.message(Message.commandAdminUnClaimAll, info.player!!.name)
        info.fPlayer!!.message(Message.commandAdminUnclaimAllSuccess, faction.tag)
        return true
    }

    override fun getHelpInfo(): String {
        return Message.commandAdminUnClaimAllHelp
    }
}