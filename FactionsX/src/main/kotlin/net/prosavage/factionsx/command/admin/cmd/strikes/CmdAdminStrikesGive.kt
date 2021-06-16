package net.prosavage.factionsx.command.admin.cmd.strikes

import net.prosavage.factionsx.command.engine.CommandInfo
import net.prosavage.factionsx.command.engine.CommandRequirementsBuilder
import net.prosavage.factionsx.command.engine.FCommand
import net.prosavage.factionsx.core.Permission
import net.prosavage.factionsx.persist.Message
import net.prosavage.factionsx.persist.config.Config

class CmdAdminStrikesGive : FCommand() {
    init {
        aliases += "give"

        requiredArgs += Argument("faction", 0, FactionArgument())
        requiredArgs += Argument("reason", 1, StringArgument())
        bypassArgumentCount = true

        commandRequirements = CommandRequirementsBuilder()
                .withPermission(Permission.STRIKES_GIVE)
                .build()
    }

    override fun execute(info: CommandInfo): Boolean {
        val faction = info.getArgAsFaction(0, cannotReferenceYourSelf = false) ?: return false
        val reason = info.args.drop(1).joinToString(" ")

        if (faction.strikes.size >= Config.factionStrikeMax && Config.factionStrikeMax != -1) {
            info.message(Message.commandStrikesMetMax, faction.tag)
            return false
        }

        faction.addStrike(info.commandSender, reason)
        info.message(Message.commandStrikesGive, faction.tag, reason)
        return true
    }

    override fun getHelpInfo(): String = Message.commandStrikesGiveHelp
}