package net.prosavage.factionsx;

import net.prosavage.factionsx.addonframework.Addon;
import net.prosavage.factionsx.cmd.CmdWild;
import net.prosavage.factionsx.persist.CooldownData;
import net.prosavage.factionsx.persist.WildConfig;

public class FWildAddon extends Addon {

    private static Addon addonInstance;

    public static Addon getAddonInstance() {
        return addonInstance;
    }

    @Override
    protected void onEnable() {
        logColored("Enabling FWild-Addon.");
        addonInstance = this;
        FactionsX.baseCommand.addSubCommand(new CmdWild());
        logColored("Injected Command.");
        loadFiles();
        logColored("Loaded Configuration & Data Files.");
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
    protected void onDisable() {
        logColored("Disabling FWild-Addon.");
        FactionsX.baseCommand.removeSubCommand(new CmdWild());
        logColored("Unregistered Command.");
        saveFiles();
        logColored("Saved Configuration and Data Files.");
    }
}
