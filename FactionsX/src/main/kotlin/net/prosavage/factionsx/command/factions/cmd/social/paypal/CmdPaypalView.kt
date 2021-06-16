package net.prosavage.factionsx.command.factions.cmd.social.paypal

import net.prosavage.factionsx.command.engine.CommandInfo
import net.prosavage.factionsx.command.engine.CommandRequirementsBuilder
import net.prosavage.factionsx.command.engine.FCommand
import net.prosavage.factionsx.core.Faction
import net.prosavage.factionsx.core.Permission
import net.prosavage.factionsx.persist.Message

class CmdPaypalView : FCommand() {
    init {
        aliases.add("view")

        optionalArgs.add(Argument("faction-name", 0, FactionArgument()))

        commandRequirements = CommandRequirementsBuilder()
                .asPlayer(true)
                .withPermission(Permission.PAYPAL_VIEW)
                .build()
    }

    override fun execute(info: CommandInfo): Boolean {
        if (info.args.isEmpty()) info.message(Message.commandPaypalViewYourOwn, info.fPlayer!!.getFaction().paypal)
        if (info.args.isNotEmpty()) {
            val faction = info.getArgAsFaction(0) ?: return false
            checkPaypal(faction, info)
        }
        return true
    }

    override fun getHelpInfo(): String {
        return Message.commandPaypalViewHelp
    }

    private fun checkPaypal(faction: Faction?, info: CommandInfo) {
        info.message(Message.commandPaypalView, faction!!.tag, faction.paypal)
    }
}