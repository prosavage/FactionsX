package net.prosavage.factionsx.command.factions.cmd.warp

import net.prosavage.factionsx.command.engine.CommandInfo
import net.prosavage.factionsx.command.engine.CommandRequirementsBuilder
import net.prosavage.factionsx.command.engine.FCommand
import net.prosavage.factionsx.persist.Message
import net.prosavage.factionsx.persist.config.EconConfig
import net.prosavage.factionsx.persist.data.getFLocation
import net.prosavage.factionsx.persist.data.wrappers.DataLocation
import net.prosavage.factionsx.util.MemberAction

class CmdWarpSet : FCommand() {
    init {
        aliases.add("set")

        requiredArgs.add(Argument("name", 0, StringArgument()))
        optionalArgs.add(Argument("password", 1, StringArgument()))
        optionalArgs.add(Argument("confirm-password", 2, StringArgument()))

        commandRequirements = CommandRequirementsBuilder()
                .withMemberAction(MemberAction.SET_WARP)
                .withPrice(EconConfig.fWarpSetCost)
                .build()
    }

    override fun execute(info: CommandInfo): Boolean {
        val location = info.player!!.location
        val factionAt = getFLocation(location).getFaction()
        val faction = info.faction!!

        if (factionAt != faction) {
            info.message(Message.commandWarpsSetNotInOwnClaim)
            return false
        }

        if (faction.getAllWarps().size > faction.getMaxWarps()) {
            info.message(Message.commandWarpsSetLimit)
            return false
        }

        val name = info.args[0]
        if (faction.hasWarp(name)) {
            info.message(Message.commandWarpsSetAlreadyExists, name)
            return false
        }

        var password: String? = null
        if (info.args.size > 1) {
            // No need to do null check since, we are only here if there are 2 or more args.
            password = info.args[1]
            // Null check here since it's the third arg.
            val confirmPassword = info.args.getOrNull(2) ?: run {
                info.message(Message.commandWarpsConfirmPassword)
                return false
            }
            if (password != confirmPassword) {
                info.message(Message.commandWarpsSetPasswordConfirmFailed)
                return false
            }
        }

        faction.setWarp(name.toLowerCase(), password, DataLocation(location.world!!.name, location.x, location.y, location.z))
        info.message(Message.commandWarpsSetSuccess, name)
        return true
    }

    override fun getHelpInfo(): String {
        return Message.commandWarpsSetHelp
    }
}