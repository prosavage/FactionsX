package net.prosavage.factionsx.command.factions.cmd.warp

import net.prosavage.factionsx.command.engine.CommandInfo
import net.prosavage.factionsx.command.engine.CommandRequirementsBuilder
import net.prosavage.factionsx.command.engine.FCommand
import net.prosavage.factionsx.core.Permission
import net.prosavage.factionsx.persist.Message

class CmdWarp : FCommand() {
    init {
        aliases.add("warp")

        addSubCommand(CmdWarpGo())
        addSubCommand(CmdWarpList())
        addSubCommand(CmdWarpSet())
        addSubCommand(CmdWarpRemove())

        commandRequirements = CommandRequirementsBuilder()
                .withPermission(Permission.WARP)
                .asFactionMember(true)
                .build()
    }

    override fun execute(info: CommandInfo): Boolean {
        generateHelp(1, info.commandSender, info.args)
        return true
    }

    override fun getHelpInfo(): String {
        return Message.commandWarpsHelp
    }
}