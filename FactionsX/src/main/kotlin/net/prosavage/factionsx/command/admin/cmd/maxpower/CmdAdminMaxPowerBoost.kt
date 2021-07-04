package net.prosavage.factionsx.command.admin.cmd.maxpower

import net.prosavage.factionsx.command.engine.CommandInfo
import net.prosavage.factionsx.command.engine.CommandRequirementsBuilder
import net.prosavage.factionsx.command.engine.FCommand
import net.prosavage.factionsx.core.Permission
import net.prosavage.factionsx.persist.Message
import net.prosavage.factionsx.persist.config.Config

class CmdAdminMaxPowerBoost : FCommand() {
    init {
        aliases.add("boost")

        requiredArgs.add(Argument("player", 0, PlayerArgument()))
        requiredArgs.add(Argument("delta", 1, IntArgument()))

        commandRequirements = CommandRequirementsBuilder()
            .withPermission(Permission.ADMIN_MAX_POWER_BOOST)
            .build()
    }

    override fun execute(info: CommandInfo): Boolean {
        val target = info.getArgAsFPlayer(0, cannotReferenceYourSelf = false, offline = true) ?: return false
        val delta = info.getArgAsDouble(1) ?: return false
        target.maxPowerBoost = delta
        info.message(
            Message.commandAdminMaxPowerBoostSuccess,
            delta.toString(), target.name,
            Config.numberFormat.format(target.getMaxPower())
        )
        return true
    }

    override fun getHelpInfo(): String = Message.commandAdminMaxPowerBoostHelp
}