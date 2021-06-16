package net.prosavage.factionsx.command.factions.cmd

import net.prosavage.factionsx.command.engine.CommandInfo
import net.prosavage.factionsx.command.engine.CommandRequirementsBuilder
import net.prosavage.factionsx.command.engine.FCommand
import net.prosavage.factionsx.core.Permission
import net.prosavage.factionsx.persist.Message
import net.prosavage.factionsx.persist.config.Config

class CmdPower : FCommand() {


    init {
        aliases.add("power")
        aliases.add("pow")

        optionalArgs.add(Argument("player", 0, PlayerArgument()))


        commandRequirements = CommandRequirementsBuilder()
                .asPlayer(true)
                .withPermission(Permission.POWER)
                .build()
    }

    override fun execute(info: CommandInfo): Boolean {
        var target = info.fPlayer
        if (info.args.isNotEmpty()) target = info.getArgAsFPlayer(0, offline = true, cannotReferenceYourSelf = false) ?: return false
        info.message(
                Message.commandPowerInfo,
                target!!.name,
                Config.numberFormat.format(target.power()),
                Config.numberFormat.format(target.getMaxPower())
        )
        return true
    }

    override fun getHelpInfo(): String {
        return Message.commandPowerHelp
    }

}





