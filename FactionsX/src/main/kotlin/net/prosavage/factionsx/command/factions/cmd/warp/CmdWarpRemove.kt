package net.prosavage.factionsx.command.factions.cmd.warp

import net.prosavage.factionsx.command.engine.CommandInfo
import net.prosavage.factionsx.command.engine.CommandRequirementsBuilder
import net.prosavage.factionsx.command.engine.FCommand
import net.prosavage.factionsx.core.Permission
import net.prosavage.factionsx.persist.Message
import net.prosavage.factionsx.persist.config.EconConfig
import net.prosavage.factionsx.util.MemberAction

class CmdWarpRemove : FCommand() {
    init {
        aliases.add("remove")

        requiredArgs.add(Argument("name", 0, WarpArgument()))

        commandRequirements = CommandRequirementsBuilder()
                .withPermission(Permission.WARP)
                .withMemberAction(MemberAction.DEL_WARP)
                .withPrice(EconConfig.fWarpRemoveCost)
                .build()
    }

    override fun execute(info: CommandInfo): Boolean {
        val name = info.args[0]
        val warp = info.faction!!.getWarp(name) ?: run {
            info.message(Message.commandWarpsRemoveDoesNotExist, name)
            return false
        }
        info.faction!!.removeWarp(warp.name)
        info.message(Message.commandWarpsRemoveSuccess, warp.name)
        return true
    }

    override fun getHelpInfo(): String {
        return Message.commandWarpsRemoveHelp
    }
}