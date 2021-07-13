package net.prosavage.factionsx.persist;

import net.prosavage.factionsx.addonframework.AddonPlugin;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class GraceConfig {
    public static transient GraceConfig instance = new GraceConfig();

    public static boolean graceEnabled = true;
    public static boolean graceOnlyClaims = true;

    public static long graceTimeCheckTicks = 20;
    public static String dateFormat = "yyyy/MM/dd HH:mm:ss";
    public static String timeDifferenceFormat = "{days} days, {hours} hours, {minutes} minutes, and {seconds} seconds";
    public static String graceEnd = "2020/12/31 12:00:00";


    public static boolean sendGraceEndedAnnouncement = true;
    public static String graceEndedAnnouncement = "&7Grace period has ended, &6explosions are now &6&lENABLED&7.";
    public static String commandGraceHelp = "view grace timer.";
    public static String commandGraceTimer = "&7Grace time left: %1$s";
    public static String commandGraceDisabled = "&7Grace is &4disabled.";

    public static String commandAdminGraceHelp = "toggle grace period";
    public static String commandAdminGraceEnabled = "&7You have &6enabled &7the grace period.";
    public static String commandAdminGraceDisabled = "&7You have &6disabled &7the grace period.";

    public static String graceEnabledHolder = "true";
    public static String graceDisabledHolder = "false";
    public static String graceAlreadyEndedHolder = "now";

    public static Date getGraceEndDate() throws ParseException {
        return new SimpleDateFormat(dateFormat).parse(graceEnd);
    }

    public static long getGraceEndDifferenceRaw() throws ParseException {
        return getGraceEndDate().getTime() - new Date().getTime();
    }

    public static boolean hasGraceEnded(boolean sync, AddonPlugin addon) throws ParseException {
        if (!graceEnabled) return true;
        boolean ended = getGraceEndDifferenceRaw() <= 0L;
        if (sync) {
            if (graceEnabled == ended) {
                graceEnabled = !ended;
                save(addon);
            }
        }
        return ended;
    }

    public static String getGraceEndDifferenceFormatted() throws ParseException {
        long timeLeft = getGraceEndDifferenceRaw();
        return timeDifferenceFormat.replace("{days}", TimeUnit.MILLISECONDS.toDays(timeLeft) + "")
                .replace("{hours}", TimeUnit.MILLISECONDS.toHours(timeLeft) % TimeUnit.DAYS.toHours(1) + "")
                .replace("{minutes}", TimeUnit.MILLISECONDS.toMinutes(timeLeft) % TimeUnit.HOURS.toMinutes(1) + "")
                .replace("{seconds}", TimeUnit.MILLISECONDS.toSeconds(timeLeft) % TimeUnit.MINUTES.toSeconds(1) + "");
    }

    public static void save(AddonPlugin addon) {
        addon.configSerializer.save(instance, new File(addon.getDataFolder(), "grace-config.json"));
    }

    public static void load(AddonPlugin addon) {
        addon.configSerializer.load(instance, GraceConfig.class, new File(addon.getDataFolder(), "grace-config.json"));
    }
}