package net.prosavage.factionsx

import net.prosavage.factionsx.addonframework.Addon
import net.prosavage.factionsx.cmd.CmdRoster
import net.prosavage.factionsx.persist.RosterConfig
import net.prosavage.factionsx.persist.RosterData

class FRosterAddon : Addon() {
    override fun onEnable() {
        logColored("Registering commands...")
        FactionsX.baseCommand.addSubCommand(CmdRoster(FactionsX.baseCommand))

        RosterConfig.load(this)
        RosterData.load(this)
    }

    override fun onDisable() {
        RosterConfig.load(this)
        RosterData.save(this)
    }

}

