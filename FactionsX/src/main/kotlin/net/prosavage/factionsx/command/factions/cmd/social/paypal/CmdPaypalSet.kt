package net.prosavage.factionsx.command.factions.cmd.social.paypal

import net.prosavage.factionsx.command.engine.CommandInfo
import net.prosavage.factionsx.command.engine.CommandRequirementsBuilder
import net.prosavage.factionsx.command.engine.FCommand
import net.prosavage.factionsx.core.Permission
import net.prosavage.factionsx.persist.Message
import net.prosavage.factionsx.persist.config.EconConfig
import net.prosavage.factionsx.util.MemberAction

class CmdPaypalSet : FCommand() {
    init {
        aliases.add("set")

        requiredArgs.add(Argument("paypal", 0, StringArgument()))

        commandRequirements = CommandRequirementsBuilder()
                .withMemberAction(MemberAction.PAYPAL_SET)
                .withPermission(Permission.PAYPAL_SET)
                .withPrice(EconConfig.fPayPalSetCost)
                .build()
    }

    override fun execute(info: CommandInfo): Boolean {
        info.fPlayer!!.getFaction().paypal = info.args[0]
        info.message(Message.commandPaypalSet)
        return true
    }

    override fun getHelpInfo(): String {
        return Message.commandPaypalSetHelp
    }
}