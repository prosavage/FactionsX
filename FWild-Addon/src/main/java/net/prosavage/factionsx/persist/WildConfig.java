package net.prosavage.factionsx.persist;

import com.cryptomorin.xseries.XMaterial;
import net.prosavage.factionsx.FWildAddon;
import net.prosavage.factionsx.FactionsX;
import net.prosavage.factionsx.addonframework.Addon;

import java.io.File;
import java.util.*;

public class WildConfig {

    public static transient WildConfig instance = new WildConfig();


    public static EnumSet<XMaterial> unsafeBlocks = EnumSet.of(
            XMaterial.WATER,
            XMaterial.LAVA,
            XMaterial.MAGMA_BLOCK,
            XMaterial.VOID_AIR
    );


    public static int centerX = 0;
    public static int centerZ = 0;

    public static int radiusInChunks = 100;
    public static int minimumXChunkRadius = 0;
    public static int minimumZChunkRadius = 0;

    public static String commandWildHelp = "teleport to a random location.";

    public static boolean useCooldown = true;
    public static int cooldownSeconds = 60 * 5;
    public static String coolDownDeny = "&7Wild Teleport is on cooldown for &6%1$s&7 seconds.";
    public static boolean useCoolDownBypassPermission = true;
    public static String cooldownBypassPermission = "factionsx.wild-cooldown-bypass";


    public static boolean usePermission = false;
    public static String wildPermission = "factionsx.wild";

    public static String teleportWarmupMessage = "&7Teleporting in &63&7 seconds...";
    public static int teleportWarmupInSeconds = 3;

    public static Map<String, String> teleportFromWorldToWorld = new HashMap<>();
    public static List<String> blacklistedWorlds = new ArrayList<>();
    public static String blacklistedWorldMessage = "&7This world is blacklisted.";
    public static String unsafeLocation = "&7Teleport location is unsafe, cancelled.";
    public static String successMessage = "&7Teleported successfully.";
    public static boolean automaticRetry = true;
    public static String automaticRetryMessage = "&7Retrying in %1$s seconds.";
    public static long automaticRetryDelay = 10L;

    static {
        teleportFromWorldToWorld.put("cool-world", "change-to-this-world");
    }

    static {
        blacklistedWorlds.add("example-world");
        blacklistedWorlds.add("example-2");
        blacklistedWorlds.add("example-3");
    }

    public static void save(Addon addon) {
        addon.getConfigSerializer().save(instance, new File(FWildAddon.getAddonInstance().getAddonDataFolder(), "config.json"));
    }

    public static void load(Addon addon) {
        FactionsX.baseCommand.getHelpInfo();
        addon.getConfigSerializer().load(instance, WildConfig.class, new File(FWildAddon.getAddonInstance().getAddonDataFolder(), "config.json"));
    }


}
