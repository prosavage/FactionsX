package net.prosavage.factionsx.command.factions.cmd

import net.prosavage.factionsx.FactionsX
import net.prosavage.factionsx.command.engine.CommandInfo
import net.prosavage.factionsx.command.engine.CommandRequirementsBuilder
import net.prosavage.factionsx.command.engine.FCommand
import net.prosavage.factionsx.core.Permission
import net.prosavage.factionsx.event.FactionCreateEvent
import net.prosavage.factionsx.event.FactionPreCreateEvent
import net.prosavage.factionsx.hook.vault.VaultHook
import net.prosavage.factionsx.manager.FactionManager
import net.prosavage.factionsx.persist.Message
import net.prosavage.factionsx.persist.Message.commandCreateAnnouncement
import net.prosavage.factionsx.persist.color
import net.prosavage.factionsx.persist.config.Config
import net.prosavage.factionsx.persist.config.Config.factionCreationCommandsToExecute
import net.prosavage.factionsx.persist.config.EconConfig
import net.prosavage.factionsx.persist.data.Factions
import org.bukkit.Bukkit
import org.bukkit.ChatColor

class CmdCreate : FCommand() {
    init {
        aliases.add("create")

        requiredArgs.add(Argument("tag", 0, StringArgument()))

        commandRequirements = CommandRequirementsBuilder()
                .asPlayer(true)
                .withPermission(Permission.CREATE)
                .build()
    }

    override fun execute(info: CommandInfo): Boolean {
        val fplayer = info.fPlayer!!
        val factionTag = ChatColor.stripColor(info.args[0])!!

        if (fplayer.hasFaction()) {
            info.message(Message.commandCreateAlreadyHaveFaction)
            return false
        }

        if (Config.factionTagEnforceLength && (factionTag.length < Config.factionTagsMinLength || factionTag.length > Config.factionTagsMaxLength)) {
            info.message(Message.commandCreateLength, Config.factionTagsMinLength.toString(), Config.factionTagsMaxLength.toString())
            return false
        }

        if (Config.factionTagEnforceAlphaNumeric && factionTag.chars().anyMatch {
                it > 0x7a || it < 0x30 || (it in 0x5b..0x60) || (it in 0x3a..0x40)
        }) {
            info.message(Message.commandCreateNonAlphaNumeric)
            return false
        }

        if (FactionManager.isTagTaken(factionTag)) {
            info.message(Message.commandCreateFactionAlreadyExists, factionTag)
            return false
        }

        val creationCost = EconConfig.factionCreateCost
        val chargeForFactionCreation = creationCost > 0.0 && EconConfig.economyEnabled
        if (chargeForFactionCreation && !VaultHook.hasEnough(fplayer, creationCost)) {
            info.message(Message.commandCreateFactionNotEnoughMoney, creationCost.toString())
            return false
        }

        FactionPreCreateEvent(factionTag, fplayer).let {
            Bukkit.getPluginManager().callEvent(it)
            if (it.isCancelled) return false
        }

        val faction = FactionManager.createNewFaction(FactionsX.instance, factionTag, Factions.nextFactionId++, fplayer) {
            val console = Bukkit.getConsoleSender()
            factionCreationCommandsToExecute.forEach {
                Bukkit.dispatchCommand(console, it.format(
                    this.tag, fplayer.name
                ))
            }
        }
        Bukkit.getPluginManager().callEvent(FactionCreateEvent(faction, fplayer))

        if (chargeForFactionCreation) {
            VaultHook.takeFrom(fplayer, creationCost)
            fplayer.message(Message.genericTransactionSuccessTake, creationCost.toString(), VaultHook.getBalance(fplayer, 0.0).toString())
        }

        info.message(Message.commandCreateSuccess, faction.tag)
        if (commandCreateAnnouncement.isNotEmpty()) {
            Bukkit.broadcastMessage(color(
                    commandCreateAnnouncement.format(
                            fplayer.name, factionTag
                    )
            ))
        }
        return true
    }

    override fun getHelpInfo(): String {
        return Message.commandCreateHelp
    }
}