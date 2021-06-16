package net.prosavage.factionsx.command.factions.cmd.alts

import net.prosavage.factionsx.command.engine.CommandInfo
import net.prosavage.factionsx.command.engine.CommandRequirementsBuilder
import net.prosavage.factionsx.command.engine.FCommand
import net.prosavage.factionsx.core.Permission
import net.prosavage.factionsx.persist.Message
import net.prosavage.factionsx.util.MemberAction

class CmdAltsKick : FCommand() {
    init {
        aliases += "kick"

        requiredArgs += Argument("player", 0, PlayerArgument())

        commandRequirements = CommandRequirementsBuilder()
                .asFactionMember(true)
                .withPermission(Permission.ALTS_KICK)
                .withMemberAction(MemberAction.ALTS_KICK)
                .build()
    }

    override fun execute(info: CommandInfo): Boolean {
        val faction = info.faction ?: return false
        val kicked = info.getArgAsFPlayer(0) ?: return false
        val kicker = info.player ?: return false

        if (kicked.getFaction() != faction) {
            info.message(Message.commandAltsKickNotInFaction)
            return false
        }

        if (!kicked.alt) {
            info.message(Message.commandAltsKickMemberSuggestion)
            return false
        }

        faction.removeAlt(kicked)
        kicked.message(Message.commandKickTargetInform, faction.tag, kicker.name)

        info.message(Message.commandAltsKickSuccess, kicked.name)
        faction.message(Message.commandAltsKickAll, kicked.name, kicker.name, excludeFPlayers = listOf(info.fPlayer))
        return true
    }

    override fun getHelpInfo(): String = Message.commandAltsKickHelp
}