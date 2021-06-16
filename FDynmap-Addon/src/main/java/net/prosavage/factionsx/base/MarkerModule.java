package net.prosavage.factionsx.base;

import net.prosavage.factionsx.core.Faction;
import net.prosavage.factionsx.helper.Executable;
import net.prosavage.factionsx.helper.MarkerUtil;
import net.prosavage.factionsx.manager.GridManager;
import net.prosavage.factionsx.map.WebPaint;
import net.prosavage.factionsx.persist.data.FLocation;
import org.dynmap.DynmapAPI;
import org.dynmap.markers.AreaMarker;
import org.dynmap.markers.MarkerSet;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static java.lang.Integer.parseInt;

public abstract class MarkerModule extends Executable {
    /**
     * {@link DynmapAPI} this will be used for base fetching etc.
     */
    @NotNull
    private final DynmapAPI dynamicMap;

    /**
     * {@link MarkerSet} this markerset will be used to mark areas around the map.
     */
    @NotNull
    private final MarkerSet markerSet;

    /**
     * {@link Map} this map contains all claimed areas.
     * <ul>
     *     <li>Key: Id of a faction ({@link Long})</li>
     *     <li>Value: Object containing both {@link FLocation} & {@link AreaMarker} ({@link ClaimArea})</li>
     * </ul>
     */
    @NotNull
    private final Map<Long, Set<ClaimArea>> claimedAreas = new HashMap<>();

    /**
     * Constructor.
     * Initialize our fields that will be used for different things, such as marking etc.
     */
    public MarkerModule(@NotNull final DynmapAPI dynamicApi, @NotNull final MarkerSet markerSet, final int threads) {
        // call the super constructor
        super(threads);

        // initialize our map fields
        this.dynamicMap = dynamicApi;
        this.markerSet = markerSet;
    }

    /**
     * Mark a faction's claim on the dynamic map.
     *
     * @param faction {@link Faction} instance of the faction to mark the claim by.
     * @param claim   {@link FLocation} instance of the flocation to be marked.
     * @param paint   {@link WebPaint} paint settings to be used when filling, stroking, etc.
     */
    public void mark(@NotNull final Faction faction, @NotNull final FLocation claim, @NotNull final WebPaint paint) {
        final AreaMarker marker = MarkerUtil.marker(markerSet, faction, claim.getWorld(), claim.getX(), claim.getZ());
        marker.setFillStyle(paint.fillOpacity, paint.fillColor);
        marker.setLineStyle(paint.strokeWeight, paint.strokeOpacity, paint.strokeColor);
        final ClaimArea area = new ClaimArea(claim, marker);
        claimedAreas.compute(faction.getId(), ($, areas) -> {
            if (areas == null) areas = ConcurrentHashMap.newKeySet();
            areas.add(area);
            return areas;
        });
    }

    /**
     * Mark a faction's claims on the dynamic map.
     *
     * @param faction {@link Faction} instance of the faction to mark all claims from.
     */
    public void mark(@NotNull final Faction faction) {
        final WebPaint paint = MarkerUtil.paintByFaction(faction);
        for (final FLocation claim : GridManager.INSTANCE.getAllClaims(faction))
            this.mark(faction, claim, paint);
    }

    /**
     * Unmark a claim by specific faction id and flocation object.
     *
     * @param id    {@link Long} of the faction to unmark.
     * @param claim {@link FLocation} to be unmarked.
     */
    public void unmark(final long id, @NotNull final FLocation claim) {
        final Set<ClaimArea> areas = claimedAreas.get(id);
        if (areas == null) return;

        final Iterator<ClaimArea> areaIterator = areas.iterator();
        boolean isRemoved = false;

        while (areaIterator.hasNext() && !isRemoved) {
            final ClaimArea claimArea = areaIterator.next();
            if (!claimArea.getLocation().equals(claim)) continue;

            claimArea.clear();
            areaIterator.remove();
            isRemoved = true;
        }

        if (areas.size() <= 0) claimedAreas.remove(id);
        else claimedAreas.put(id, areas);
    }

    /**
     * Update all {@link ClaimArea} labels of a single faction.
     *
     * @param faction {@link Faction} instance of the faction to modify labels for.
     */
    public void updateLabels(@NotNull final Faction faction) {
        for (final ClaimArea area : claimedAreas.get(faction.getId())) {
            final AreaMarker marker = area.getMarker();
            final FLocation location = area.getLocation();
            marker.setLabel(MarkerUtil.formatLabel(faction, location.getX(), location.getZ()));
        }
    }

    /**
     * Get a faction's set of claimed areas by their id.
     *
     * @param id {@link Long} of the faction to fetch the claimed areas from.
     * @return {@link Set} of areas if present, otherwise null.
     */
    @Nullable
    public Set<ClaimArea> remove(final long id) {
        return claimedAreas.remove(id);
    }

    /**
     * Get the dynamic map.
     *
     * @return {@link DynmapAPI} corresponding instance.
     */
    @NotNull
    public DynmapAPI getDynamicMap() {
        return this.dynamicMap;
    }
}