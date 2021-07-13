package net.prosavage.factionsx

import net.prosavage.factionsx.addonframework.AddonPlugin
import net.prosavage.factionsx.addonframework.StartupResponse
import net.prosavage.factionsx.cmd.CmdRoster
import net.prosavage.factionsx.persist.RosterConfig
import net.prosavage.factionsx.persist.RosterData

class FRosterAddon : AddonPlugin(true) {
    override fun onStart(): StartupResponse {
        logColored("Registering commands...")
        FactionsX.baseCommand.addSubCommand(CmdRoster(FactionsX.baseCommand))

        RosterConfig.load(this)
        RosterData.load(this)

        return StartupResponse.ok()
    }

    override fun onTerminate() {
        RosterConfig.load(this)
        RosterData.save(this)
    }
}