package net.prosavage.factionsx.cmd.argument

import net.prosavage.factionsx.command.engine.FCommand
import net.prosavage.factionsx.core.FPlayer
import net.prosavage.factionsx.persist.TNTAddonData

class TNTInBankArgument : FCommand.ArgumentType() {
    override fun getPossibleValues(fPlayer: FPlayer?): List<String> {
        val tnt = fPlayer?.getFaction()?.let { TNTAddonData.tntData.getTNTData(it) } ?: return listOf("0")
        return listOf(tnt.tntAmt.toString())
    }
}