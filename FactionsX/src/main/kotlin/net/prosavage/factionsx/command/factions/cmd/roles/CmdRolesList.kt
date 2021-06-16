package net.prosavage.factionsx.command.factions.cmd.roles

import me.rayzr522.jsonmessage.JSONMessage
import net.prosavage.factionsx.command.engine.CommandInfo
import net.prosavage.factionsx.command.engine.CommandRequirementsBuilder
import net.prosavage.factionsx.command.engine.FCommand
import net.prosavage.factionsx.persist.Message
import net.prosavage.factionsx.persist.color
import net.prosavage.factionsx.util.buildBar

class CmdRolesList : FCommand() {

    init {
        aliases.add("list")


        commandRequirements = CommandRequirementsBuilder().asLeader(true).build()
    }

    override fun execute(info: CommandInfo): Boolean {
        if (Message.commandRolesListSendBar) info.message(buildBar(Message.commandRolesListBarElement))
        info.message(Message.commandRolesListHeader)
        val factionRoles = info.faction!!.factionRoles
        val allRoles = factionRoles.getAllRoles()
        for ((index, customRole) in allRoles.withIndex().reversed()) {
            val entry = JSONMessage.create(color(
                    String.format(
                            Message.commandRolesListEntry,
                            (index + 1).toString(),
                            customRole.roleTag
                    )
            ))
                    .tooltip(color(String.format(Message.commandRolesListEntryToolTip, customRole.roleTag)))
                    .runCommand("/f roles info ${customRole.roleTag}")


            if (allRoles.getOrNull(index + 1) != null) {
                val roleToReplace = allRoles[index + 1]
                entry.then(color(Message.commandRolesListMoveUp))
                        .tooltip(color(Message.commandRolesListMoveUpTooltip))
                        .runCommand("/f roles move ${customRole.roleTag} ${roleToReplace.roleTag} true")
            }

            if (index != 0) {
                val roleToReplace = allRoles[index - 1]
                entry.then(color(Message.commandRolesListMoveDown))
                        .tooltip(color(Message.commandRolesListMoveDownTooltip))
                        .runCommand("/f roles move ${customRole.roleTag} ${roleToReplace.roleTag} true")

            }

            if (factionRoles.getApexRole() != customRole) {
                entry.then(color(Message.commandRolesListMoveDelete))
                        .tooltip(color(Message.commandRolesListMoveDeleteTooltip))
                        .runCommand("/f role remove ${customRole.roleTag}")
            }

            entry.send(info.player)
        }
        if (Message.commandRolesListSendBar) info.message(buildBar(Message.commandRolesListBarElement))
        return true
    }

    override fun getHelpInfo(): String {
        return Message.commandRolesListHelp
    }
}