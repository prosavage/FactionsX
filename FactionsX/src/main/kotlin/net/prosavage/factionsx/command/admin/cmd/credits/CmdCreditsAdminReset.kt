package net.prosavage.factionsx.command.admin.cmd.credits

import net.prosavage.factionsx.command.engine.CommandInfo
import net.prosavage.factionsx.command.engine.CommandRequirementsBuilder
import net.prosavage.factionsx.command.engine.FCommand
import net.prosavage.factionsx.core.Permission
import net.prosavage.factionsx.manager.CreditManager
import net.prosavage.factionsx.persist.Message
import net.prosavage.factionsx.persist.config.Config

class CmdCreditsAdminReset : FCommand() {
    init {
        aliases.add("reset")

        requiredArgs.add(Argument("target", 0, PlayerArgument()))

        commandRequirements = CommandRequirementsBuilder()
                .withPermission(Permission.RESET)
                .build()
    }

    override fun execute(info: CommandInfo): Boolean {
        val target = info.getArgAsFPlayer(0, cannotReferenceYourSelf = false) ?: return false
        CreditManager.reset(target)

        with (Config.creditSettings.startingCredits) {
            target.message(Message.commandCreditsResetNotify, this.toString())
            info.fPlayer?.message(Message.commandCreditsReset, this.toString(), target.name)
        }
        return true
    }

    override fun getHelpInfo(): String {
        return Message.commandCreditsResetHelp
    }
}