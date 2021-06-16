package net.prosavage.factionsx.helper;

import net.prosavage.factionsx.core.Faction;
import net.prosavage.factionsx.map.WebPaint;
import net.prosavage.factionsx.persistence.DynamicConfig;
import net.prosavage.factionsx.persistence.DynamicData;
import org.dynmap.markers.AreaMarker;
import org.dynmap.markers.MarkerSet;
import org.jetbrains.annotations.NotNull;

import static java.lang.String.format;
import static net.prosavage.factionsx.manager.FactionManager.SAFEZONE_ID;
import static net.prosavage.factionsx.manager.FactionManager.WARZONE_ID;

public final class MarkerUtil {
    /**
     * Mark a faction's claim area.
     *
     * @param markerSet {@link MarkerSet} this marker set is what the area will be bound to.
     * @param faction   {@link Faction} the owner of the claim.
     * @param world     {@link String} the world that the claim exists in.
     * @param x         {@link Double} X coordinate of the claimed chunk.
     * @param z         {@link Double} Z coordinate of the claimed chunk.
     * @return {@link AreaMarker} created area marker.
     */
    @NotNull
    public static AreaMarker marker(
            @NotNull final MarkerSet markerSet,
            @NotNull final Faction faction,
            @NotNull final String world,
            final long x, final long z
    ) {
        return markerSet.createAreaMarker(
                format("%s_%d_%d", faction.getTag(), x, z),
                formatLabel(faction, x, z),
                true, world, new double[]{x * 16, x * 16 + 15},
                new double[]{z * 16, z * 16 + 15}, false
        );
    }

    /**
     * Get a web paint correspondent to a faction.
     *
     * @param faction {@link Faction} whom's paint to fetch.
     * @return {@link WebPaint} corresponding web paint.
     */
    @NotNull
    public static WebPaint paintByFaction(@NotNull final Faction faction) {
        // extract the faction's id
        final long id = faction.getId();

        // check and return system factions' corresponding paint (if it is one)
        if (id == WARZONE_ID) return DynamicConfig.warZonePaint;
        else if (id == SAFEZONE_ID) return DynamicConfig.safeZonePaint;

        // it's not a system faction so let's continue with 'other'
        return DynamicData.customColors.getOrDefault(id, DynamicConfig.otherPaint);
    }

    /**
     * Format the label for an {@link AreaMarker}.
     *
     * @param faction {@link Faction} instance of the faction to format this label for.
     * @param x {@link Long} the X coordinate of the chunk.
     * @param z {@link Long} the Z coordinate of the chunk.
     * @return {@link String}
     */
    @NotNull
    public static String formatLabel(@NotNull final Faction faction, final long x, final long z) {
        return format(DynamicConfig.claimLabel, faction.getTag().replaceAll("(?i)&[0-9A-FK-ORX]", ""), x, z);
    }
}