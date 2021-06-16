package net.prosavage.factionsx.command.admin.cmd.strikes

import net.prosavage.factionsx.command.engine.CommandInfo
import net.prosavage.factionsx.command.engine.CommandRequirementsBuilder
import net.prosavage.factionsx.command.engine.FCommand
import net.prosavage.factionsx.command.factions.cmd.CmdStrikes
import net.prosavage.factionsx.core.Permission
import net.prosavage.factionsx.persist.Message
import net.prosavage.factionsx.persist.color

class CmdAdminStrikesCheck : FCommand() {
    init {
        aliases += "check"

        requiredArgs += Argument("faction", 0, FactionArgument())

        commandRequirements = CommandRequirementsBuilder()
                .withPermission(Permission.STRIKES_CHECK)
                .build()
    }

    override fun execute(info: CommandInfo): Boolean {
        val faction = info.getArgAsFaction(0, cannotReferenceYourSelf = false) ?: return false
        info.message(color(CmdStrikes.generateView(faction)))
        return true
    }

    override fun getHelpInfo(): String = Message.commandStrikesCheckHelp
}