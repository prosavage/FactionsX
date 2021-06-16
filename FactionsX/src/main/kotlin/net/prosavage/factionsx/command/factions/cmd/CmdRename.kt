package net.prosavage.factionsx.command.factions.cmd

import net.prosavage.factionsx.command.engine.CommandInfo
import net.prosavage.factionsx.command.engine.CommandRequirementsBuilder
import net.prosavage.factionsx.command.engine.FCommand
import net.prosavage.factionsx.core.Permission
import net.prosavage.factionsx.event.FactionPreRenameEvent
import net.prosavage.factionsx.event.FactionRenameEvent
import net.prosavage.factionsx.manager.FactionManager
import net.prosavage.factionsx.persist.Message
import net.prosavage.factionsx.persist.config.Config
import net.prosavage.factionsx.util.MemberAction
import org.bukkit.Bukkit

class CmdRename : FCommand() {
    init {
        aliases.add("rename")
        aliases.add("tag")

        requiredArgs.add(Argument("new-tag", 0, StringArgument()))

        commandRequirements = CommandRequirementsBuilder()
                .withPermission(Permission.RENAME)
                .withMemberAction(MemberAction.RENAME)
                .build()
    }

    override fun execute(info: CommandInfo): Boolean {
        val newName = info.args[0]
        val faction = info.faction ?: return false
        val player = info.fPlayer ?: return false

        if (Config.factionTagEnforceLength && (newName.length < Config.factionTagsMinLength || newName.length > Config.factionTagsMaxLength)) {
            info.message(Message.commandCreateLength, Config.factionTagsMinLength.toString(), Config.factionTagsMaxLength.toString())
            return false
        }

        if (Config.factionTagEnforceAlphaNumeric && !newName.chars().allMatch(Character::isLetterOrDigit)) {
            info.message(Message.commandCreateNonAlphaNumeric)
            return false
        }

        if (FactionManager.isTagTaken(newName)) {
            info.message(Message.commandRenameFactionNameIsTaken)
            return false
        }

        val oldName = faction.tag
        val fPlayer = info.fPlayer

        FactionPreRenameEvent(faction, fPlayer, false, oldName, newName).let {
            Bukkit.getPluginManager().callEvent(it)
            if (it.isCancelled) return false
        }

        faction.tag = newName
        info.message(Message.commandRenameSuccess, newName)
        faction.message(Message.commandRenameAnnounce, newName, excludeFPlayers = listOf(player))

        val event = FactionRenameEvent(faction, player, false, oldName, newName)
        Bukkit.getPluginManager().callEvent(event)
        return true
    }

    override fun getHelpInfo(): String {
        return Message.commandRenameHelp
    }
}
