package net.prosavage.factionsx.cmd.argument

import net.prosavage.factionsx.api.SimpleAPIService
import net.prosavage.factionsx.command.engine.FCommand
import net.prosavage.factionsx.core.FPlayer
import net.prosavage.factionsx.persist.TNTAddonData

class TNTInBankArgument : FCommand.ArgumentType() {
    override fun getPossibleValues(fPlayer: FPlayer?): List<String> =
        listOf(fPlayer?.getFaction()?.let { SimpleAPIService.amountOf(it) }?.toString() ?: "0")
}