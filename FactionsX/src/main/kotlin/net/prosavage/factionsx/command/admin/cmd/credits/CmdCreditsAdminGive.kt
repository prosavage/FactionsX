package net.prosavage.factionsx.command.admin.cmd.credits

import net.prosavage.factionsx.command.engine.CommandInfo
import net.prosavage.factionsx.command.engine.CommandRequirementsBuilder
import net.prosavage.factionsx.command.engine.FCommand
import net.prosavage.factionsx.core.Permission
import net.prosavage.factionsx.manager.CreditManager
import net.prosavage.factionsx.persist.Message

class CmdCreditsAdminGive : FCommand() {
    init {
        aliases.add("give")

        requiredArgs.add(Argument("target", 0, PlayerArgument()))
        requiredArgs.add(Argument("credits", 1, IntArgument()))

        commandRequirements = CommandRequirementsBuilder()
                .withPermission(Permission.GIVE)
                .build()
    }

    override fun execute(info: CommandInfo): Boolean {
        val target = info.getArgAsFPlayer(0, cannotReferenceYourSelf = false) ?: return false
        val amount = info.getArgAsDouble(1) ?: return false

        val overflow = CreditManager.give(target, amount, false)
        val exactAmount = amount - overflow

        target.message(Message.commandCreditsGiveNotify, exactAmount.toString())
        info.fPlayer?.message(Message.commandCreditsGive, target.name, exactAmount.toString())
        return true
    }

    override fun getHelpInfo(): String {
        return Message.commandCreditsGiveHelp
    }
}