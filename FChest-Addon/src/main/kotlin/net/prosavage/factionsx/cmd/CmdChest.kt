package net.prosavage.factionsx.cmd

import net.prosavage.factionsx.command.engine.CommandInfo
import net.prosavage.factionsx.command.engine.CommandRequirementsBuilder
import net.prosavage.factionsx.command.engine.FCommand
import net.prosavage.factionsx.persist.ChestConfig
import net.prosavage.factionsx.persist.getChest

class CmdChest : FCommand() {
    init {
        aliases.add("chest")

        commandRequirements = CommandRequirementsBuilder()
                .asFactionMember(true)
                .asPlayer(true)
                .withRawPermission(ChestConfig.cmdChestPermission)
                .build()
    }

    override fun execute(info: CommandInfo): Boolean {
        info.message(ChestConfig.cmdChestMessage)
        info.player!!.openInventory(info.faction!!.getChest())
        return true
    }

    override fun getHelpInfo(): String {
        return ChestConfig.cmdChestHelp
    }
}