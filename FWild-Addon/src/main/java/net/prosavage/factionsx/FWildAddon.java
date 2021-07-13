package net.prosavage.factionsx;

import net.prosavage.factionsx.addonframework.AddonPlugin;
import net.prosavage.factionsx.addonframework.StartupResponse;
import net.prosavage.factionsx.cmd.CmdWild;
import net.prosavage.factionsx.persist.CooldownData;
import net.prosavage.factionsx.persist.WildConfig;

public class FWildAddon extends AddonPlugin {
    /**
     * Primary constructor;
     */
    public FWildAddon() {
        super(true);
    }

    @Override
    public StartupResponse onStart() {
        logColored("Enabling FWild-Addon.");
        FactionsX.baseCommand.addSubCommand(new CmdWild());
        logColored("Injected Command.");
        loadFiles();
        logColored("Loaded Configuration & Data Files.");
        return StartupResponse.ok();
    }

    private void loadFiles() {
        WildConfig.load(this);
        CooldownData.load(this);
    }

    private void saveFiles() {
        WildConfig.load(this);
        WildConfig.save(this);
        CooldownData.save(this);
    }

    @Override
    public void onTerminate() {
        logColored("Disabling FWild-Addon.");
        FactionsX.baseCommand.removeSubCommand(new CmdWild());
        logColored("Unregistered Command.");
        saveFiles();
        logColored("Saved Configuration and Data Files.");
    }
}
