package net.prosavage.factionsx.command.admin.cmd.strikes

import net.prosavage.factionsx.command.engine.CommandInfo
import net.prosavage.factionsx.command.engine.CommandRequirementsBuilder
import net.prosavage.factionsx.command.engine.FCommand
import net.prosavage.factionsx.core.Permission
import net.prosavage.factionsx.persist.Message

class CmdAdminStrikesRemove : FCommand() {
    init {
        aliases += "remove"

        requiredArgs += Argument("faction", 0, FactionArgument())
        requiredArgs += Argument("id", 1, IntArgument())

        commandRequirements = CommandRequirementsBuilder()
                .withPermission(Permission.STRIKES_REMOVE)
                .build()
    }

    override fun execute(info: CommandInfo): Boolean {
        val faction = info.getArgAsFaction(0, cannotReferenceYourSelf = false) ?: return false
        val id = info.getArgAsInt(1) ?: return false

        if (id <= 0 || faction.strikes.size < id) {
            info.message(Message.commandStrikesNotFoundById, faction.tag, id.toString())
            return false
        }

        faction.removeStrike(info.commandSender, id)
        info.message(Message.commandStrikesRemove, id.toString(), faction.tag)
        return true
    }

    override fun getHelpInfo(): String = Message.commandStrikesRemoveHelp
}