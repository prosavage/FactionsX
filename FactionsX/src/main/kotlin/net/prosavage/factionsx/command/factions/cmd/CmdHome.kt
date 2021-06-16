package net.prosavage.factionsx.command.factions.cmd

import net.prosavage.factionsx.command.engine.CommandInfo
import net.prosavage.factionsx.command.engine.CommandRequirementsBuilder
import net.prosavage.factionsx.command.engine.FCommand
import net.prosavage.factionsx.core.Permission
import net.prosavage.factionsx.event.FPlayerPreTeleportEvent
import net.prosavage.factionsx.manager.GridManager
import net.prosavage.factionsx.manager.teleportAsyncWithWarmup
import net.prosavage.factionsx.persist.Message
import net.prosavage.factionsx.persist.config.Config
import net.prosavage.factionsx.persist.config.EconConfig
import net.prosavage.factionsx.persist.data.getFLocation
import net.prosavage.factionsx.util.MemberAction
import net.prosavage.factionsx.util.Relation
import org.bukkit.Bukkit

class CmdHome : FCommand() {
    init {
        aliases.add("home")

        commandRequirements = CommandRequirementsBuilder()
            .asFactionMember(true)
            .withPermission(Permission.HOME)
            .withMemberAction(MemberAction.HOME)
            .withPrice(EconConfig.fHomeCost)
            .build()
    }

    override fun execute(info: CommandInfo): Boolean {
        val faction = info.faction!!
        if (faction.home == null) {
            info.message(Message.commandHomeNotSet)
            return false
        }

        val flocation = getFLocation(info.player!!.location)
        val factionAt = flocation.getFaction()

        val home = faction.home!!
        if (!home.exists() || faction != GridManager.getFactionAt(home.getLocation().chunk)) {
            info.message(Message.commandHomeInvalid)
            faction.home = null
            return false
        }

        if (Config.denyHomeTeleportInEnemyLand && factionAt.getRelationTo(faction) == Relation.ENEMY) {
            info.message(Message.commandHomeEnemyLand)
            return false
        }

        val event = FPlayerPreTeleportEvent(info.fPlayer!!, home.getLocation(), MemberAction.HOME, Message.commandHomeTeleportDenied)
        Bukkit.getPluginManager().callEvent(event)
        if (event.isCancelled) {
            info.message(event.denyMessage)
            return false
        }

        teleportAsyncWithWarmup(info.player!!, home.getLocation(), Config.homeTeleportWarmupSeconds * 20L)
        info.message(Message.commandHomeTeleporting, Config.homeTeleportWarmupSeconds.toString())
        return true
    }

    override fun getHelpInfo(): String {
        return Message.commandHomeHelp
    }
}