package net.prosavage.factionsx.command.factions.cmd.claim

import net.prosavage.factionsx.command.engine.CommandInfo
import net.prosavage.factionsx.command.engine.CommandRequirementsBuilder
import net.prosavage.factionsx.command.engine.FCommand
import net.prosavage.factionsx.core.Permission
import net.prosavage.factionsx.event.FactionUnClaimAllEvent
import net.prosavage.factionsx.manager.GridManager
import net.prosavage.factionsx.persist.Message
import net.prosavage.factionsx.util.MemberAction
import org.bukkit.Bukkit

class CmdUnclaimAll : FCommand() {
    init {
        aliases.add("unclaimall")

        commandRequirements = CommandRequirementsBuilder()
                .asFactionMember(true)
                .withPermission(Permission.UNCLAIMALL)
                .withMemberAction(MemberAction.UNCLAIMALL)
                .build()
    }

    override fun execute(info: CommandInfo): Boolean {
        val faction = info.faction ?: return false
        val allClaims = GridManager.getAllClaims(faction)
        allClaims.forEach { claim -> GridManager.unclaim(faction, claim) }

        Bukkit.getPluginManager().callEvent(FactionUnClaimAllEvent(faction, allClaims, info.fPlayer!!, false))
        faction.message(Message.commandUnClaimAll, info.player!!.name)
        return true
    }

    override fun getHelpInfo(): String {
        return Message.commandUnClaimAllHelp
    }
}