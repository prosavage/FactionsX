package net.prosavage.factionsx.persistence;

import net.prosavage.factionsx.map.WebPaint;

import java.io.File;
import java.util.function.Function;

public final class DynamicConfig {
    // Instance of which will be used in saving & loading.
    private static final transient DynamicData INSTANCE = new DynamicData();

    // Instance of where this serialized configuration will be placed.
    private static final transient Function<File, File> PATH = parent -> new File(parent, "config.json");

    /**
     * {@link String} this string contains the name of our command to change the color of a faction's claims.
     */
    public static String factionColorChangeCommandName = "mapcolor";

    /**
     * {@link String} this is the description for our command.
     */
    public static String factionColorChangeCommandDescription = "change the dynmap color for your faction";

    /**
     * {@link String} this message will be sent upon changing color.
     */
    public static String factionColorChangeCommandSuccess = "&7You have successfully changed your faction's dynmap color to &6%s&7.";

    /**
     * {@link String} this message will be sent upon failure.
     */
    public static String factionColorChangeCommandFail = "&7The format you have entered does not match a valid hex color code.";

    /**
     * {@link String} this is the label that will be used for areas on the dynamic map.
     */
    public static String claimLabel = "%s (x: %d, z: %d)";

    /**
     * {@link WebPaint} this paint will be used for warzone claims.
     */
    public static WebPaint warZonePaint = new WebPaint(
            12719901, .5,
            12719901, 1, 1
    );

    /**
     * {@link WebPaint} this paint will be used for safezone claims.
     */
    public static WebPaint safeZonePaint = new WebPaint(
            15385376, .5,
            15385376, 1, 1
    );

    /**
     * {@link WebPaint} this paint will be used for other claims unless faction has set their own.
     */
    public static WebPaint otherPaint = new WebPaint(
            16777215, .5,
            16777215, 1, 1
    );
}