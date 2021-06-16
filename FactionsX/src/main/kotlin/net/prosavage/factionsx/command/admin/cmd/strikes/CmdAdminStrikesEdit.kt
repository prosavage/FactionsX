package net.prosavage.factionsx.command.admin.cmd.strikes

import net.prosavage.factionsx.command.engine.CommandInfo
import net.prosavage.factionsx.command.engine.CommandRequirementsBuilder
import net.prosavage.factionsx.command.engine.FCommand
import net.prosavage.factionsx.core.Permission
import net.prosavage.factionsx.persist.Message

class CmdAdminStrikesEdit : FCommand() {
    init {
        aliases += "edit"

        requiredArgs += Argument("faction", 0, FactionArgument())
        requiredArgs += Argument("id", 1, IntArgument())
        requiredArgs += Argument("reason", 2, StringArgument())
        bypassArgumentCount = true

        commandRequirements = CommandRequirementsBuilder()
                .withPermission(Permission.STRIKES_EDIT)
                .build()
    }

    override fun execute(info: CommandInfo): Boolean {
        val faction = info.getArgAsFaction(0, cannotReferenceYourSelf = false) ?: return false
        val id = info.getArgAsInt(1) ?: return false
        val reason = info.args.drop(2).joinToString(" ")

        if (id <= 0 || faction.strikes.size < id) {
            info.message(Message.commandStrikesNotFoundById, faction.tag, id.toString())
            return false
        }

        faction.editStrike(info.commandSender, id, reason)
        info.message(Message.commandStrikesEdit, id.toString(), faction.tag, reason)
        return true
    }

    override fun getHelpInfo(): String = Message.commandStrikesEditHelp
}