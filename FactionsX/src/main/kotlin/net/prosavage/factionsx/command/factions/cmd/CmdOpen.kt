package net.prosavage.factionsx.command.factions.cmd

import net.prosavage.factionsx.command.engine.CommandInfo
import net.prosavage.factionsx.command.engine.CommandRequirementsBuilder
import net.prosavage.factionsx.command.engine.FCommand
import net.prosavage.factionsx.core.Permission
import net.prosavage.factionsx.persist.Message
import net.prosavage.factionsx.util.MemberAction

class CmdOpen : FCommand() {

    init {
        aliases.add("open")

        requiredArgs.add(Argument("true/false", 0, BooleanArgument()))

        commandRequirements = CommandRequirementsBuilder()
                .asPlayer(true)
                .asFactionMember(true)
                .withPermission(Permission.OPEN)
                .withMemberAction(MemberAction.OPEN)
                .build()
    }

    override fun execute(info: CommandInfo): Boolean {
        val player = info.fPlayer!!
        val status = info.getArgAsBoolean(0) ?: return false

        player.getFaction().openStatus = status
        info.message(Message.commandFactionStatusSwitch, if (status) Message.commandFactionsOpen else Message.commandFactionsClosed)
        return true
    }

    override fun getHelpInfo(): String {
        return Message.commandFactionsOpenHelp
    }
}