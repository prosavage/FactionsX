package net.prosavage.factionsx.cmd.argument

import com.cryptomorin.xseries.XMaterial
import net.prosavage.factionsx.command.engine.FCommand
import net.prosavage.factionsx.core.FPlayer
import org.bukkit.Material

class TNTInInventoryArgument : FCommand.ArgumentType() {
    override fun getPossibleValues(fPlayer: FPlayer?): List<String> {
        return listOf("1", fPlayer?.getAmountOfMaterialInPlayerInv(XMaterial.TNT).toString())
    }
}