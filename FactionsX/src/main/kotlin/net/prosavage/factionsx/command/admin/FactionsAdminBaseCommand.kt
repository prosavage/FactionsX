package net.prosavage.factionsx.command.admin

import net.prosavage.factionsx.command.admin.cmd.*
import net.prosavage.factionsx.command.admin.cmd.chatspy.CmdAdminChatSpy
import net.prosavage.factionsx.command.admin.cmd.claim.CmdAdminAutoClaim
import net.prosavage.factionsx.command.admin.cmd.claim.CmdAdminClaim
import net.prosavage.factionsx.command.admin.cmd.claim.CmdAdminUnclaim
import net.prosavage.factionsx.command.admin.cmd.claim.CmdAdminUnclaimAll
import net.prosavage.factionsx.command.admin.cmd.credits.CmdAdminCredits
import net.prosavage.factionsx.command.admin.cmd.maxpower.CmdAdminMaxPower
import net.prosavage.factionsx.command.admin.cmd.power.CmdAdminPower
import net.prosavage.factionsx.command.admin.cmd.strikes.CmdAdminStrikes
import net.prosavage.factionsx.command.engine.CommandInfo
import net.prosavage.factionsx.command.engine.CommandRequirementsBuilder
import net.prosavage.factionsx.command.engine.FCommand
import net.prosavage.factionsx.core.Permission
import net.prosavage.factionsx.core.hasPermission
import net.prosavage.factionsx.persist.Message
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player
import java.util.*

class FactionsAdminBaseCommand : FCommand(), CommandExecutor, TabCompleter {
    init {
        this.commandRequirements = CommandRequirementsBuilder()
                .withPermission(Permission.ADMIN)
                .build()

        addSubCommand(CmdFactionsReload())
        addSubCommand(CmdAdminHelp())
        addSubCommand(CmdAdminCredits())
        addSubCommand(CmdFactionsBypass())
        addSubCommand(CmdAdminClaim())
        addSubCommand(CmdAdminUnclaim())
        addSubCommand(CmdAdminUnclaimAll())
        addSubCommand(CmdAdminAutoClaim())
        addSubCommand(CmdAdminJoin())
        addSubCommand(CmdAdminMaxPower())
        addSubCommand(CmdAdminPower())
        addSubCommand(CmdAdminKick())
        addSubCommand(CmdAdminDisband())
        addSubCommand(CmdAdminRename())
        addSubCommand(CmdAdminSetRole())
        addSubCommand(CmdAdminPaypalView())
        addSubCommand(CmdAdminStrikes())
        addSubCommand(CmdAdminRelation())
        addSubCommand(CmdAdminChatSpy())
        addSubCommand(CmdAdminInvite())

        prefix = "fx"
    }

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        run(CommandInfo(sender, ArrayList(args.toList()), ""))
        return true
    }

    override fun getHelpInfo(): String {
        return Message.commandAdminBaseHelp
    }

    override fun execute(info: CommandInfo): Boolean {
        info.message(Message.commandAdminBaseHelpMessage)
        generateHelp(1, info.commandSender, info.args)
        return true
    }

    override fun onTabComplete(
            sender: CommandSender,
            command: Command,
            alias: String,
            args: Array<String>
    ): List<String>? = with(commandRequirements.permission) {
        if (sender is Player && this != null && !hasPermission(sender, this)) return emptyList()
        return handleTabComplete(sender, command, alias, args)
    }
}