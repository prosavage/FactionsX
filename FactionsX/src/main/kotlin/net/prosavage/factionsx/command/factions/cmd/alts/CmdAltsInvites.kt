package net.prosavage.factionsx.command.factions.cmd.alts

import net.prosavage.factionsx.command.engine.CommandInfo
import net.prosavage.factionsx.command.engine.CommandRequirementsBuilder
import net.prosavage.factionsx.command.engine.FCommand
import net.prosavage.factionsx.core.Permission
import net.prosavage.factionsx.persist.Message
import net.prosavage.factionsx.persist.config.Config
import net.prosavage.factionsx.util.MemberAction

class CmdAltsInvites : FCommand() {
    init {
        aliases += "invites"

        commandRequirements = CommandRequirementsBuilder()
                .asFactionMember(true)
                .withPermission(Permission.ALTS_INVITES)
                .withMemberAction(MemberAction.ALTS_INVITES)
                .build()
    }

    override fun execute(info: CommandInfo): Boolean {
        val faction = info.faction ?: return false

        val invites = faction.getAltInvites().ifEmpty {
            info.message(Message.commandAltsInvitesEmpty)
            return false
        }

        info.message(Message.commandAltsInvitesPresent.format(
                invites.size.toString(),
                invites.joinToString(Config.altsInvitesSeparator) {
                    Config.altsInvitesFormat.format(it.name)
                }
        ))

        return true
    }

    override fun getHelpInfo(): String = Message.commandAltsInvitesHelp
}