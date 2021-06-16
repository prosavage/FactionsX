package net.prosavage.factionsx.command.factions.cmd

import net.prosavage.factionsx.command.engine.CommandInfo
import net.prosavage.factionsx.command.engine.CommandRequirementsBuilder
import net.prosavage.factionsx.command.engine.FCommand
import net.prosavage.factionsx.core.Permission
import net.prosavage.factionsx.gui.upgrades.UpgradesGlobalMenu
import net.prosavage.factionsx.gui.upgrades.UpgradesScopeMenu
import net.prosavage.factionsx.gui.upgrades.UpgradesTerritoryChooseMenu
import net.prosavage.factionsx.persist.Message
import net.prosavage.factionsx.upgrade.UpgradeScope
import net.prosavage.factionsx.util.MemberAction
import net.prosavage.factionsx.util.enumValueOrNull

class CmdUpgrade : FCommand() {
    init {
        aliases.add("upgrade")
        aliases.add("perks")

        optionalArgs.add(Argument("scope", 0, UpgradeScopeArgument()))

        commandRequirements = CommandRequirementsBuilder()
                .asFactionMember(true)
                .withMemberAction(MemberAction.UPGRADE)
                .withPermission(Permission.UPGRADE)
                .build()
    }

    override fun execute(info: CommandInfo): Boolean {
        if (info.args.isNotEmpty()) run {
            val scope = enumValueOrNull<UpgradeScope>(info.args[0].toUpperCase()) ?: return@run
            when (scope) {
                UpgradeScope.TERRITORY -> UpgradesTerritoryChooseMenu.getInv(info.faction!!)?.open(info.player)
                UpgradeScope.GLOBAL -> UpgradesGlobalMenu.getInv(info.faction!!).open(info.player)
            }
            return true
        }
        UpgradesScopeMenu.getInv(info.faction!!)?.open(info.player!!)
        return true
    }

    override fun getHelpInfo(): String {
        return Message.commandUpgradeHelp
    }
}