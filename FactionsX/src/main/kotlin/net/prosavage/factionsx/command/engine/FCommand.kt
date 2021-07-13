package net.prosavage.factionsx.command.engine


import com.cryptomorin.xseries.XMaterial
import me.rayzr522.jsonmessage.JSONMessage
import net.prosavage.factionsx.FactionsX
import net.prosavage.factionsx.command.admin.FactionsAdminBaseCommand
import net.prosavage.factionsx.command.factions.FactionsBaseCommand
import net.prosavage.factionsx.core.FPlayer
import net.prosavage.factionsx.core.Faction
import net.prosavage.factionsx.hook.EssentialsHook
import net.prosavage.factionsx.hook.vault.VaultHook
import net.prosavage.factionsx.manager.FactionManager
import net.prosavage.factionsx.manager.PlayerManager
import net.prosavage.factionsx.manager.SpecialActionManager
import net.prosavage.factionsx.persist.Message
import net.prosavage.factionsx.persist.color
import net.prosavage.factionsx.persist.config.Config
import net.prosavage.factionsx.persist.config.EconConfig
import net.prosavage.factionsx.upgrade.UpgradeScope
import net.prosavage.factionsx.util.MemberAction
import net.prosavage.factionsx.util.PlayerAction
import net.prosavage.factionsx.util.Relation
import net.prosavage.factionsx.util.logColored
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.util.*
import kotlin.collections.ArrayList
import kotlin.streams.toList

abstract class FCommand {

    val aliases = LinkedList<String>()
    val requiredArgs = LinkedList<Argument>()
    val optionalArgs = LinkedList<Argument>()
    lateinit var commandRequirements: CommandRequirements
    var prefix = ""
    var bypassArgumentCount = false
    var showInHelp = true
    var cost = 0

    val subCommands = LinkedList<FCommand>()

    fun run(info: CommandInfo) {
        var commandRun = info.aliasUsed
        if (info.args.isNotEmpty()) {
            for (command in subCommands) {
                if (command.aliases.contains(info.args.first().toLowerCase())) {
                    // Remove the first arg so when the CommandInfo is passed to subcommand,
                    // first arg is relative.
                    commandRun += " " + info.args[0]
                    info.args.removeAt(0)
                    info.aliasUsed = commandRun
                    command.run(info)
                    return
                }
            }
        }

        if (!checkRequirements(info)) {
            return
        }

        if (this !is FactionsBaseCommand && this !is FactionsAdminBaseCommand) {
            if (!checkInput(info)) {
                return
            }
        }

        val result = execute(info)
        // If neg price or 0, then we can skip this...
        if (result && commandRequirements.price > 0 && EconConfig.economyEnabled) {
            val price = commandRequirements.price
//            info.faction!!.bank.command(info.fPlayer!!, price)

            val response = info.fPlayer!!.takeMoneyFromFactionBank(price, Faction.BankLogType.COMMAND)
            if (!response.transactionSuccess()) return

            // this is just to avoid double messages
            if (price > info.faction?.bank?.amount ?: 0.0) {
                info.message(
                        Message.genericTransactionSuccessTake,
                        VaultHook.format(price),
                        VaultHook.format(
                                VaultHook.getBalance(info.fPlayer ?: return)
                        )
                )
                return
            }

            info.message(Message.commandPaidFor, Config.numberFormat.format(price))
        }
    }

    abstract fun execute(info: CommandInfo): Boolean


    fun initializeSubCommandData() {
        for (subCommand in subCommands) {
            subCommand.prefix = prefix.replace("/", "")
            subCommand.initializeSubCommandData()
        }
    }


    private fun checkRequirements(info: CommandInfo): Boolean {
        return commandRequirements.checkRequirements(info)
    }

    private fun checkInput(info: CommandInfo): Boolean {
        if (info.args.size < requiredArgs.size) {
            info.message(Message.genericCommandsTooFewArgs)
            handleCommandFormat(info)
            return false
        }

        if (!bypassArgumentCount && info.args.size > requiredArgs.size + optionalArgs.size) {
            info.message(Message.genericCommandsTooManyArgs)
            handleCommandFormat(info)
            return false
        }
        return true
    }

    private fun handleCommandFormat(info: CommandInfo) {
        sendCommandFormat(info, info.isPlayer())
    }

    /**
     * Removes based off of first alias.
     */
    fun removeSubCommand(fCommand: FCommand) {
        subCommands.remove(this.subCommands.find { subCommand -> subCommand.aliases.first == fCommand.aliases.first })
    }

    var parentHelpString = ""

    fun addSubCommand(fCommand: FCommand) {
        val alias = fCommand.aliases.first
        if (this !is FactionsAdminBaseCommand && Config.blacklistedCommands.contains(alias)) {
            logColored("Command ${alias} is blacklisted in config, and will not be added.")
            return
        }
        subCommands.add(fCommand)
        fCommand.prefix = this.prefix
        if (this is FactionsAdminBaseCommand || this is FactionsBaseCommand) return
        fCommand.parentHelpString += aliases[0] + " "
    }

    fun generateHelp(page: Int, commandSender: CommandSender, args: ArrayList<String>) {
        val pageStartEntry = Config.helpGeneratorPageEntries * (page - 1)
        if (page <= 0 || pageStartEntry >= subCommands.size) {
            commandSender.sendMessage(
                    color(
                            Message.messagePrefix + String.format(
                                    Message.commandHelpGeneratorPageInvalid,
                                    page
                            )
                    )
            )
            return
        }

        for (i in pageStartEntry until (pageStartEntry + Config.helpGeneratorPageEntries)) {
            if (subCommands.size - 1 < i) {
                continue
            }
            val command = subCommands[i]
            if (!command.showInHelp) continue
            val base = (if (aliases.size > 0) parentHelpString + aliases[0] + " " else "") + command.aliases[0]
            val tooltip = String.format(
                    Message.commandHelpGeneratorFactionRequired,
                    (if (command.commandRequirements.asFactionMember) Message.commandHelpGeneratorRequires else Message.commandHelpGeneratorNotRequired)
            ) + "\n" + Message.commandHelpGeneratorClickMeToPaste
            if (commandSender is Player) {
                JSONMessage.create(
                        color(
                                String.format(
                                        Message.commandHelpGeneratorFormat,
                                        prefix,
                                        base,
                                        command.getHelpInfo()
                                )
                        )
                )
                        .color(Message.commandHelpGeneratorBackgroundColor)
                        .tooltip(color(tooltip)).suggestCommand("/$prefix $base").send(commandSender)
            } else commandSender.sendMessage(
                    color(
                            String.format(
                                    Message.commandHelpGeneratorFormat,
                                    prefix,
                                    base,
                                    command.getHelpInfo()
                            )
                    )
            )


        }
        if (commandSender is Player) {
            val pageNav = JSONMessage.create("       ")
            if (page > 1) pageNav.then(color(Message.commandHelpGeneratorPageNavBack)).tooltip(color(String.format(Message.commandHelpGeneratorGoTopage, page - 1))).runCommand(
                    "/$prefix help ${page - 1}"
            ).then("       ")
            if (page < subCommands.size) pageNav.then(color(Message.commandHelpGeneratorPageNavNext)).tooltip(color(String.format(Message.commandHelpGeneratorGoTopage, page + 1))).runCommand(
                    "/$prefix help ${page + 1}"
            )
            pageNav.send(commandSender)
        }

    }

    fun sendCommandFormat(info: CommandInfo, useJSON: Boolean = true) {
        val list = mutableListOf<Argument>()
        list.addAll(requiredArgs)
        list.addAll(optionalArgs)
        requiredArgs.sortBy { arg -> arg.argumentOrder }

        if (useJSON) {
            var commandFormatJSON =
                    JSONMessage.create(color(Message.commandEngineFormatHoverable)).then(String.format(Message.commandEngineFormatPrefix, prefix, info.aliasUsed.substring(1))).then(" ")

            for (arg in list) {
                commandFormatJSON = if (optionalArgs.contains(arg)) {
                    commandFormatJSON.then(String.format(Message.commandEngineFormatOptionalArg, arg.name)).tooltip(Message.commandEngineFormatOptionalTooltip).then(" ")
                } else {
                    commandFormatJSON.then(String.format(Message.commandEngineFormatRequiredArg, arg.name)).tooltip(Message.commandEngineFormatRequiredTooltip).then(" ")
                }

            }
            commandFormatJSON.send(info.player)
            return
        }

        // This is for the rest usually for console.
        var commandFormat = "/$prefix ${info.aliasUsed.substring(1)} "
        for (arg in list) {
            commandFormat += if (optionalArgs.contains(arg)) {
                "(${arg.name}) "
            } else {
                "<${arg.name}> "

            }
        }

        info.message(commandFormat)


    }

    class Argument(
            val name: String,
            val argumentOrder: Int,
            val argumentType: ArgumentType
    )


    abstract class ArgumentType {
        abstract fun getPossibleValues(fPlayer: FPlayer?): List<String>
    }

    class ShieldTimeArgument : ArgumentType() {
        val times: List<String> = run {
            val times = arrayListOf<String>()
            for (hour in 1..24) {
                for (minute in 1..59) {
                    times.add("${hour}:${minute}")
                }
            }
            times
        }

        override fun getPossibleValues(fPlayer: FPlayer?): List<String> {
            return times
        }
    }

    enum class RolePropertyType(val tag: String) {
        ROLE_TAG("roleTag"),
        CHAT_TAG("chatTag"),
        ICON_MATERIAL("iconMaterial"),
        PERMISSIONS("permissions")
    }

    class XMaterialArgument : ArgumentType() {
        override fun getPossibleValues(fPlayer: FPlayer?): List<String> {
            return XMaterial.values().map { it.name }
        }
    }

    class RolePropertyTypeArgument : ArgumentType() {
        override fun getPossibleValues(fPlayer: FPlayer?): List<String> {
            return RolePropertyType.values().map { it.tag }
        }
    }

    class PlayerArgument : FCommand.ArgumentType() {
        override fun getPossibleValues(fPlayer: FPlayer?): List<String> {
            val valuePlayer = fPlayer?.getPlayer()
            return getSeenOnlinePlayersBy(valuePlayer).map { player -> player.name }
        }
    }

    class FactionMemberArgument : FCommand.ArgumentType() {
        override fun getPossibleValues(fPlayer: FPlayer?): List<String> {
            if (fPlayer!!.getFaction().isWilderness()) emptyList<String>()
            return fPlayer.getFaction().getMembers().map { fplayer -> fplayer.name }
        }
    }

    class AllFactionsArgument : FCommand.ArgumentType() {
        override fun getPossibleValues(fPlayer: FPlayer?): List<String> {
            return FactionManager.getFactions().map { faction ->
                ChatColor.stripColor(color(faction.tag))!!
            }
        }
    }

    class FactionArgument : FCommand.ArgumentType() {
        override fun getPossibleValues(fPlayer: FPlayer?): List<String> {
            return FactionManager.getFactions().filter { faction -> !faction.isSystemFaction() }.map { faction -> ChatColor.stripColor(color(faction.tag))!! }
        }
    }

    class RelationArgument : ArgumentType() {
        override fun getPossibleValues(fPlayer: FPlayer?): List<String> {
            return Relation.values().map(Relation::tagReplacement)
        }
    }

    class FactionPlayerArgument : FCommand.ArgumentType() {
        override fun getPossibleValues(fPlayer: FPlayer?): List<String> {
            val possibleValues = FactionManager.getFactions().filter { faction -> !faction.isSystemFaction() }
                    .map { faction -> ChatColor.stripColor(color(faction.tag))!! }.toMutableList()
            possibleValues.addAll(getSeenOnlinePlayersBy(fPlayer?.getPlayer()).map { player -> player.name })
            return possibleValues
        }
    }

    class FMapToggleArgument : FCommand.ArgumentType() {
        override fun getPossibleValues(fPlayer: FPlayer?): List<String> {
            return listOf("on", "off")
        }
    }

    class ChatChannelArgument : FCommand.ArgumentType() {
        override fun getPossibleValues(fPlayer: FPlayer?): List<String> {
            return net.prosavage.factionsx.core.ChatChannel.values().map { channel -> channel.channelName }
        }
    }

    class UpgradeScopeArgument : FCommand.ArgumentType() {
        override fun getPossibleValues(fPlayer: FPlayer?): List<String> {
            return UpgradeScope.values().map { scope -> scope.name }
        }
    }

    class IgnoreChannelsArgument : FCommand.ArgumentType() {
        override fun getPossibleValues(fPlayer: FPlayer?): List<String> {
            return net.prosavage.factionsx.core.ChatChannel.values().filter { channel -> channel != net.prosavage.factionsx.core.ChatChannel.PUBLIC }.map { channel -> channel.channelName }
        }
    }

    class RolesArgument : FCommand.ArgumentType() {
        override fun getPossibleValues(fPlayer: FPlayer?): List<String> {
            return fPlayer!!.getFaction().factionRoles.getAllRoles().map { customrole -> customrole.roleTag }.toList()
        }
    }

    class PromotableRoleArgument : ArgumentType() {
        override fun getPossibleValues(fPlayer: FPlayer?): List<String> {
            val factionRoles = fPlayer!!.getFaction().factionRoles
            return factionRoles.getAllRoles().filter { role -> factionRoles.isHigherRole(fPlayer.role, role) }.map { role -> role.roleTag }.toList()
        }
    }

    class PermsRelationArgument : FCommand.ArgumentType() {
        override fun getPossibleValues(fPlayer: FPlayer?): List<String> {
            return Relation.values().map { rel -> rel.name }.toList()
        }
    }

    class WarpArgument : FCommand.ArgumentType() {
        override fun getPossibleValues(fPlayer: FPlayer?): List<String> {
            return fPlayer!!.getFaction().getAllWarps().map { warp -> warp.name }
        }
    }

    class RoleActionArgument : FCommand.ArgumentType() {
        override fun getPossibleValues(fPlayer: FPlayer?): List<String> {
            val memberActions = MemberAction.values().map { action -> action.actionName }.toMutableList()
            memberActions.addAll(PlayerAction.values().map { playerAction -> playerAction.actionName }.toList())
            memberActions.addAll(SpecialActionManager.getRegisteredActionNames())
            return memberActions
        }
    }

    class PermsActionArgument : FCommand.ArgumentType() {
        override fun getPossibleValues(fPlayer: FPlayer?): List<String> {
            return PlayerAction.values().map { action -> action.actionName }.toList()
        }
    }

    class StringArgument : FCommand.ArgumentType() {
        override fun getPossibleValues(fPlayer: FPlayer?): List<String> {
            return emptyList()
        }
    }

    class IntArgument : FCommand.ArgumentType() {
        override fun getPossibleValues(fPlayer: FPlayer?): List<String> {
            return listOf(1.toString())
        }
    }

    class ReloadTypeArgument : FCommand.ArgumentType() {
        override fun getPossibleValues(fPlayer: FPlayer?): List<String> {
            return listOf("hot-reload-jars", "reload-configs")
        }

    }

    class BooleanArgument : FCommand.ArgumentType() {
        override fun getPossibleValues(fPlayer: FPlayer?): List<String> {
            return listOf("true", "false")
        }
    }

//    class MemberArgument : ArgumentType() {
//        override fun getPossibleValues(fPlayer: FPlayer?): List<String> {
//            return if (fPlayer != null && fPlayer.hasFaction()) fplayer.getFaction()!!.getIslandMembers().map { member -> member.name } else emptyList()
//        }
//    }

    fun handleTabComplete(
            sender: CommandSender,
            command: Command,
            alias: String,
            args: Array<String>
    ): List<String> {
        // This is for basic subcommand tabbing. /f <subcommand>

        // Spigot has an empty arg instead of making the array so we gotta check if it's empty :).
        if (args.size == 1) {
            val tabComplete = ArrayList<String>()
            if (args[0].isEmpty()) {
                // Just loop all commands and give the alias.
                subCommands.forEach { subCommand -> tabComplete.add(subCommand.aliases[0]) }
            } else {
                for (subCommand in subCommands) {
                    for (subCommandAlias in subCommand.aliases) {
                        if (subCommandAlias.toLowerCase().startsWith(args[0], true)) {
                            tabComplete.add(subCommandAlias)
                        }
                    }
                }
            }
            return tabComplete
        }

        // Now we gotta check if the command has args and show those...
        if (args.size >= 2) {
            // This is needed so we can get the right relative argument for the command when tabbing.
            var relativeArgIndex = 2
            var subCommandIndex = 0


            var commandToTab: FCommand = this

            // Predicate for filtering our command.
            // If command is not found return empty list.
            while (commandToTab.subCommands.isNotEmpty()) {
                var findCommand: FCommand? = null
                findCommand = commandToTab.subCommands.find { subCommand -> subCommand.aliases.contains(args[subCommandIndex].toLowerCase()) }
                subCommandIndex++
                if (findCommand != null) commandToTab = findCommand else break
                relativeArgIndex++
            }


            // This is the actual arg we need to complete.
            val argToComplete = args.size + 1 - relativeArgIndex
            if (commandToTab.requiredArgs.size >= argToComplete) {
                // Quick add all so we can find from all args.
                val list = mutableListOf<Argument>()
                list.addAll(commandToTab.requiredArgs)
                list.addAll(commandToTab.optionalArgs)
                val possibleValues = mutableListOf<String>()
                val arg = list.find { argument -> argument.argumentOrder == argToComplete }
                if (arg != null) {
                    val start = args[args.size - 1]
                    var possibleValuesArg = arg.argumentType.getPossibleValues(if (sender is Player) PlayerManager.getFPlayer(sender) else null)
                    if (!start.isNullOrEmpty()) possibleValuesArg = possibleValuesArg.filter { possibleValue -> possibleValue.startsWith(start, true) }
                    possibleValues.addAll(possibleValuesArg.toList())
                }
                // If we have more subCommands show those
                if (commandToTab.subCommands.isNotEmpty()) {
                    for (subCommand in commandToTab.subCommands) {
                        for (subCommandAlias in subCommand.aliases) {
                            if (subCommandAlias.toLowerCase().startsWith(args[argToComplete + 1], true)) {
                                possibleValues.add(subCommandAlias)
                            }
                        }
                    }
                }
                return possibleValues
            }

        }

        return emptyList()
    }

    /**
     * Build a [CommandRequirements] object.
     *
     * @param handle to be applied to our [CommandRequirementsBuilder].
     * @return [CommandRequirements] corresponding built requirements.
     */
    fun buildRequirements(handle: CommandRequirementsBuilder.() -> Unit): CommandRequirements {
        return CommandRequirementsBuilder().also(handle).build()
    }

    abstract fun getHelpInfo(): String

    companion object {
        private fun getSeenOnlinePlayersBy(player: Player?): Collection<Player> = Bukkit.getOnlinePlayers().filter {
            player?.let { player -> player.canSee(it) && !EssentialsHook.playerIsVanished(player) } ?: false
        }
    }
}
