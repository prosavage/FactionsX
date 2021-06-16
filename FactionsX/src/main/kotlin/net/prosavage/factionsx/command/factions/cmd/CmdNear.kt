package net.prosavage.factionsx.command.factions.cmd

import net.prosavage.factionsx.command.engine.CommandInfo
import net.prosavage.factionsx.command.engine.CommandRequirementsBuilder
import net.prosavage.factionsx.command.engine.FCommand
import net.prosavage.factionsx.core.Permission
import net.prosavage.factionsx.manager.PlayerManager
import net.prosavage.factionsx.persist.Message
import net.prosavage.factionsx.persist.config.Config
import org.bukkit.entity.Player

class CmdNear : FCommand() {

    init {
        aliases.add("near")

        commandRequirements = CommandRequirementsBuilder()
                .withPermission(Permission.NEAR)
                .asFactionMember(true)
                .build()
    }

    override fun execute(info: CommandInfo): Boolean {
        info.message(Message.commandNearHeader)
        info.player!!.getNearbyEntities(Config.fNearRadiusInBlocks, Config.fNearRadiusInBlocks, Config.fNearRadiusInBlocks).forEachIndexed { index, entity ->
            if (entity is Player && PlayerManager.getFPlayer(entity).getFaction() == info.faction!!) {
                info.message(Message.commandNearFormat, (index + 1).toString(), entity.name, Config.numberFormat.format(info.player!!.location.distance(entity.location)))
            }
        }
        return true
    }

    override fun getHelpInfo(): String {
        return Message.commandNearHelp
    }


}