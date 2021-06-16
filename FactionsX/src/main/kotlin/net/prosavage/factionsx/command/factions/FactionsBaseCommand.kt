package net.prosavage.factionsx.command.factions

import net.prosavage.factionsx.command.engine.CommandInfo
import net.prosavage.factionsx.command.engine.CommandRequirementsBuilder
import net.prosavage.factionsx.command.engine.FCommand
import net.prosavage.factionsx.command.factions.cmd.*
import net.prosavage.factionsx.command.factions.cmd.alts.CmdAlts
import net.prosavage.factionsx.command.factions.cmd.bank.CmdBank
import net.prosavage.factionsx.command.factions.cmd.claim.*
import net.prosavage.factionsx.command.factions.cmd.credits.CmdCredits
import net.prosavage.factionsx.command.factions.cmd.perms.CmdPerms
import net.prosavage.factionsx.command.factions.cmd.relation.CmdAlly
import net.prosavage.factionsx.command.factions.cmd.relation.CmdEnemy
import net.prosavage.factionsx.command.factions.cmd.relation.CmdNeutral
import net.prosavage.factionsx.command.factions.cmd.relation.CmdTruce
import net.prosavage.factionsx.command.factions.cmd.roles.CmdRoles
import net.prosavage.factionsx.command.factions.cmd.social.CmdDiscord
import net.prosavage.factionsx.command.factions.cmd.social.CmdPaypal
import net.prosavage.factionsx.command.factions.cmd.warp.CmdWarp
import net.prosavage.factionsx.persist.Message
import net.prosavage.factionsx.persist.config.Config
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import java.util.*


class FactionsBaseCommand : FCommand(), CommandExecutor, TabCompleter {
    init {
        this.commandRequirements = CommandRequirementsBuilder().build()
        prefix = "f"
        addSubCommand(CmdCreate())
        addSubCommand(CmdDisband())
        addSubCommand(CmdClaim())
        addSubCommand(CmdHere())
        addSubCommand(CmdInvite())
        addSubCommand(CmdJoin())
        addSubCommand(CmdPower())
        addSubCommand(CmdKick())
        addSubCommand(CmdLeave())
        addSubCommand(CmdPromote())
        addSubCommand(CmdDemote())
        addSubCommand(CmdClaimAt())
        addSubCommand(CmdMap())
        addSubCommand(CmdEnemy())
        addSubCommand(CmdNeutral())
        addSubCommand(CmdAlly())
        addSubCommand(CmdTruce())
        addSubCommand(CmdHelp())
        addSubCommand(CmdChat())
        addSubCommand(CmdPerms())
        addSubCommand(CmdWho())
        addSubCommand(CmdInvites())
        addSubCommand(CmdDeinvite())
        addSubCommand(CmdRename())
        addSubCommand(CmdPrefix())
        addSubCommand(CmdIgnore())
        addSubCommand(CmdCredits())
        addSubCommand(CmdWarp())
        addSubCommand(CmdNear())
        addSubCommand(CmdList())
        addSubCommand(CmdUnclaimAll())
        addSubCommand(CmdUnClaim())
        addSubCommand(CmdDesc())
        addSubCommand(CmdAutoClaim())
        addSubCommand(CmdUnClaimAt())
        addSubCommand(CmdHome())
        addSubCommand(CmdSetHome())
        addSubCommand(CmdFly())
        addSubCommand(CmdUpgrade())
        addSubCommand(CmdOpen())
        addSubCommand(CmdDiscord())
        addSubCommand(CmdPaypal())
        if (Config.factionShieldEnabled) addSubCommand(CmdShield())
        addSubCommand(CmdCoords())
        addSubCommand(CmdSeeChunk())
        addSubCommand(CmdStrikes())
        addSubCommand(CmdBank())
        addSubCommand(CmdAlts())
        if (!Config.disableCustomRoleCommands) addSubCommand(CmdRoles())
        addSubCommand(CmdAccess())
        addSubCommand(CmdChunkMessage())
    }

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        run(CommandInfo(sender, ArrayList(args.toList()), ""))
        return true
    }

    override fun getHelpInfo(): String {
        return Message.commandBaseHelp
    }

    override fun execute(info: CommandInfo): Boolean {
        info.message(Message.commandBaseHelpMessage)
        generateHelp(1, info.commandSender, info.args)
        return true
    }

    override fun onTabComplete(
            sender: CommandSender,
            command: Command,
            alias: String,
            args: Array<String>
    ): List<String>? = handleTabComplete(sender, command, alias, args)
}