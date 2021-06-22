package net.prosavage.factionsx;

import net.prosavage.factionsx.addonframework.Addon;
import net.prosavage.factionsx.cmd.CmdGrace;
import net.prosavage.factionsx.cmd.admin.CmdAdminGrace;
import net.prosavage.factionsx.hook.PlaceholderAPI;
import net.prosavage.factionsx.listener.ExplosionListener;
import net.prosavage.factionsx.manager.TimeTask;
import net.prosavage.factionsx.manager.TimerManager;
import net.prosavage.factionsx.persist.GraceConfig;
import net.prosavage.factionsx.util.UtilKt;
import org.bukkit.Bukkit;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class FGraceAddon extends Addon {

    public static final String GRACE_TASK_ID = "GRACE";
    private static Addon addonInstance;

    public static Addon getAddonInstance() {
        return addonInstance;
    }

    public static TimeTask getGraceTimer() {
        return TimerManager.INSTANCE.getTimeTask(GRACE_TASK_ID);
    }

    @Override
    protected void onEnable() {
        logColored("Enabling FGrace-Addon.");
        addonInstance = this;
        loadFiles();
        Date graceEndDate = null;
        logColored("Loaded Configuration & Data Files.");
        try {
            graceEndDate = GraceConfig.getGraceEndDate();
            logColored("Grace end is set @ " + new SimpleDateFormat(GraceConfig.dateFormat).format(graceEndDate));
            logColored("Server timezone is " + TimeZone.getDefault().getDisplayName());
            logColored("Time to Grace End:");
            logColored(GraceConfig.getGraceEndDifferenceFormatted());
            if (!GraceConfig.graceEnabled) {
                logColored("&4GRACE IS DISABLED IN CONFIG.");
            } else {
                logColored("Grace " + (GraceConfig.hasGraceEnded(true) ? "&chas ended" : "&ais on countdown"));
            }
        } catch (ParseException e) {
            logColored("INVALID formatting for graceEnd option in config!");
            e.printStackTrace();
        }
        FactionsX.baseCommand.addSubCommand(new CmdGrace());
        FactionsX.baseAdminCommand.addSubCommand(new CmdAdminGrace(this));
        logColored("Injected commands.");
        Bukkit.getServer().getPluginManager().registerEvents(new ExplosionListener(), addonInstance.getFactionsXInstance());
        logColored("Registered Grace Listener.");

        if (GraceConfig.graceEnabled) {
            this.enableTask(true);
        }

        logColored("Hooking...");
        this.hook();
    }

    public void enableTask(final boolean logUnable) {
        try {
            final Date graceEndDate = GraceConfig.getGraceEndDate();
            if (graceEndDate != null) {
                TimerManager.INSTANCE.removeTask("GRACE");
                TimerManager.INSTANCE.registerTimeTask("GRACE", new TimeTask(graceEndDate, () -> {
                    if (GraceConfig.graceEnabled && GraceConfig.sendGraceEndedAnnouncement) {
                        Bukkit.broadcastMessage(UtilKt.color(GraceConfig.graceEndedAnnouncement));
                    }
                    GraceConfig.graceEnabled = false;
                }));
                return;
            }
        } catch (Exception ignored) {}

        if (!logUnable) return;
        logColored("Unable to register Grace Timer due to invalid date.");
    }

    private void hook() {
        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            new PlaceholderAPI(this.getFactionsXInstance().getDescription().getVersion()).register();
            logColored("Hooked into PlaceholderAPI.");
        }
    }

    private void loadFiles() {
        GraceConfig.load(this);
    }

    @Override
    protected void onDisable() {
        logColored("Disabling FGrace-Addon.");
        saveFiles();
        logColored("Saved Configuration and Data Files.");
        FactionsX.baseCommand.removeSubCommand(new CmdGrace());
    }

    private void saveFiles() {
        GraceConfig.load(this);
        GraceConfig.save(this);
    }
}
