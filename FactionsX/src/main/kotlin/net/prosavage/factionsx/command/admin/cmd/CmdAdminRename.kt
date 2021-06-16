package net.prosavage.factionsx.command.admin.cmd

import net.prosavage.factionsx.command.engine.CommandInfo
import net.prosavage.factionsx.command.engine.CommandRequirementsBuilder
import net.prosavage.factionsx.command.engine.FCommand
import net.prosavage.factionsx.core.Permission
import net.prosavage.factionsx.event.FactionPreRenameEvent
import net.prosavage.factionsx.event.FactionRenameEvent
import net.prosavage.factionsx.manager.FactionManager
import net.prosavage.factionsx.persist.Message
import net.prosavage.factionsx.persist.config.Config
import org.bukkit.Bukkit

class CmdAdminRename : FCommand() {
    init {
        aliases.add("rename")

        requiredArgs.add(Argument("target", 0, FactionArgument()))
        requiredArgs.add(Argument("name", 1, StringArgument()))

        commandRequirements = CommandRequirementsBuilder()
                .withPermission(Permission.ADMIN_DISBAND)
                .build()
    }

    override fun execute(info: CommandInfo): Boolean {
        val target = info.getArgAsFaction(0, false) ?: return false
        val newName = info.args[1]

        if (target.isSystemFaction()) {
            info.message(Message.commandAdminRenameSystemFaction)
            return false
        }

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

        val oldName = target.tag
        val fPlayer = info.fPlayer

        FactionPreRenameEvent(target, fPlayer, true, oldName, newName).let {
            Bukkit.getPluginManager().callEvent(it)
            if (it.isCancelled) return false
        }

        target.tag = newName
        target.message(Message.commandAdminRenameNotify)
        info.message(Message.commandAdminRenameSuccess, oldName, newName)

        val event = FactionRenameEvent(target, fPlayer, true, oldName, newName)
        Bukkit.getPluginManager().callEvent(event)
        return true
    }

    override fun getHelpInfo(): String {
        return Message.commandAdminDisbandHelp
    }
}