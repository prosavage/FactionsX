package net.prosavage.factionsx.map;

import net.prosavage.factionsx.base.ClaimArea;
import net.prosavage.factionsx.base.MarkerModule;
import net.prosavage.factionsx.core.Faction;
import net.prosavage.factionsx.manager.FactionManager;
import org.dynmap.DynmapAPI;
import org.jetbrains.annotations.NotNull;

import java.lang.management.ManagementFactory;
import java.util.Set;

import static net.prosavage.factionsx.manager.FactionManager.WILDERNESS_ID;
import static org.bukkit.Bukkit.getPluginManager;

public final class MapBase extends MarkerModule {
    /**
     * Constructor.
     * Initialize our fields that will be used for different things, such as marking etc.
     */
    public MapBase(@NotNull final DynmapAPI dynamicApi) {
        // call the super constructor
        super(
                dynamicApi,
                MapSettings.defaultMarker(dynamicApi),
                ManagementFactory.getThreadMXBean().getThreadCount()
        );
    }

    /**
     * Get whether or not the Dynamic Map plugin is enabled
     * on the corresponding server.
     *
     * @return {@link Boolean} whether or not dynmap is present and enabled.
     */
    public static boolean attemptRegistration() {
        return getPluginManager().isPluginEnabled("dynmap");
    }

    /**
     * Mark all available factions.
     */
    public void markAll() {
        for (final Faction faction : FactionManager.INSTANCE.getFactions()) {
            if (faction.getId() == WILDERNESS_ID) continue;
            this.mark(faction);
        }
    }

    /**
     * Unmark all claims by a specific faction's id.
     *
     * @param id {@link Long} of the faction to commence a massive unmark on.
     */
    public void massiveUnmark(final long id) {
        final Set<ClaimArea> areas = remove(id);
        if (areas == null) return;
        for (final ClaimArea claimArea : areas) claimArea.clear();
    }

    /**
     * Unmark all claims from the map that are not from the faction Wilderness.
     */
    public void unmarkAll() {
        for (Faction faction : FactionManager.INSTANCE.getFactions()) {
            if (faction.getId() == WILDERNESS_ID) continue;
            massiveUnmark(faction.getId());
        }
    }
}