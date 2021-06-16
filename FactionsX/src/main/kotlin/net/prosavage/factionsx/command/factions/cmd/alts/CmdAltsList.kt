package net.prosavage.factionsx.command.factions.cmd.alts

import net.prosavage.factionsx.command.engine.CommandInfo
import net.prosavage.factionsx.command.engine.CommandRequirementsBuilder
import net.prosavage.factionsx.command.engine.FCommand
import net.prosavage.factionsx.core.Permission
import net.prosavage.factionsx.persist.Message
import net.prosavage.factionsx.persist.config.Config
import net.prosavage.factionsx.util.MemberAction

class CmdAltsList : FCommand() {
    init {
        aliases += "list"

        commandRequirements = CommandRequirementsBuilder()
                .asFactionMember(true)
                .withPermission(Permission.ALTS_LIST)
                .withMemberAction(MemberAction.ALTS_LIST)
                .build()
    }

    override fun execute(info: CommandInfo): Boolean {
        val faction = info.faction ?: return false

        val currentAlts = faction.getAlts().ifEmpty {
            info.message(Message.commandAltsListEmpty)
            return false
        }

        info.message(Message.commandAltsListPresent.format(
                currentAlts.size.toString(),
                currentAlts.joinToString(Config.altsListSeparator) {
                    Config.altsListFormat.format(it.name)
                }
        ))

        return true
    }

    override fun getHelpInfo(): String = Message.commandAltsListHelp
}