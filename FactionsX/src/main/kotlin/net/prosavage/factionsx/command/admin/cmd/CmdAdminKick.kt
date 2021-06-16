package net.prosavage.factionsx.command.admin.cmd

import net.prosavage.factionsx.command.engine.CommandInfo
import net.prosavage.factionsx.command.engine.CommandRequirementsBuilder
import net.prosavage.factionsx.command.engine.FCommand
import net.prosavage.factionsx.core.Permission
import net.prosavage.factionsx.persist.Message

class CmdAdminKick : FCommand() {
    init {
        aliases.add("kick")

        requiredArgs.add(Argument("player", 0, FactionMemberArgument()))

        commandRequirements = CommandRequirementsBuilder()
                .withPermission(Permission.ADMIN_KICK)
                .build()
    }

    override fun execute(info: CommandInfo): Boolean {
        val target = info.getArgAsFPlayer(0, false, offline = true) ?: return false
        if (!target.hasFaction()) {
            info.message(Message.commandAdminKickNotInFaction, target.name)
            return false
        }

        if (target.isLeader()) {
            info.message(Message.commandAdminKickLeader, target.name)
            return false
        }

        val faction = target.getFaction()
        faction.removeMember(target, isAdminKick = true)
        target.message(Message.commandAdminKickNotify)
        info.message(Message.commandAdminKickSuccess, target.name)
        return false
    }

    override fun getHelpInfo(): String {
        return Message.commandAdminKickHelp
    }
}