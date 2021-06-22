package net.prosavage.factionsx.listener;

import net.prosavage.factionsx.manager.GridManager;
import net.prosavage.factionsx.persist.GraceConfig;
import net.prosavage.factionsx.persist.data.FLocation;
import net.prosavage.factionsx.persist.data.FactionsKt;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;

public class ExplosionListener implements Listener {
    /**
     * Called when an {@link org.bukkit.entity.Entity} explodes.
     *
     * @param event {@link EntityExplodeEvent}
     */
    @EventHandler
    public void onExplode(EntityExplodeEvent event) {
        if (!GraceConfig.graceEnabled) return;

        if (GraceConfig.graceOnlyClaims) {
            event.blockList().removeIf(it -> {
                final FLocation location = FactionsKt.getFLocation(it.getLocation());
                return !GridManager.INSTANCE.getFactionAt(location).isWilderness();
            });
            return;
        }

        event.setCancelled(true);
    }
}