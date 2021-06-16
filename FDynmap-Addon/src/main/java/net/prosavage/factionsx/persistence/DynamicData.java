package net.prosavage.factionsx.persistence;

import net.prosavage.factionsx.map.WebPaint;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public final class DynamicData {
    // Instance of which will be used in saving & loading.
    private static final transient DynamicData INSTANCE = new DynamicData();

    // Instance of where this serialized configuration will be placed.
    private static final transient Function<File, File> PATH = parent -> new File(parent, "data.json");

    // This map contains all custom colors of factions.
    public static Map<Long, WebPaint> customColors = new HashMap<>();
}