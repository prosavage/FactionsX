package net.prosavage.factionsx.command.admin.cmd

import net.prosavage.factionsx.command.engine.CommandInfo
import net.prosavage.factionsx.command.engine.CommandRequirementsBuilder
import net.prosavage.factionsx.command.engine.FCommand
import net.prosavage.factionsx.core.Permission
import net.prosavage.factionsx.event.FactionDisbandEvent
import net.prosavage.factionsx.event.FactionPreDisbandEvent
import net.prosavage.factionsx.manager.FactionManager
import net.prosavage.factionsx.persist.Message
import org.bukkit.Bukkit

class CmdAdminDisband : FCommand() {
    init {
        aliases.add("disband")
        aliases.add("delete")

        requiredArgs.add(Argument("target", 0, FactionArgument()))

        commandRequirements = CommandRequirementsBuilder()
                .withPermission(Permission.ADMIN_DISBAND)
                .build()
    }

    override fun execute(info: CommandInfo): Boolean {
        val target = info.getArgAsFaction(0, false) ?: return false
        val fPlayer = info.fPlayer

        if (target.isSystemFaction()) {
            info.message(Message.commandAdminDisbandSystemFac)
            return false
        }

        FactionPreDisbandEvent(target, fPlayer, true).let {
            Bukkit.getPluginManager().callEvent(it)
            if (it.isCancelled) return false
        }

        FactionManager.deleteFaction(target)
        Bukkit.getPluginManager().callEvent(FactionDisbandEvent(target, fPlayer, true))

        target.message(Message.commandAdminDisbandNotify)
        info.message(Message.commandAdminDisbandSuccess, target.tag)
        return true
    }

    override fun getHelpInfo(): String {
        return Message.commandAdminDisbandHelp
    }
}