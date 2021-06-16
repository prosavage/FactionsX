package net.prosavage.factionsx.command;

import com.google.common.collect.Lists;
import net.prosavage.factionsx.command.engine.CommandInfo;
import net.prosavage.factionsx.command.engine.CommandRequirementsBuilder;
import net.prosavage.factionsx.command.engine.FCommand;
import net.prosavage.factionsx.core.FPlayer;
import net.prosavage.factionsx.core.Faction;
import net.prosavage.factionsx.map.MapBase;
import net.prosavage.factionsx.map.WebPaint;
import net.prosavage.factionsx.persist.config.Config;
import net.prosavage.factionsx.persistence.DynamicData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static java.lang.Integer.parseInt;
import static net.prosavage.factionsx.persistence.DynamicConfig.*;

public final class MapColorCommand extends FCommand {
    /**
     * {@link MapBase} the only map base instance needed.
     */
    @NotNull private final MapBase mapBase;

    /**
     * Set up the permission, command name etc..
     */
    public MapColorCommand(@NotNull final MapBase mapBase) {
        // initialize map base
        this.mapBase = mapBase;

        // set prefix
        this.getAliases().add(factionColorChangeCommandName);

        // add argument
        this.getRequiredArgs().add(new Argument("color", 0, ColorArgument.INSTANCE));

        // build requirements
        this.commandRequirements = new CommandRequirementsBuilder()
                .asFactionMember(true)
                .asLeader(true)
                .withRawPermission(Config.INSTANCE.getFactionsPermissionPrefix() + ".dynmap.colorchange")
                .build();
    }

    /**
     * Invoked when this command is performed.
     */
    @Override public boolean execute(@NotNull CommandInfo info) {
        final Faction faction = info.getFaction();
        final String color = info.getArgs().get(0);

        if (!color.matches("^[0-9a-fA-F]{1,6}$")) {
            info.message(factionColorChangeCommandFail);
            return false;
        }

        final int exactColor = parseInt(color, 16);
        final WebPaint paint = new WebPaint(exactColor, 0.5, exactColor, 1, 1);

        DynamicData.customColors.put(faction.getId(), paint);
        info.message(factionColorChangeCommandSuccess, color);

        // remark
        mapBase.callAsync(() -> {
            mapBase.massiveUnmark(faction.getId());
            mapBase.mark(faction);
        });
        return false;
    }

    /**
     * Invoked when the information about this command is required.
     */
    @Override public String getHelpInfo() {
        return factionColorChangeCommandDescription;
    }

    /**
     * Color argument class.
     */
    private static final class ColorArgument extends ArgumentType {
        // one and only instance
        public static final ArgumentType INSTANCE = new ColorArgument();

        // No constructor needed.
        private ColorArgument() {}

        /**
         * Get the possible colors.
         */
        @Override public List<String> getPossibleValues(@Nullable FPlayer fPlayer) {
            return Lists.newArrayList("F0F0F0", "FCFCFC", "000000", "046DCF");
        }
    }
}