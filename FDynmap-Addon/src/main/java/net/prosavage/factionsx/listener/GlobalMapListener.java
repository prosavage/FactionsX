package net.prosavage.factionsx.listener;

import net.prosavage.factionsx.core.Faction;
import net.prosavage.factionsx.event.*;
import net.prosavage.factionsx.helper.MarkerUtil;
import net.prosavage.factionsx.map.MapBase;
import net.prosavage.factionsx.persistence.DynamicData;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

public final class GlobalMapListener implements Listener {
    /**
     * {@link MapBase} instance to be used for marking the map.
     */
    @NotNull
    private final MapBase mapBase;

    /**
     * Constructor.
     * Initialize our field.
     *
     * @param mapBase {@link MapBase} instance to be used for marking.
     */
    public GlobalMapListener(@NotNull final MapBase mapBase) {
        this.mapBase = mapBase;
    }

    /**
     * This event is called when a faction unclaims a chunk.
     *
     * @param event {@link FactionUnClaimEvent}
     */
    @EventHandler
    private void onUnclaim(final FactionUnClaimEvent event) {
        final Faction faction = event.getFactionUnClaiming();
        mapBase.callAsync(() -> mapBase.unmark(faction.getId(), event.getFLocation()));
    }

    /**
     * This event is called when a faction unclaims all their chunks.
     *
     * @param event {@link FactionUnClaimAllEvent}
     */
    @EventHandler
    private void onUnclaimAll(final FactionUnClaimAllEvent event) {
        final Faction faction = event.getUnclaimingFaction();
        mapBase.callAsync(() -> mapBase.massiveUnmark(faction.getId()));
    }

    /**
     * This event is called when a faction pre-claims a chunk.
     *
     * @param event {@link FactionPreClaimEvent}
     */
    @EventHandler
    private void onClaim(final FactionPreClaimEvent event) {
        final Faction faction = event.getFactionClaiming();
        mapBase.callAsync(() -> mapBase.mark(
                faction, event.getFLocation(),
                MarkerUtil.paintByFaction(faction))
        );
    }

    /**
     * This event is called when a faction gets renamed.
     *
     * @param event {@link FactionRenameEvent}
     */
    @EventHandler
    private void onRename(final FactionRenameEvent event) {
        final Faction faction = event.getFaction();
        mapBase.callAsync(() -> mapBase.updateLabels(faction));
    }

    /**
     * This event is called when a faction gets disbanded.
     *
     * @param event {@link FactionPreDisbandEvent}
     */
    @EventHandler
    private void onDisband(final FactionPreDisbandEvent event) {
        final Faction faction = event.getFaction();
        final long id = faction.getId();

        mapBase.callAsync(() -> mapBase.massiveUnmark(id));
        DynamicData.customColors.remove(id);
    }
}