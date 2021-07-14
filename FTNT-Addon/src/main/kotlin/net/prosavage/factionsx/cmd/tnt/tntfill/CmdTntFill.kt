package net.prosavage.factionsx.cmd.tnt.tntfill

import com.cryptomorin.xseries.XMaterial
import net.prosavage.factionsx.cmd.argument.TNTInInventoryArgument
import net.prosavage.factionsx.cmd.argument.TNTRadiusArgument
import net.prosavage.factionsx.command.engine.CommandInfo
import net.prosavage.factionsx.command.engine.CommandRequirementsBuilder
import net.prosavage.factionsx.command.engine.FCommand
import net.prosavage.factionsx.core.Faction
import net.prosavage.factionsx.core.TNTFillAction
import net.prosavage.factionsx.persist.TNTAddonData
import net.prosavage.factionsx.persist.TNTConfig
import net.prosavage.factionsx.persist.data.getFLocation
import org.bukkit.GameMode
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.World
import org.bukkit.block.Block
import org.bukkit.block.Dispenser
import org.bukkit.inventory.ItemStack


class CmdTntFill(parent: FCommand) : FCommand() {

    init {
        aliases.add("tntfill")

        requiredArgs.add(Argument("radius", 0, TNTRadiusArgument()))

        requiredArgs.add(Argument("amount", 1, TNTInInventoryArgument()))

        commandRequirements = CommandRequirementsBuilder()
            .asFactionMember(true)
            .withSpecialAction(TNTFillAction())
            .withRawPermission(TNTConfig.tntFillCommandPermission)
            .build()
    }


    override fun execute(info: CommandInfo): Boolean {
        val radius = info.getArgAsInt(0) ?: return false
        if (radius > TNTConfig.tntFillMaxRadius) {
            info.message(TNTConfig.commandTntFillRadiusTooHigh, TNTConfig.tntFillMaxRadius.toString())
            return false
        }

        val amountOfTntToFill = info.getArgAsInt(1) ?: return false
        if (amountOfTntToFill <= 0) {
            info.message(TNTConfig.commandTntCannotBeNegaitve)
            return false
        }

        val tntData = TNTAddonData.tntData.getTNTData(info.faction!!)
        val nearbyResult = getNearbyDispensers(info.faction!!, info.player!!.location, radius)

        val nearbyDispensers = nearbyResult.first.ifEmpty {
            // if this is true there were dispensers however, they were in another faction's land.
            if (nearbyResult.second) {
                info.message(TNTConfig.commandTntFillNotYourDispensers)
            } else {
                info.message(TNTConfig.commandTntFillNoneFound)
            }
            return false
        }

        info.message(TNTConfig.commandTntFillDispensers, nearbyDispensers.size.toString())
        val totalTnT = info.fPlayer!!.getAmountOfMaterialInPlayerInv(XMaterial.TNT) + tntData.tntAmt

        var newAmount = if (totalTnT > amountOfTntToFill) amountOfTntToFill else totalTnT
        val amountToTake = newAmount
        val gameMode = info.player!!.gameMode

        val amountForEach = amountOfTntToFill / nearbyDispensers.size
        var amountRemainder = amountOfTntToFill % nearbyDispensers.size

        for (dispenser in nearbyDispensers) {
            if (gameMode != GameMode.CREATIVE && newAmount <= 0) {
                info.message(TNTConfig.commandTntFillFailed)
                break
            }

            var totalFailed = 0
            val amountFromRemainder = if (amountRemainder > 0) 1 else 0

            val preciseValue = amountForEach + amountFromRemainder
            if (preciseValue == 0) break

            dispenser.inventory.addItem(ItemStack(Material.TNT, preciseValue))
                .forEach { item -> totalFailed += item.value.amount }

            newAmount -= (amountForEach + amountFromRemainder)
            if (amountRemainder > 0) amountRemainder -= amountFromRemainder
        }

        if (gameMode != GameMode.CREATIVE) {
            val failedToTakeTntAmount = info.fPlayer!!.takeAmountOfMaterialFromPlayerInv(XMaterial.TNT, amountToTake)
            tntData.takeTnt(failedToTakeTntAmount)
        }

        info.message(TNTConfig.commandTntFillComplete, amountToTake.toString())
        return true
    }


    // Honestly the pair result is fucking dirty but it's fine since we only use this function in one place.
    private fun getNearbyDispensers(faction: Faction, center: Location, radius: Int): Pair<Set<Dispenser>, Boolean> {
        val world: World = center.world!!
        val result = hashSetOf<Dispenser>()
        var foundDispenserInAnotherFactionsLand = false
        for (x in center.blockX - radius until center.blockX + radius) {
            for (z in center.blockZ - radius until center.blockZ + radius) {
                for (y in center.blockY - radius until center.blockY + radius) {
                    val target: Block = world.getBlockAt(x, y, z)
                    if (target.state is Dispenser) {
                        val dispenser: Dispenser = target.state as Dispenser
                        if (TNTConfig.blockTntFillInOtherFactionLand && getFLocation(target.location).getFaction() != faction) {
                            foundDispenserInAnotherFactionsLand = true
                            continue
                        }
                        result.add(dispenser)
                    }
                }
            }
        }
        return Pair(result, foundDispenserInAnotherFactionsLand)
    }


    override fun getHelpInfo(): String {
        return TNTConfig.commandTntFillHelp
    }
}