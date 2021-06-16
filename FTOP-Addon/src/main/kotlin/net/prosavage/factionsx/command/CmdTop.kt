package net.prosavage.factionsx.command

import com.cryptomorin.xseries.XMaterial
import me.rayzr522.jsonmessage.JSONMessage
import net.prosavage.factionsx.FTOPAddon.Companion.factionValues
import net.prosavage.factionsx.calc.Response
import net.prosavage.factionsx.command.engine.CommandInfo
import net.prosavage.factionsx.command.engine.CommandRequirementsBuilder
import net.prosavage.factionsx.command.engine.FCommand
import net.prosavage.factionsx.manager.FactionManager
import net.prosavage.factionsx.manager.PlaceholderManager
import net.prosavage.factionsx.menus.TopMenu
import net.prosavage.factionsx.persist.FTOPConfig
import net.prosavage.factionsx.persist.FTOPConfig.priceFormat
import net.prosavage.factionsx.persist.color
import org.bukkit.entity.EntityType
import java.util.*
import java.util.concurrent.ForkJoinPool

class CmdTop : FCommand() {
    init {
        aliases.add("top")

        optionalArgs += Argument("menu", 0, StringArgument())

        commandRequirements = CommandRequirementsBuilder()
                .withRawPermission(FTOPConfig.ftopPermission)
                .build()
    }

    override fun execute(info: CommandInfo): Boolean {
        if (info.args.isEmpty()) {
            if (factionValues.isEmpty()) {
                info.message(FTOPConfig.commandTopNotCalculated)
                return false
            }

            factionValues.forEachAsync { index, value ->
                val faction = FactionManager.getFaction(value.faction)
                var tooltip = ""
                val line = PlaceholderManager.processPlaceholders(info.fPlayer, faction, FTOPConfig.ftopLineFormat)
                        .replace("{relational_tag}", PlaceholderManager.getRelationPrefix(info.fPlayer!!.getFaction(), faction) + faction.tag)
                        .replace("{rank}", index.toString())
                        .replace("{value}", priceFormat.format(value.latestCalculatedValue))
                        .replace("{bank_balance}", priceFormat.format(value.bankBalance))
                        .replace("{percentage_loss}", if (value.percentageLoss <= 0.0) "" else
                            FTOPConfig.ftopPercentageLoss.format(value.percentageLoss.toString()))

                if (!info.isPlayer()) {
                    info.message(line)
                    return@forEachAsync
                }

                for (ftopLine in FTOPConfig.ftopFactionEntryLore) {
                    var parsedLine = PlaceholderManager.processPlaceholders(info.fPlayer!!, faction, ftopLine)
                    value.otherMaterials.forEach { (xmat, amt) -> parsedLine = parsedLine.replace("{${xmat.name}}", amt.toString()) }
                    value.spawners.forEach { (entityType, amt) -> parsedLine = parsedLine.replace("{${entityType.name}}", amt.toString()) }
                    XMaterial.values().forEach { xmat -> parsedLine = parsedLine.replace("{${xmat.name}}", "0") }
                    EntityType.values().forEach { entityType -> parsedLine = parsedLine.replace("{${entityType.name}}", "0") }
                    tooltip += color("${if (tooltip.isEmpty()) "" else "\n"}$parsedLine")
                }

                JSONMessage.create(color(line)).tooltip(tooltip).send(info.player)
            }
            return true
        }

        // argument for per-fac calculation -> /f calc (cooldown)

        if (info.isPlayer() && info.args[0].equals("menu", ignoreCase = true)) {
            TopMenu.INVENTORY.open(info.player)
            return true
        }

        generateHelp(1, info.commandSender, arrayListOf())
        return true
    }

    override fun getHelpInfo(): String {
        return FTOPConfig.commandTopHelp
    }

    private fun Map<Long, Response>.forEachAsync(action: (Long, Response) -> Unit) =
        ForkJoinPool.commonPool().submit { for ((index, value) in this) action(index, value) }
}