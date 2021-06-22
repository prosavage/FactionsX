package net.prosavage.factionsx.cmd;

import net.prosavage.factionsx.FGraceAddon;
import net.prosavage.factionsx.command.engine.CommandInfo;
import net.prosavage.factionsx.command.engine.CommandRequirementsBuilder;
import net.prosavage.factionsx.command.engine.FCommand;
import net.prosavage.factionsx.manager.TimerManagerKt;
import net.prosavage.factionsx.persist.GraceConfig;

public class CmdGrace extends FCommand {

    public CmdGrace() {
        // Alias for the command, so this would do `/f wild`.
        getAliases().add("grace");

        // The commandRequirements pre-check common things for you, the official way.
        // For example we could add #asFactionMember(true) if we want to make sure they're a faction member.
        // Here we do not want this executed in console, as the console cannot be teleported.
        this.commandRequirements = new CommandRequirementsBuilder()
                .build();
    }


    @Override
    public boolean execute(CommandInfo info) {
        if (GraceConfig.graceEnabled)
            info.message(GraceConfig.commandGraceTimer, TimerManagerKt.formatMillis(FGraceAddon.getGraceTimer().getTimeLeft(), GraceConfig.timeDifferenceFormat));
        else info.message(GraceConfig.commandGraceDisabled);
        return true;
    }

    @Override
    public String getHelpInfo() {
        return GraceConfig.commandGraceHelp;
    }
}
