package net.prosavage.factionsx.cmd.admin;

import net.prosavage.factionsx.FGraceAddon;
import net.prosavage.factionsx.command.engine.CommandInfo;
import net.prosavage.factionsx.command.engine.CommandRequirementsBuilder;
import net.prosavage.factionsx.command.engine.FCommand;
import net.prosavage.factionsx.persist.GraceConfig;

import static net.prosavage.factionsx.persist.GraceConfig.commandAdminGraceDisabled;
import static net.prosavage.factionsx.persist.GraceConfig.commandAdminGraceEnabled;

public class CmdAdminGrace extends FCommand {
    private final FGraceAddon addon;

    public CmdAdminGrace(final FGraceAddon addon) {
        this.addon = addon;
        getAliases().add("grace");

        this.commandRequirements = new CommandRequirementsBuilder()
                .withRawPermission("factionsx.admin.grace")
                .build();
    }

    @Override
    public boolean execute(final CommandInfo info) {
        GraceConfig.graceEnabled = !GraceConfig.graceEnabled;
        info.message(GraceConfig.graceEnabled ? commandAdminGraceEnabled : commandAdminGraceDisabled);

        if (GraceConfig.graceEnabled) {
            addon.enableTask(true);
        }
        return true;
    }

    @Override
    public String getHelpInfo() {
        return GraceConfig.commandAdminGraceHelp;
    }
}