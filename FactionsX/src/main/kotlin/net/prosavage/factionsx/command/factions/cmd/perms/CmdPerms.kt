package net.prosavage.factionsx.command.factions.cmd.perms

import net.prosavage.factionsx.command.engine.CommandInfo
import net.prosavage.factionsx.command.engine.CommandRequirementsBuilder
import net.prosavage.factionsx.command.engine.FCommand
import net.prosavage.factionsx.core.Permission
import net.prosavage.factionsx.gui.perms.PermsSuperMenu
import net.prosavage.factionsx.persist.Message

class CmdPerms : FCommand() {
    init {
        aliases.add("perms")
        aliases.add("perm")

        commandRequirements = CommandRequirementsBuilder()
                .asFactionMember(true)
                .asLeader(true)
                .withPermission(Permission.PERMS)
                .build()

        addSubCommand(CmdPermsRelation(this))
        addSubCommand(CmdPermsRole(this))
        addSubCommand(CmdPermsRoleMenu())
    }

    override fun execute(info: CommandInfo): Boolean {
        if (info.args.isEmpty()) {
            PermsSuperMenu.getInv(info.faction!!)?.open(info.player!!)
        } else generateHelp(1, info.commandSender, info.args)
        return true
    }

    override fun getHelpInfo(): String {
        return Message.commandPermsInfo
    }
}