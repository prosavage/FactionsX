package net.prosavage.factionsx

import net.prosavage.factionsx.addonframework.AddonPlugin
import net.prosavage.factionsx.addonframework.StartupResponse
import net.prosavage.factionsx.cmd.tnt.bank.CmdTntBank
import net.prosavage.factionsx.cmd.tnt.tntfill.CmdTntFill
import net.prosavage.factionsx.command.engine.ConfirmAction
import net.prosavage.factionsx.core.*
import net.prosavage.factionsx.event.ConfirmationEvent
import net.prosavage.factionsx.manager.SpecialActionManager
import net.prosavage.factionsx.manager.UpgradeManager
import net.prosavage.factionsx.persist.TNTAddonData
import net.prosavage.factionsx.persist.TNTConfig
import net.prosavage.factionsx.util.SpecialAction
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

class FTNTAddon : AddonPlugin(true) {
    companion object {
        val tntFillAction: SpecialAction by lazy { TNTFillAction() }
        val tntBankAction: SpecialAction by lazy { TNTBankAction() }
        val tntBankUpgrade: TNTBankUpgrade by lazy {
            TNTBankUpgrade(
                    TNTConfig.tntBankUpgrade.name,
                    TNTConfig.tntBankUpgrade.upgradeItem,
                    TNTConfig.tntBankUpgrade.upgradeMaxLevelLore,
                    TNTConfig.tntBankUpgrade.costLevel
            )
        }
    }

    override fun onStart(): StartupResponse {
        logColored("Enabling FTNT-Addon!")
        FactionsX.baseCommand.addSubCommand(CmdTntBank(FactionsX.baseCommand))
        FactionsX.baseCommand.addSubCommand(CmdTntFill(FactionsX.baseCommand))
        logColored("Injected Commands.")

        loadFiles()
        logColored("Loaded Configuration & Data Files.")

        SpecialActionManager.addSpecialAction(tntFillAction)
        SpecialActionManager.addSpecialAction(tntBankAction)
        logColored("Registered TNTBank & TNTFill special action.")

        UpgradeManager.registerUpgrade(TNTConfig.tntBankUpgrade.scope, tntBankUpgrade)
        logColored("Registered TNTBank Upgrade.")

        Bukkit.getServer().pluginManager.registerEvents(ConfirmationListener(), FactionsX.instance)
        return StartupResponse.ok()
    }

    fun getTNTAddonData()  = TNTAddonData.tntData

    private fun loadFiles() {
        TNTConfig.load(this)
        TNTAddonData.load(this)
    }

    override fun onTerminate() {
        logColored("Disabling FTNT-Addon!")
        SpecialActionManager.removeSpecialAction(tntFillAction)
        SpecialActionManager.removeSpecialAction(tntBankAction)
        logColored("Unregistered TNTBank & TNTFill special action.")
        FactionsX.baseCommand.removeSubCommand(CmdTntBank(FactionsX.baseCommand))
        FactionsX.baseCommand.removeSubCommand(CmdTntFill(FactionsX.baseCommand))
        logColored("Unregistered Commands.")
        UpgradeManager.deRegisterUpgrade(FactionsX.instance, tntBankUpgrade);
        logColored("Unregistered tnt bank upgrade.")
        saveFiles()
        logColored("Saved Configuration & Data Files.")
    }

    private fun saveFiles() {
        TNTConfig.load(this)
        TNTConfig.save(this)
        TNTAddonData.save(this)
    }

    inner class ConfirmationListener : Listener {
        @EventHandler
        fun ConfirmationEvent.onDisband() {
            if (this.action != ConfirmAction.DISBAND || !TNTConfig.notifyDisbandConfirmation) return

            val factionData = TNTAddonData.tntData.getTNTData(this.faction)
            if (!factionData.hasTnt(0)) return

            this.caller.message(TNTConfig.notifyDisbandConfirmationMessage, factionData.tntAmt.toString())
        }
    }
}

fun Faction.getTNTBank(): FactionTNTData.TNTBank {
    return TNTAddonData.tntData.getTNTData(this)
}