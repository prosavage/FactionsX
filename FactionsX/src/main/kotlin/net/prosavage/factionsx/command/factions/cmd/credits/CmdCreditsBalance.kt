package net.prosavage.factionsx.command.factions.cmd.credits

import net.prosavage.factionsx.command.engine.CommandInfo
import net.prosavage.factionsx.command.engine.CommandRequirementsBuilder
import net.prosavage.factionsx.command.engine.FCommand
import net.prosavage.factionsx.core.Permission
import net.prosavage.factionsx.persist.Message

class CmdCreditsBalance : FCommand() {
    init {
        aliases.add("balance")

        optionalArgs.add(Argument("target", 0, PlayerArgument()))

        commandRequirements = CommandRequirementsBuilder()
                .asPlayer(true)
                .withPermission(Permission.CHECK)
                .build()
    }

    override fun execute(info: CommandInfo): Boolean {
        val player = info.fPlayer ?: return false

        if (info.args.isEmpty()) {
            info.message(Message.commandCreditsBalance, player.credits.toString())
            return false
        }

        val target = info.getArgAsFPlayer(0) ?: return false
        info.message(Message.commandCreditsBalanceOthers, target.name, target.credits.toString())
        return true
    }

    override fun getHelpInfo(): String {
        return Message.commandCreditsBalanceHelp
    }
}