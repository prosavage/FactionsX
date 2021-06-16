package net.prosavage.factionsx.map;

import org.dynmap.DynmapAPI;
import org.dynmap.markers.MarkerSet;
import org.jetbrains.annotations.NotNull;

interface MapSettings {
    /**
     * {@link String} this constant will be used as the default {@link MarkerSet} id.
     */
    String MARKER_ID = "factionsx-dynamic-markerset";

    /**
     * {@link String} this constant will be used as the default {@link MarkerSet} label.
     */
    String MARKER_LABEL = "factionsx-dynamic-claims";

    /**
     * Create the default MarkerSet by passed API instance
     * and constants, {@link MapSettings#MARKER_ID} & {@link MapSettings#MARKER_LABEL}.
     *
     * @param dynamicMap {@link DynmapAPI} instance to be used for creation.
     * @return {@link MarkerSet} freshly created MarkerSet.
     */
    @NotNull
    static MarkerSet defaultMarker(@NotNull final DynmapAPI dynamicMap) {
        return dynamicMap.getMarkerAPI().createMarkerSet(
                MARKER_ID, MARKER_LABEL,
                dynamicMap.getMarkerAPI().getMarkerIcons(), false
        );
    }
}