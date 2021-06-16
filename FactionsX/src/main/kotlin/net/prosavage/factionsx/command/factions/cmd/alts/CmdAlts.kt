package net.prosavage.factionsx.command.factions.cmd.alts

import net.prosavage.factionsx.command.engine.CommandInfo
import net.prosavage.factionsx.command.engine.CommandRequirementsBuilder
import net.prosavage.factionsx.command.engine.FCommand
import net.prosavage.factionsx.core.Permission
import net.prosavage.factionsx.persist.Message

class CmdAlts : FCommand() {
    init {
        aliases += "alts"

        commandRequirements = CommandRequirementsBuilder()
                .asPlayer(true)
                .withPermission(Permission.ALTS)
                .build()

        addSubCommand(CmdAltsInvite())
        addSubCommand(CmdAltsJoin())
        addSubCommand(CmdAltsRevoke())
        addSubCommand(CmdAltsOpen())
        addSubCommand(CmdAltsClose())
        addSubCommand(CmdAltsInvites())
        addSubCommand(CmdAltsList())
        addSubCommand(CmdAltsKick())
    }

    override fun execute(info: CommandInfo): Boolean {
        generateHelp(1, info.commandSender, info.args)
        return true
    }

    override fun getHelpInfo(): String = Message.commandAltsHelp
}