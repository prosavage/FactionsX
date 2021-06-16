package net.prosavage.factionsx.command.factions.cmd

import net.prosavage.factionsx.command.engine.CommandInfo
import net.prosavage.factionsx.command.engine.FCommand
import net.prosavage.factionsx.core.Permission
import net.prosavage.factionsx.manager.FactionManager
import net.prosavage.factionsx.persist.Message
import net.prosavage.factionsx.persist.config.Config

class CmdList : FCommand() {

    init {
        aliases.add("list")

        optionalArgs.add(Argument("index", 0, IntArgument()))

        commandRequirements = net.prosavage.factionsx.command.engine.CommandRequirementsBuilder()
                .asPlayer(true)
                .withPermission(Permission.LIST)
                .build()
    }


    override fun execute(info: CommandInfo): Boolean {
        val factionsSorted = FactionManager.getFactions().filter { faction -> !faction.isSystemFaction() }
                .sortedByDescending { faction -> faction.getMembers().size }
                .sortedByDescending { faction -> faction.getOnlineMembers().size }.toMutableList()

        if (factionsSorted.isEmpty()) {
            info.message(Message.commandListEmpty)
            return false
        }
        info.message(Message.commandListHeader)
        var index = 1
        if (info.args.isNotEmpty()) {
            index = info.getArgAsInt(0) ?: return false
            if (index <= 0) {
                info.message(Message.commandListTooLow)
                return false
            }
        }
        val startIndex = (index - 1) * Config.listCommandPageRows
        if (startIndex > factionsSorted.size - 1) {
            info.message(Message.commandListTooHighIndex)
            return false
        }
        for (factionIndex in startIndex..startIndex + Config.listCommandPageRows) {
            val faction = factionsSorted.getOrNull(factionIndex) ?: break
            info.message(Message.commandListFormat,
                    (factionIndex + 1).toString(),
                    faction.tag,
                    faction.getOnlineMembers().size.toString(),
                    faction.getMembers().size.toString())
        }
        return true
    }

    override fun getHelpInfo(): String {
        return Message.commandListHelp
    }
}