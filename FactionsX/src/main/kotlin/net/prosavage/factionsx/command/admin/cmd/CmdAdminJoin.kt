package net.prosavage.factionsx.command.admin.cmd

import net.prosavage.factionsx.command.engine.CommandInfo
import net.prosavage.factionsx.command.engine.CommandRequirementsBuilder
import net.prosavage.factionsx.command.engine.FCommand
import net.prosavage.factionsx.core.Faction
import net.prosavage.factionsx.core.Permission
import net.prosavage.factionsx.event.FPlayerFactionJoinEvent
import net.prosavage.factionsx.event.FPlayerFactionPreJoinEvent
import net.prosavage.factionsx.persist.Message
import org.bukkit.Bukkit

class CmdAdminJoin : FCommand() {
    init {
        aliases.add("join")

        requiredArgs.add(Argument("faction", 0, FactionPlayerArgument()))

        commandRequirements = CommandRequirementsBuilder()
                .asPlayer(true)
                .withPermission(Permission.ADMIN_JOIN)
                .build()
    }

    override fun execute(info: CommandInfo): Boolean {
        val fPlayer = info.fPlayer ?: return false

        if (fPlayer.hasFaction()) {
            info.message(Message.commandAdminJoinPartOfFaction)
            return false
        }

        val player = info.getArgAsFPlayer(0, true, informIfNot = false)
        val target: Faction = if (player != null && player.hasFaction()) player.getFaction() else info.getArgAsFaction(0)
                ?: return false

        if (target.isSystemFaction()) {
            info.message(Message.commandAdminJoinSystemFaction)
            return false
        }

        FPlayerFactionPreJoinEvent(fPlayer, target, true).let {
            Bukkit.getPluginManager().callEvent(it)
            if (it.isCancelled) return false
        }

        target.addMember(info.fPlayer!!, target.factionRoles.getMinimumRole(), asAdmin = true, silently = fPlayer.isVanished())
        Bukkit.getPluginManager().callEvent(FPlayerFactionJoinEvent(fPlayer, target, true))

        info.message(Message.commandAdminJoinSuccess, target.tag)
        return true
    }

    override fun getHelpInfo(): String {
        return Message.commandAdminJoinHelp
    }
}