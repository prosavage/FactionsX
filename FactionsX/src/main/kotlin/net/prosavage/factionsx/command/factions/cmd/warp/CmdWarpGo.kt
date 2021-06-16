package net.prosavage.factionsx.command.factions.cmd.warp

import net.prosavage.factionsx.command.engine.CommandInfo
import net.prosavage.factionsx.command.engine.CommandRequirementsBuilder
import net.prosavage.factionsx.command.engine.FCommand
import net.prosavage.factionsx.event.FPlayerPreTeleportEvent
import net.prosavage.factionsx.manager.teleportAsyncWithWarmup
import net.prosavage.factionsx.persist.Message
import net.prosavage.factionsx.persist.config.Config
import net.prosavage.factionsx.persist.config.EconConfig
import net.prosavage.factionsx.util.MemberAction
import net.prosavage.factionsx.util.Relation
import org.bukkit.Bukkit

class CmdWarpGo : FCommand() {
    init {
        aliases.add("go")

        requiredArgs.add(Argument("name", 0, WarpArgument()))
        optionalArgs.add(Argument("password", 1, StringArgument()))

        commandRequirements = CommandRequirementsBuilder()
                .withMemberAction(MemberAction.WARP)
                .withPrice(EconConfig.fWarpGoCost)
                .build()
    }

    override fun execute(info: CommandInfo): Boolean {
        val warpName = info.args[0]
        val faction = info.faction!!
        val warp = faction.getWarp(warpName) ?: run {
            info.message(Message.commandWarpsGoWarpDoesNotExist)
            return false
        }

        if (warp.hasPassword()) {
            val password = info.args.getOrNull(1) ?: run {
                info.message(Message.commandWarpsGoRequiresPassword)
                return false
            }
            if (password != warp.password) {
                info.message(Message.commandWarpsInvalidPassword)
                return false
            }
        }

        if (!warp.dataLocation.exists()) {
            info.message(Message.commandWarpsGoLocationNoLongerExists)
            return false
        }

        if (Config.denyWarpTeleportInEnemyLand && info.fPlayer!!.getFactionAt().getRelationTo(faction) == Relation.ENEMY) {
            info.message(Message.commandHomeEnemyLand)
            return false
        }

        val event = FPlayerPreTeleportEvent(info.fPlayer!!,  warp.dataLocation.getLocation(), MemberAction.WARP, Message.commandWarpTeleportDenied)
        Bukkit.getPluginManager().callEvent(event)
        if (event.isCancelled) {
            info.message(event.denyMessage)
            return false
        }

        teleportAsyncWithWarmup(info.player!!, warp.dataLocation.getLocation(), Config.warpTeleportWarmupSeconds.toLong() * 20)
        info.message(Message.commandWarpsGoSuccess, warp.name, Config.warpTeleportWarmupSeconds.toString())
        return true
    }

    override fun getHelpInfo(): String {
        return Message.commandWarpsGoHelp
    }
}