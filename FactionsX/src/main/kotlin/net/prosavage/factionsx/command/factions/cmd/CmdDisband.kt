package net.prosavage.factionsx.command.factions.cmd

import net.prosavage.factionsx.FactionsX
import net.prosavage.factionsx.command.engine.CommandInfo
import net.prosavage.factionsx.command.engine.CommandRequirementsBuilder
import net.prosavage.factionsx.command.engine.ConfirmAction
import net.prosavage.factionsx.command.engine.FCommand
import net.prosavage.factionsx.core.Permission
import net.prosavage.factionsx.event.ConfirmationEvent
import net.prosavage.factionsx.event.FactionDisbandEvent
import net.prosavage.factionsx.event.FactionPreDisbandEvent
import net.prosavage.factionsx.manager.FactionManager
import net.prosavage.factionsx.persist.Message
import net.prosavage.factionsx.persist.config.Config
import net.prosavage.factionsx.util.MemberAction
import org.bukkit.Bukkit

class CmdDisband : FCommand() {
    init {
        aliases.add("disband")
        aliases.add("delete")

        commandRequirements = CommandRequirementsBuilder()
                .withPermission(Permission.DISBAND)
                .withMemberAction(MemberAction.DISBAND)
                .build()
    }

    override fun execute(info: CommandInfo): Boolean {
        val fplayer = info.fPlayer!!
        val faction = info.faction ?: return false

        if (Config.disbandRequireConfirmation) {
            if (!fplayer.confirmAction.hasConfirmedAction(ConfirmAction.DISBAND)) {
                info.message(Message.commandDisbandConfirmation, Config.confirmTimeOutSeconds.toString())

                val confirmationEvent = ConfirmationEvent(fplayer.getFaction(), fplayer, ConfirmAction.DISBAND)
                Bukkit.getPluginManager().callEvent(confirmationEvent)

                fplayer.startConfirmProcess(FactionsX.instance, ConfirmAction.DISBAND)
                return false
            }

            fplayer.confirmAction.clearConfirmation()
        }

        FactionPreDisbandEvent(faction, fplayer, false).let {
            Bukkit.getPluginManager().callEvent(it)
            if (it.isCancelled) return false
        }

        FactionManager.deleteFaction(faction)
        Bukkit.getPluginManager().callEvent(FactionDisbandEvent(faction, fplayer, false))

        info.message(String.format(Message.commandDisbandSuccess))
        return true
    }

    override fun getHelpInfo(): String {
        return Message.commandDisbandHelp
    }
}