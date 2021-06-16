package net.prosavage.factionsx.command

import net.prosavage.factionsx.command.engine.CommandInfo
import net.prosavage.factionsx.command.engine.CommandRequirementsBuilder
import net.prosavage.factionsx.command.engine.FCommand
import net.prosavage.factionsx.disablePrinter
import net.prosavage.factionsx.enablePrinter
import net.prosavage.factionsx.isInPrinter
import net.prosavage.factionsx.persist.PrinterConfig
import net.prosavage.factionsx.persist.data.getFLocation

class CmdPrinter : FCommand() {

    init {
        aliases.add("printer")

        optionalArgs.add(Argument("toggle (enabled/disabled)", 0, BooleanArgument()))

        commandRequirements = CommandRequirementsBuilder().withRawPermission(PrinterConfig.printerCommandPermission)
                .asPlayer(true)
                .asFactionMember(true)
                .build()
    }


    override fun execute(info: CommandInfo): Boolean {
        val fPlayer = info.fPlayer!!
        val toggle = info.getArgAsBoolean(0, informIfNot = false) ?: !fPlayer.isInPrinter()
        if (toggle) {
            if (fPlayer.isInPrinter()) {
                info.message(PrinterConfig.commandPrinterAlreadyEnabled)
                return false
            }
            val player = info.player!!
            val location = player.location
            val fLocation = getFLocation(location)
            val faction = fLocation.getFaction()
            if (PrinterConfig.canOnlyPrintInOwnTerritory && info.faction != faction) {
                info.message(PrinterConfig.commandPrinterCanOnlyPrintInOwn)
                return false
            }
            if (fPlayer.getEnemiesNearby(PrinterConfig.printerEnemyNearByBlocks).isNotEmpty()) {
                info.message(PrinterConfig.printerDisabledEnemy)
                return false
            }
            val inventory = player.inventory
            if (inventory.helmet != null
                    || inventory.chestplate != null
                    || inventory.leggings != null
                    || inventory.boots != null) {
                info.message(PrinterConfig.commandPrinterArmorOff)
                return false
            }
            fPlayer.enablePrinter()
        } else {
            if (!fPlayer.isInPrinter()) {
                info.message(PrinterConfig.commandPrinterAlreadyDisabled)
                return false
            }
            fPlayer.disablePrinter()
        }
        info.message(
                PrinterConfig.commandPrinterToggle,
                if (toggle) PrinterConfig.commandPrinterEnabled else PrinterConfig.commandPrinterDisabled
        )
        return true
    }

    override fun getHelpInfo(): String {
        return PrinterConfig.commandPrinterHelp
    }
}