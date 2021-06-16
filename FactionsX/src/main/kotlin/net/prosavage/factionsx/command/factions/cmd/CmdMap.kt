package net.prosavage.factionsx.command.factions.cmd

import net.prosavage.factionsx.command.engine.CommandInfo
import net.prosavage.factionsx.command.engine.CommandRequirementsBuilder
import net.prosavage.factionsx.command.engine.FCommand
import net.prosavage.factionsx.core.Permission
import net.prosavage.factionsx.persist.Message

class CmdMap : FCommand() {

    init {
        aliases.add("map")

        optionalArgs.add(Argument("on/off", 0, FMapToggleArgument()))

        commandRequirements = CommandRequirementsBuilder()
                .asPlayer(true)
                .withPermission(Permission.MAP)
                .build()
    }


    override fun execute(info: CommandInfo): Boolean {
        if (info.args.size == 1) {
            val toggle = info.getArgAsBoolean(0) ?: return false
            info.message(Message.commandMapToggled, if (toggle) Message.commandMapToggledOnMessage else Message.commandMapToggledOffMessage)
            info.fPlayer!!.mapToggle = toggle
            return true
        } else if (info.args.isNotEmpty()) {
            sendCommandFormat(info, true)
            return false
        }
        info.fPlayer!!.sendMap()
        return true
    }

    override fun getHelpInfo(): String {
        return Message.commandMapHelp
    }
}