package net.prosavage.factionsx.base;

import net.prosavage.factionsx.persist.data.FLocation;
import org.dynmap.markers.AreaMarker;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public final class ClaimArea implements TracableMarker {
    /**
     * {@link FLocation} the faction location of the claimed area.
     */
    @NotNull
    private final FLocation location;

    /**
     * {@link AreaMarker} the area marker that corresponds to this claim.
     */
    @NotNull
    private final AreaMarker marker;

    /**
     * Constructor.
     * Initialize our fields.
     *
     * @param location faction claim to be set.
     * @param marker   area marker to be traced for this claim.
     */
    public ClaimArea(@NotNull final FLocation location, @NotNull final AreaMarker marker) {
        this.location = location;
        this.marker = marker;
    }

    /**
     * Get the location of this claim area.
     *
     * @return {@link FLocation} corresponding location.
     */
    @NotNull
    public FLocation getLocation() {
        return this.location;
    }

    /**
     * Get the marker of this claim area.
     *
     * @return {@link AreaMarker}
     */
    @NotNull
    public AreaMarker getMarker() {
        return this.marker;
    }

    /**
     * Clear the marker.
     **/
    @Override
    public void clear() {
        marker.deleteMarker();
    }

    /**
     * Overriden method for base handling.
     **/
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClaimArea claimArea = (ClaimArea) o;
        return getLocation().equals(claimArea.getLocation()) &&
                marker.equals(claimArea.marker);
    }

    /**
     * Overriden method for base handling.
     **/
    @Override
    public int hashCode() {
        return Objects.hash(getLocation(), marker);
    }
}