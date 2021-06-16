package net.prosavage.factionsx.command.admin.cmd.claim

import net.prosavage.factionsx.command.engine.CommandInfo
import net.prosavage.factionsx.command.engine.CommandRequirementsBuilder
import net.prosavage.factionsx.command.engine.FCommand
import net.prosavage.factionsx.core.Permission
import net.prosavage.factionsx.persist.Message

class CmdAdminAutoClaim : FCommand() {
    init {
        aliases.add("autoclaim")

        requiredArgs.add(Argument("faction", 0, AllFactionsArgument()))

        commandRequirements = CommandRequirementsBuilder()
                .asFactionMember(true)
                .withPermission(Permission.ADMIN_AUTOCLAIM)
                .build()
    }


    override fun execute(info: CommandInfo): Boolean {
        val player = info.fPlayer ?: return false
        val faction = info.getArgAsFaction(0, false) ?: return false

        player.isAutoClaiming = !player.isAutoClaiming
        if (player.isAutoClaiming) player.autoClaimTargetFaction = faction else player.autoClaimTargetFaction = null
        info.message(Message.commandAdminAutoClaimToggle, if (player.isAutoClaiming) Message.commandAutoClaimToggleOn else Message.commandAutoClaimToggleOff, faction.tag)
        return true
    }

    override fun getHelpInfo(): String {
        return Message.commandAdminAutoClaimHelp
    }
}