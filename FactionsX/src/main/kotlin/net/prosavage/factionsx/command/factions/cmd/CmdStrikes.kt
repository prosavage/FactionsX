package net.prosavage.factionsx.command.factions.cmd

import net.prosavage.factionsx.command.engine.CommandInfo
import net.prosavage.factionsx.command.engine.CommandRequirementsBuilder
import net.prosavage.factionsx.command.engine.FCommand
import net.prosavage.factionsx.core.Faction
import net.prosavage.factionsx.core.Permission
import net.prosavage.factionsx.manager.PlaceholderManager.processPlaceholders
import net.prosavage.factionsx.persist.Message
import net.prosavage.factionsx.persist.color

class CmdStrikes : FCommand() {
    init {
        aliases.add("strikes")

        commandRequirements = CommandRequirementsBuilder()
                .asFactionMember(true)
                .withPermission(Permission.STRIKES)
                .build()
    }

    override fun execute(info: CommandInfo): Boolean {
        val faction = info.faction ?: return false
        info.message(color(generateView(faction)))
        return true
    }

    override fun getHelpInfo() = Message.commandStrikesHelp

    companion object {
        internal fun generateView(faction: Faction): String = Message.commandStrikesView
                .map { processPlaceholders(null, faction, it) }
                .joinToString("\n") { msg ->
                    msg.replace("{strikes_list}", faction.strikes.mapIndexed { index, string ->
                        Message.commandStrikesFormat.replace("{id}", (index + 1).toString()).replace("{reason}", string)
                    }.joinToString("\n"))
                }
    }
}