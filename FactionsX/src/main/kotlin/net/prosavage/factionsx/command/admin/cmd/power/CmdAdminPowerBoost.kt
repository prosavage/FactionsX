package net.prosavage.factionsx.command.admin.cmd.power

import net.prosavage.factionsx.command.engine.CommandInfo
import net.prosavage.factionsx.command.engine.CommandRequirementsBuilder
import net.prosavage.factionsx.command.engine.FCommand
import net.prosavage.factionsx.core.Permission
import net.prosavage.factionsx.persist.Message
import net.prosavage.factionsx.persist.config.Config

class CmdAdminPowerBoost : FCommand() {
    init {
        aliases.add("boost")

        requiredArgs.add(Argument("player", 0, PlayerArgument()))
        requiredArgs.add(Argument("delta", 1, IntArgument()))

        commandRequirements = CommandRequirementsBuilder()
                .withPermission(Permission.ADMIN_POWERBOOST)
                .build()
    }


    override fun execute(info: CommandInfo): Boolean {
        val target = info.getArgAsFPlayer(0, cannotReferenceYourSelf = false, offline = true) ?: return false
        val delta = info.getArgAsDouble(1) ?: return false
        target.powerBoost = delta
        info.message(Message.commandAdminPowerBoostSuccess, delta.toString(), target.name, Config.numberFormat.format(target.power()))
        return true
    }

    override fun getHelpInfo(): String {
        return Message.commandAdminPowerBoostHelp
    }
}