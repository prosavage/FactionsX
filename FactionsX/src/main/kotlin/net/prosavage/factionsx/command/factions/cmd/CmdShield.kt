package net.prosavage.factionsx.command.factions.cmd

import net.prosavage.factionsx.command.engine.CommandInfo
import net.prosavage.factionsx.command.engine.CommandRequirementsBuilder
import net.prosavage.factionsx.command.engine.FCommand
import net.prosavage.factionsx.gui.shield.ShieldMenu
import net.prosavage.factionsx.gui.shield.ShieldMenuTwo
import net.prosavage.factionsx.manager.formatMillis
import net.prosavage.factionsx.persist.Message
import net.prosavage.factionsx.persist.config.Config

class CmdShield : FCommand() {
    init {
        aliases.add("shield")

        commandRequirements = CommandRequirementsBuilder()
                .asFactionMember(true)
                .build()
    }

    override fun execute(info: CommandInfo): Boolean {
        if (!Config.factionShieldEnabled) {
            info.message(Message.commandShieldNotEnabled)
            return false
        }

        val faction = info.faction ?: return false
        val player = info.fPlayer!!
        //println(TimerManager.timedTasks.map { task -> task.key + " " + task.value.time })

        with(faction.shieldTimeStart) {
            if (this != null) return@with

            if (!player.isLeader()) {
                info.message(Message.commandShieldNotAllowed)
                return false
            }

            val precisePlayer = player.getPlayer() ?: return false
            if (Config.shieldMode.equals("PAGINATION", ignoreCase = true)) ShieldMenuTwo.INVENTORY.open(precisePlayer)
            else ShieldMenu.getInv(faction)?.open(precisePlayer)
            return true
        }

        if (faction.shielded) {
            info.message(Message.shieldEndsIn, formatMillis(faction.getShieldTime() ?: 1L))
            return false
        }

        info.message(Message.shieldStartsIn, formatMillis(faction.getNextShield() ?: 1L))
        return true
    }

    override fun getHelpInfo(): String {
        return Message.commandShieldHelp
    }
}