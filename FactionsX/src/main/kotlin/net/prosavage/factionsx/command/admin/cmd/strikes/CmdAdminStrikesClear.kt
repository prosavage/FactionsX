package net.prosavage.factionsx.command.admin.cmd.strikes

import net.prosavage.factionsx.command.engine.CommandInfo
import net.prosavage.factionsx.command.engine.CommandRequirementsBuilder
import net.prosavage.factionsx.command.engine.FCommand
import net.prosavage.factionsx.core.Permission
import net.prosavage.factionsx.persist.Message

class CmdAdminStrikesClear : FCommand() {
    init {
        aliases += "clear"

        requiredArgs += Argument("faction", 0, FactionArgument())

        commandRequirements = CommandRequirementsBuilder()
                .withPermission(Permission.STRIKES_CLEAR)
                .build()
    }

    override fun execute(info: CommandInfo): Boolean { // FACTION MAX STRIKES AND IF ID EXISTS
        val faction = info.getArgAsFaction(0, cannotReferenceYourSelf = false) ?: return false

        if (faction.strikes.isEmpty()) {
            info.message(Message.commandStrikesNoStrikes, faction.tag)
            return false
        }

        faction.clearStrikes(info.commandSender)
        info.message(Message.commandStrikesClear, faction.tag)
        return true
    }

    override fun getHelpInfo(): String = Message.commandStrikesClearHelp
}