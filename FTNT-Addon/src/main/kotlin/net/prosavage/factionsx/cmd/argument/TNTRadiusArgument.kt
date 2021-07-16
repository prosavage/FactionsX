package net.prosavage.factionsx.cmd.argument

import net.prosavage.factionsx.command.engine.FCommand
import net.prosavage.factionsx.core.FPlayer
import net.prosavage.factionsx.persist.TNTConfig

class TNTRadiusArgument : FCommand.ArgumentType() {
    override fun getPossibleValues(fPlayer: FPlayer?): List<String> =
        listOf("1", "3", TNTConfig.tntFillMaxRadius.toString())
}