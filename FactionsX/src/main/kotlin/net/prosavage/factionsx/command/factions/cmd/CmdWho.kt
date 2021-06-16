package net.prosavage.factionsx.command.factions.cmd

import net.prosavage.factionsx.command.engine.CommandInfo
import net.prosavage.factionsx.command.engine.CommandRequirementsBuilder
import net.prosavage.factionsx.command.engine.FCommand
import net.prosavage.factionsx.core.Permission
import net.prosavage.factionsx.manager.FactionManager
import net.prosavage.factionsx.persist.Message
import net.prosavage.factionsx.persist.config.Config

class CmdWho : FCommand() {
    init {
        aliases.add("who")
        aliases.add("show")
        aliases.add("faction")
        aliases.add("f")

        optionalArgs.add(Argument("player/faction", 0, FactionPlayerArgument()))

        commandRequirements = CommandRequirementsBuilder()
                .withPermission(Permission.WHO)
                .asPlayer(true)
                .build()
    }

    override fun execute(info: CommandInfo): Boolean {
        var targetFaction = info.getArgAsFaction(0, false, informIfNot = false)
        if (targetFaction == null) targetFaction = info.getArgAsFPlayer(0, false, informIfNot = false, offline = true)?.getFaction()
                ?: info.faction ?: FactionManager.getWilderness()
        if (!targetFaction.isWilderness() && targetFaction.isSystemFaction()) {
            info.message(Message.commandWhoNoSystemFactions, Config.sendPrefixForWhoCommand)
            return false
        }
        targetFaction.sendFactionInfo(info.fPlayer!!)
        return true
    }

    override fun getHelpInfo(): String {
        return Message.commandWhoHelp
    }
}