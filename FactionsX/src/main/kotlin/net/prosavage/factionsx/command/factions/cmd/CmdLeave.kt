package net.prosavage.factionsx.command.factions.cmd

import net.prosavage.factionsx.command.engine.CommandInfo
import net.prosavage.factionsx.command.engine.CommandRequirementsBuilder
import net.prosavage.factionsx.command.engine.FCommand
import net.prosavage.factionsx.core.Permission
import net.prosavage.factionsx.persist.Message

class CmdLeave : FCommand() {
    init {
        aliases.add("leave")

        commandRequirements = CommandRequirementsBuilder()
                .asFactionMember(true)
                .withPermission(Permission.LEAVE)
                .build()
    }

    override fun execute(info: CommandInfo): Boolean {
        val fPlayer = info.fPlayer ?: return false

        if (fPlayer.isLeader()) {
            info.message(Message.commandLeaveAsLeader)
            return false
        }

        val faction = info.faction ?: return false
        if (fPlayer.alt) faction.removeAlt(fPlayer) else faction.removeMember(fPlayer)

        info.message(Message.commandLeaveSuccess)
        faction.message(Message.commandLeaveInform, fPlayer.name)
        return true
    }

    override fun getHelpInfo(): String {
        return Message.commandLeaveHelp
    }
}