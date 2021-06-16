package net.prosavage.factionsx.command.admin.cmd.strikes

import net.prosavage.factionsx.command.engine.CommandInfo
import net.prosavage.factionsx.command.engine.CommandRequirementsBuilder
import net.prosavage.factionsx.command.engine.FCommand
import net.prosavage.factionsx.core.Permission
import net.prosavage.factionsx.persist.Message

class CmdAdminStrikes : FCommand() {
    init {
        aliases += "strikes"

        commandRequirements = CommandRequirementsBuilder()
                .withPermission(Permission.ADMIN_STRIKES)
                .build()

        addSubCommand(CmdAdminStrikesGive())
        addSubCommand(CmdAdminStrikesRemove())
        addSubCommand(CmdAdminStrikesEdit())
        addSubCommand(CmdAdminStrikesClear())
        addSubCommand(CmdAdminStrikesCheck())
    }

    override fun execute(info: CommandInfo): Boolean {
        generateHelp(1, info.commandSender, info.args)
        return true
    }

    override fun getHelpInfo(): String = Message.commandAdminStrikesHelp
}