package net.prosavage.factionsx.persist;

import net.prosavage.factionsx.FWildAddon;
import net.prosavage.factionsx.addonframework.Addon;

import java.io.File;
import java.util.HashMap;
import java.util.UUID;

public class CooldownData {
    public static transient CooldownData instance = new CooldownData();

    public static HashMap<UUID, Long> cooldowns = new HashMap<>();


    public static void save(Addon addon) {
        addon.getDataSerializer().save(instance, new File(FWildAddon.getAddonInstance().getAddonDataFolder(), "cooldown-data.json"));
    }

    public static void load(Addon addon) {
        addon.getDataSerializer().load(instance, CooldownData.class, new File(FWildAddon.getAddonInstance().getAddonDataFolder(), "cooldown-data.json"));
    }
}
