package net.prosavage.factionsx.listener

import com.cryptomorin.xseries.XMaterial
import net.prosavage.factionsx.FactionsX
import net.prosavage.factionsx.core.ChatChannel
import net.prosavage.factionsx.core.FPlayer
import net.prosavage.factionsx.core.Faction
import net.prosavage.factionsx.core.Permission
import net.prosavage.factionsx.gui.upgrades.UpgradesTerritoryManageMenu
import net.prosavage.factionsx.manager.PlaceholderManager
import net.prosavage.factionsx.manager.PlayerManager
import net.prosavage.factionsx.persist.Message
import net.prosavage.factionsx.persist.config.Config
import net.prosavage.factionsx.persist.config.gui.UpgradesGUIConfig
import net.prosavage.factionsx.util.Relation
import net.prosavage.factionsx.util.color
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerChatEvent

class ChatListener : Listener {
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    fun onPlayerChatFormat(event: AsyncPlayerChatEvent) {
        val format = event.format
        if (Config.chatHandledByAnotherPlugin || Config.chatFactionPlaceholder !in format) return

        val player = event.player
        val factionPlayer = PlayerManager.getFPlayer(event.player)

        for (recipient in event.recipients) {
            val faction = factionPlayer.getFaction()
            val relationPrefix = PlaceholderManager.getRelationPrefix(faction, PlayerManager.getFPlayer(recipient).getFaction())

            val formatToUse = format.replace(
                    Config.chatFactionPlaceholder,
                    if (faction.isWilderness()) Config.chatNoFactionReplaceString
                    else Config.chatFactionReplaceString
                            .replace("[RELATIONAL_COLOR]", relationPrefix)
                            .replace("[TAG]", factionPlayer.role.chatTag)
                            .replace("[FACTION]", faction.tag)
            )

            recipient.sendMessage(color(String.format(
                    formatToUse,
                    player.displayName,
                    if (player.hasPermission(Permission.INTERNAL_CHAT_COLOR.getFullPermissionNode()))
                        event.message else event.message.replace("(?i)&[0-9A-FK-ORX]".toRegex(), "")
            )))
        }

        event.recipients.clear()
//     event.isCancelled = true
        event.format = format
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    fun onPlayerChat(event: AsyncPlayerChatEvent) {
        val player = event.player
        val factionPlayer = PlayerManager.getFPlayer(event.player)

        if (!factionPlayer.hasFaction()) return
        val faction = factionPlayer.getFaction()

        if (factionPlayer.isAssigningClaimIcon()) {
            event.isCancelled = true
            if (event.message.equals(UpgradesGUIConfig.defaultClaimIconAssignCancelMessage, true)) {
                factionPlayer.assigningClaimIcon = null
                factionPlayer.message(Message.commandUpgradeClaimIconAssignCancelled)
            } else {
                factionPlayer.assigningClaimIcon?.icon = XMaterial.matchXMaterial(event.message).orElseGet { XMaterial.GRASS_BLOCK }
                Bukkit.getScheduler().runTask(FactionsX.instance, Runnable {
                    UpgradesTerritoryManageMenu.getInv(faction, factionPlayer.assigningClaimIcon!!)?.open(player)
                    factionPlayer.assigningClaimIcon = null
                })
            }
        } else if (factionPlayer.isNamingClaim()) {
            event.isCancelled = true
            if (event.message.equals(UpgradesGUIConfig.defaultClaimRenameCancelMessage, true)) {
                factionPlayer.namingClaim = null
                factionPlayer.message(Message.commandUpgradeClaimRenameCancelled)
            } else {
                factionPlayer.namingClaim?.name = event.message
                Bukkit.getScheduler().runTask(FactionsX.instance, Runnable {
                    UpgradesTerritoryManageMenu.getInv(faction, factionPlayer.namingClaim!!)?.open(player)
                    factionPlayer.namingClaim = null
                })
            }
        }

        val chatChannel = factionPlayer.chatChannel
        when (chatChannel) {
            ChatChannel.PUBLIC -> return
            ChatChannel.ALLY -> sendMessageToFactionOfRelation(factionPlayer, faction, Relation.ALLY, ChatChannel.ALLY, event.message)
            ChatChannel.FACTION -> {
                faction.message(
                        PlaceholderManager.processPlaceholders(factionPlayer, faction, Config.chatChannelFactionFormat)
                                .replace("{message}", event.message),
                        faction.getMembers().filter { member -> member.isIgnoringChannel(ChatChannel.FACTION) }
                )
            }
            ChatChannel.TRUCE -> sendMessageToFactionOfRelation(factionPlayer, faction, Relation.TRUCE, ChatChannel.TRUCE, event.message)
        }

        for (spy in PlayerManager.getOnlineFPlayers()) {
            if (!spy.isChatSpy) continue

            val spyFaction = spy.getFaction()
            val spyRelationTo = spyFaction.getRelationTo(faction)

            if (
                    spyFaction == faction
                    || chatChannel == ChatChannel.ALLY && spyRelationTo == Relation.ALLY && !spy.isIgnoringChannel(ChatChannel.ALLY)
                    || chatChannel == ChatChannel.TRUCE && spyRelationTo == Relation.TRUCE && !spy.isIgnoringChannel(ChatChannel.TRUCE)
            ) continue

            spy.message(Message.commandAdminChatSpyFormat, player.name, faction.tag, event.message, chatChannel.channelName)
        }

        // We returned for the NONE channel.
        event.isCancelled = true
    }

    private fun sendMessageToFactionOfRelation(fPlayer: FPlayer, faction: Faction, relation: Relation, context: ChatChannel, message: String) {
        val messageToSend = PlaceholderManager.processPlaceholders(fPlayer, faction, when (relation) {
            Relation.ALLY -> Config.chatChannelAllyFormat
            Relation.TRUCE -> Config.chatChannelTruceFormat
            else -> ""
        }.replace("{message}", message))

        faction.message(messageToSend, faction.getMembers().filter { member -> member.isIgnoringChannel(context) })
        faction.getRelationalFactions(relation).forEach { fac ->
            // We have to check the other side, did not want to loop all of the factions on the server, check one side and then make sure the other side set the relation too.
            if (fac.getRelationTo(faction) != relation) return@forEach

            fac.message(messageToSend, fac.getMembers().filter { member -> member.isIgnoringChannel(context) }.plus(fPlayer))
        }
    }
}