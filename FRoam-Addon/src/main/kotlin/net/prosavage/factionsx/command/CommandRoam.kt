package net.prosavage.factionsx.command

import club.rarlab.classicplugin.extension.hasCooldown
import club.rarlab.classicplugin.extension.setCooldown
import net.prosavage.factionsx.command.engine.CommandInfo
import net.prosavage.factionsx.command.engine.FCommand
import net.prosavage.factionsx.helper.disableRoam
import net.prosavage.factionsx.helper.enableRoam
import net.prosavage.factionsx.helper.isAllowedInRegion
import net.prosavage.factionsx.helper.isRoaming
import net.prosavage.factionsx.persist.data.getFLocation
import net.prosavage.factionsx.persistence.config.Config
import net.prosavage.factionsx.persistence.config.Config.executableCooldown
import net.prosavage.factionsx.persistence.config.Config.messageWithPrefix
import net.prosavage.factionsx.persistence.config.Message

/**
 * Class to handle the '/f roam' sub command.
 */
class CommandRoam : FCommand() {
    // Preparation for the sub command.
    init {
        this.aliases += "roam"

        this.commandRequirements = buildRequirements {
            this.asPlayer = true
            this.asFactionMember = true
            this.rawPermission = "factionsx.roam"
        }
    }

    // Called when the sub command gets executed.
    override fun execute(info: CommandInfo): Boolean {
        val fPlayer = info.fPlayer ?: return false
        val player = info.player ?: return false

        if (!Config.toggleWithinWorldGuardRegion && !player.isAllowedInRegion(getFLocation(player.location))) {
            info.message(Message.worldGuardReject, messageWithPrefix)
            return false
        }

        if (fPlayer.isRoaming()) {
            if (executableCooldown > 0) player.setCooldown("command_roam")
            info.message(Message.roamDisabled, messageWithPrefix)
            return fPlayer.disableRoam()
        }

        if (executableCooldown > 0 && player.hasCooldown("command_roam")) {
            info.message(Message.commandCooldown, messageWithPrefix)
            return false
        }

        info.message(Message.roamEnabled, messageWithPrefix)
        return fPlayer.enableRoam(Config.timeTillExpire)
    }

    // String to be displayed upon help message.
    override fun getHelpInfo(): String = "toggle your roam mode"
}