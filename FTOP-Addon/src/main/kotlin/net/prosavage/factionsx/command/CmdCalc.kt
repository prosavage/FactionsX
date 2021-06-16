package net.prosavage.factionsx.command

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import net.prosavage.factionsx.FTOPAddon.Companion.factionValues
import net.prosavage.factionsx.calc.ValueCalculatorTask
import net.prosavage.factionsx.command.engine.CommandInfo
import net.prosavage.factionsx.command.engine.CommandRequirementsBuilder
import net.prosavage.factionsx.command.engine.FCommand
import net.prosavage.factionsx.core.Faction
import net.prosavage.factionsx.manager.TimeTask
import net.prosavage.factionsx.manager.TimerManager
import net.prosavage.factionsx.manager.formatMillis
import net.prosavage.factionsx.calc.recalculate
import net.prosavage.factionsx.persist.FTOPConfig
import net.prosavage.factionsx.util.processInfo
import java.time.Instant.now
import java.util.Date.from

class CmdCalc : FCommand() {
    init {
        aliases.add("calc")

        commandRequirements = CommandRequirementsBuilder()
                .asPlayer(true)
                .asFactionMember(true)
                .asLeader(true)
                .withRawPermission(FTOPConfig.calcPermission)
                .build()
    }

    override fun execute(info: CommandInfo): Boolean {
        val faction = info.faction!!

        if (faction.claimAmt <= 0) {
            info.message(FTOPConfig.commandCalcNoClaims)
            return false
        }

        faction.calculateCoolDownTask?.run {
            info.message(FTOPConfig.commandCalcCoolDown, formatMillis(this.getTimeLeft(), FTOPConfig.calcCoolDownFormat))
            return false
        }

        this.applyCoolDown(faction)
        GlobalScope.launch {
            val calcInfo = faction.recalculate()
            val time = "%.2f".format(calcInfo.finishedAt)

            ValueCalculatorTask.sortValues(factionValues.values.filter { it.faction != faction.id }.plus(calcInfo))
            val index = factionValues.entries.find { it.value == calcInfo }?.key ?: -1L

            FTOPConfig.commandCalcSuccess.forEach { message ->
                info.message(processInfo(faction, calcInfo, index, message.replace("{time}", time)))
            }
        }
        return true
    }

    /**
     * Get whether or not a [Faction]'s calculation is available.
     */
    private val Faction.calculateCoolDownTask: TimeTask?
        get() = TimerManager.getTimeTask("faction_calc_$id")

    /**
     * Apply calculate cooldown to a [Faction].
     */
    private fun applyCoolDown(faction: Faction) {
        TimerManager.registerTimeTask("faction_calc_${faction.id}", TimeTask(
                from(now().plusSeconds(FTOPConfig.manualCalcCoolDownSeconds.toLong())),
                Runnable {}
        ))
    }

    override fun getHelpInfo(): String {
        return FTOPConfig.commandCalcHelp
    }
}