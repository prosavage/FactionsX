package net.prosavage.factionsx.persist;

import net.prosavage.factionsx.addonframework.AddonPlugin;

import java.io.File;
import java.util.HashMap;
import java.util.UUID;

public class CooldownData {
    public static transient CooldownData instance = new CooldownData();

    public static HashMap<UUID, Long> cooldowns = new HashMap<>();

    public static void save(AddonPlugin addon) {
        addon.dataSerializer.save(instance, new File(addon.getDataFolder(), "cooldown-data.json"));
    }

    public static void load(AddonPlugin addon) {
        addon.dataSerializer.load(instance, CooldownData.class, new File(addon.getDataFolder(), "cooldown-data.json"));
    }
}