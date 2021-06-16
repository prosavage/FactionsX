package net.prosavage.factionsx.command.factions.cmd

import net.prosavage.factionsx.command.engine.CommandInfo
import net.prosavage.factionsx.command.engine.CommandRequirementsBuilder
import net.prosavage.factionsx.command.engine.FCommand
import net.prosavage.factionsx.core.Permission
import net.prosavage.factionsx.persist.Message
import net.prosavage.factionsx.util.MemberAction
import kotlin.time.ExperimentalTime

class CmdFly : FCommand() {
    init {
        aliases.add("fly")

        commandRequirements = CommandRequirementsBuilder()
                .withMemberAction(MemberAction.FLY)
                .withPermission(Permission.FLY)
                .build()
    }

    @ExperimentalTime
    override fun execute(info: CommandInfo): Boolean {
        val fPlayer = info.fPlayer!!

        if (info.player!!.allowFlight) {
            fPlayer.setFly(false)
            fPlayer.sendFlyUpdateMessage(fPlayer.isFFlying)
            return true
        }

        fPlayer.runFlyChecks(true, ignoreFlyStatus = false, preCheckNotification = true)
        return true
    }

    override fun getHelpInfo(): String {
        return Message.commandFlyHelp
    }
}