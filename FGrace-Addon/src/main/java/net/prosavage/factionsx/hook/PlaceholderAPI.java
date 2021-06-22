package net.prosavage.factionsx.hook;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.prosavage.factionsx.FGraceAddon;
import net.prosavage.factionsx.manager.TimerManagerKt;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import static net.prosavage.factionsx.persist.GraceConfig.*;

public final class PlaceholderAPI extends PlaceholderExpansion {
    private final String version;

    public PlaceholderAPI(final String version) {
        this.version = version;
    }

    @Override
    public @NotNull String getAuthor() {
        return "ProSavage";
    }

    @Override
    public @NotNull String getVersion() {
        return this.version;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "graceaddon";
    }

    @Override
    public boolean canRegister() {
        return true;
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String onPlaceholderRequest(Player player, @NotNull String param) {
        switch (param) {
            case "active":
                return graceEnabled ? graceEnabledHolder : graceDisabledHolder;
            case "ends_at":
                return graceEnabled ? graceEnd : graceAlreadyEndedHolder;
            case "ends_at_pretty":
                return graceEnabled ? getFormattedEndTime() : graceAlreadyEndedHolder;
        }
        return null;
    }

    /**
     * Get the formatted grace time.
     *
     * @return {@link String} corresponding formatted time.
     */
    @NotNull
    private String getFormattedEndTime() {
        return TimerManagerKt.formatMillis(
                FGraceAddon.getGraceTimer().getTimeLeft(),
                timeDifferenceFormat
        );
    }
}