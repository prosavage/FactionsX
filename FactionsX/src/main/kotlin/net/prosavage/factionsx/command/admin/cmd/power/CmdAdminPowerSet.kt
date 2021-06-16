package net.prosavage.factionsx.command.admin.cmd.power

import net.prosavage.factionsx.command.engine.CommandInfo
import net.prosavage.factionsx.command.engine.CommandRequirementsBuilder
import net.prosavage.factionsx.command.engine.FCommand
import net.prosavage.factionsx.core.Permission
import net.prosavage.factionsx.persist.Message
import net.prosavage.factionsx.persist.config.Config

class CmdAdminPowerSet : FCommand() {
    init {
        aliases.add("set")

        requiredArgs.add(Argument("player/faction", 0, PlayerArgument()))
        requiredArgs.add(Argument("new-power", 1, IntArgument()))

        commandRequirements = CommandRequirementsBuilder()
                .withPermission(Permission.ADMIN_POWER)
                .build()
    }

    override fun execute(info: CommandInfo): Boolean {
        val target = info.getArgAsFPlayer(0, cannotReferenceYourSelf = false, offline = true) ?: return false
        val newPower = info.getArgAsDouble(1) ?: return false
        target.updatePower()
        target.setPower(newPower)
        info.message(
                Message.commandAdminPowerSetSuccessfully,
                target.name,
                Config.numberFormat.format(target.power()),
                Config.numberFormat.format(target.getMaxPower())
        )
        return true
    }

    override fun getHelpInfo(): String {
        return Message.commandAdminPowerSetHelp
    }
}