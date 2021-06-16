package net.prosavage.factionsx.command.factions.cmd.alts

import net.prosavage.factionsx.command.engine.CommandInfo
import net.prosavage.factionsx.command.engine.CommandRequirementsBuilder
import net.prosavage.factionsx.command.engine.FCommand
import net.prosavage.factionsx.core.Permission
import net.prosavage.factionsx.persist.Message
import net.prosavage.factionsx.util.MemberAction

class CmdAltsRevoke : FCommand() {
    init {
        aliases += "revoke"

        requiredArgs += Argument("player", 0, PlayerArgument())

        commandRequirements = CommandRequirementsBuilder()
                .asFactionMember(true)
                .withPermission(Permission.ALTS_REVOKE)
                .withMemberAction(MemberAction.ALTS_REVOKE)
                .build()
    }

    override fun execute(info: CommandInfo): Boolean {
        val faction = info.faction ?: return false

        val revoked = info.getArgAsFPlayer(0) ?: return false
        if (revoked.getFaction() == faction) {
            info.message(Message.commandAltsRevokeAlreadyInFaction, revoked.name)
            return false
        }

        val invite = faction.getInvite(revoked) ?: false
        if (!invite) {
            info.message(Message.commandAltsRevokeNotPresent, revoked.name)
            return false
        }

        revoked.deInviteFromFaction(faction)
        info.message(Message.commandAltsRevoked, revoked.name)

        faction.message(
                Message.commandAltsRevokedAll,
                revoked.name, info.player?.name ?: "Unknown",
                excludeFPlayers = listOf(info.fPlayer)
        )
        return true
    }

    override fun getHelpInfo(): String = Message.commandAltsRevokeHelp
}