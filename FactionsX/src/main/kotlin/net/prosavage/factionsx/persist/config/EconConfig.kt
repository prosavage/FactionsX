package net.prosavage.factionsx.persist.config

import net.prosavage.baseplugin.serializer.Serializer
import net.prosavage.factionsx.FactionsX
import net.prosavage.factionsx.persist.IConfigFile
import net.prosavage.factionsx.util.TransactionResponse
import java.io.File

object EconConfig : IConfigFile {

    @Transient
    val instance = this

    var economyEnabled = false
    var useVaultDirectlyIfNoMoneyInBank = false

    var bankLogLimitPerPage = 10
    var bankLogFormatHeader = "&7Your faction's bank logs, page &6%s &7out of &6%s&7:"
    var bankLogFormat = "&7 &l> &6#%1\$s &7%2\$s (&6$%3\$s, %4\$s&7), &6%5\$s ago"
    var bankLogPayFormat = "&7 &l> &6#%1\$s &7%2\$s (&6$%3\$s, %4\$s, %5\$s&7), &6%6\$s ago"
    var bankLogDateFormat = "MM/dd/yyyy HH:mm:ss"


    var econDisabledMsg = "Economy is disabled. Enable in \"econ-config.json\""
    var econNotEnoughMsg = "&7Not enough money to perform transaction (&e{price}&7)"

    var fDeinviteCost = 100.0
    var fDemoteCost = 100.0
    var fDescPrice = 100.0
    var fHomeCost = 100.0
    var fInviteCost = 100.0
    var fKickCost = 100.0
    var fPromoteCost = 100.0
    var fSetHomeCost = 100.0
    var fAllyCost = 100.0
    var fEnemyCost = 100.0
    var fNeutralCost = 100.0
    var fTruceCost = 100.0
    var fDiscordSetCost = 100.0
    var fPayPalSetCost = 100.0
    var fWarpGoCost = 100.0
    var fWarpRemoveCost = 100.0
    var fWarpSetCost = 100.0

    var claimLandCost = 10.0
    var factionCreateCost = 100.0
    override fun save(factionsx: FactionsX) {
        Serializer(false, factionsx.dataFolder, factionsx.logger)
                .save(instance, File("${factionsx.dataFolder}/config", "econ-config.json"))
    }

    override fun load(factionsx: FactionsX) {
        Serializer(false, factionsx.dataFolder, factionsx.logger)
                .load(EconConfig.instance, EconConfig::class.java, File("${factionsx.dataFolder}/config", "econ-config.json"))
    }
}