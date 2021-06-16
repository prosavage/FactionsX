package net.prosavage.factionsx.command.factions.cmd

import net.prosavage.factionsx.command.engine.CommandInfo
import net.prosavage.factionsx.command.engine.CommandRequirementsBuilder
import net.prosavage.factionsx.command.engine.FCommand
import net.prosavage.factionsx.core.FPlayer
import net.prosavage.factionsx.core.Permission
import net.prosavage.factionsx.gui.access.AccessChunkMenu
import net.prosavage.factionsx.gui.access.AccessTypeMenu
import net.prosavage.factionsx.manager.FactionManager
import net.prosavage.factionsx.manager.PlayerManager
import net.prosavage.factionsx.persist.Message
import net.prosavage.factionsx.persist.config.gui.AccessGUIConfig
import net.prosavage.factionsx.util.MemberAction.ACCESS_FACTIONS
import net.prosavage.factionsx.util.MemberAction.ACCESS_PLAYERS

class CmdAccess : FCommand() {
    init {
        this.aliases += "access"

        this.optionalArgs += Argument("type", 0, object : ArgumentType() {
            override fun getPossibleValues(fPlayer: FPlayer?): List<String> =
                    listOf("faction", "player")
        })

        this.optionalArgs += Argument("name", 1, FactionPlayerArgument())

        this.commandRequirements = CommandRequirementsBuilder()
                .asFactionMember(true)
                .withPermission(Permission.ACCESS)
                .build()
    }

    override fun execute(info: CommandInfo): Boolean {
        // necessity
        val fPlayer = info.fPlayer!!
        val player = info.player!!
        val faction = info.faction!!
        val type = info.args.getOrNull(0)

        // make sure the type is not null
        if (type == null) {
            AccessTypeMenu.of(faction).open(info.player ?: return true)
            return false
        }

        // fetch the name if not null otherwise send message and return
        val name = info.args.getOrNull(1) ?: run {
            info.message(Message.commandAccessSpecifyName)
            return false
        }

        // handle type
        when (type.toLowerCase()) {
            "faction" -> {
                if (!fPlayer.canDoMemberAction(ACCESS_FACTIONS)) {
                    fPlayer.message(AccessGUIConfig.typeMenuNoPermission, ACCESS_FACTIONS.actionName)
                    return false
                }

                val targetFaction = FactionManager.getFaction(name) ?: run {
                    info.message(Message.commandAccessNoFactionFound, name)
                    return false
                }

                if (targetFaction == faction) {
                    info.message(Message.commandAccessYourFaction)
                    return false
                }

                AccessChunkMenu.of(faction, true, targetFaction).open(player)
            }

            "player" -> {
                if (!fPlayer.canDoMemberAction(ACCESS_PLAYERS)) {
                    fPlayer.message(AccessGUIConfig.typeMenuNoPermission, ACCESS_PLAYERS.actionName)
                    return false
                }

                val targetPlayer = PlayerManager.getFPlayer(name) ?: run {
                    info.message(Message.commandAccessNoPlayerFound, name)
                    return false
                }

                if (targetPlayer.getFaction() == faction) {
                    info.message(Message.commandAccessYourPlayerFaction)
                    return false
                }

                AccessChunkMenu.of(faction, false, targetPlayer).open(player)
            }

            else -> info.message(Message.commandAccessInvalidType)
        }

        return true
    }

    override fun getHelpInfo() = Message.commandAccessHelp
}