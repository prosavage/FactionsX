package net.prosavage.factionsx.command.engine


import net.prosavage.factionsx.core.CustomRole
import net.prosavage.factionsx.core.FPlayer
import net.prosavage.factionsx.core.Faction
import net.prosavage.factionsx.manager.FactionManager
import net.prosavage.factionsx.manager.PlayerManager
import net.prosavage.factionsx.persist.Message
import net.prosavage.factionsx.persist.color
import net.prosavage.factionsx.util.Relation
import net.prosavage.factionsx.util.enumValueOrNull
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.util.*

class CommandInfo(val commandSender: CommandSender, val args: ArrayList<String>, var aliasUsed: String) {


    var player: Player? = if (commandSender is Player) commandSender else null
    var fPlayer: FPlayer? = if (commandSender is Player) PlayerManager.getFPlayer(commandSender) else null
    var faction: Faction? = if (commandSender is Player && fPlayer != null) fPlayer!!.getFaction() else null

    init {
        if (faction != null && faction!!.isWilderness()) faction = null
    }


    fun isBypassing(): Boolean {
        return fPlayer!!.inBypass
    }

    fun getArgAsPlayer(index: Int, informIfNot: Boolean = true): Player? {
        val player = Bukkit.getPlayer(args[index])
        if (player == null) {
            if (informIfNot) {
                message(Message.commandParsingPlayerDoesNotExist)
            }
            return null
        }
        return player
    }

    fun getArgAsRole(index: Int, informIfNot: Boolean = true): CustomRole? {
        val target = args[index].toUpperCase()

        return faction!!.factionRoles.getAllRoles()
                .find { role -> role.roleTag.toUpperCase().replace("_", " ") == target }
                ?: run {
                    message(Message.commandParsingArgIsNotRole)
                    null
                }
    }


    fun getArgAsFPlayer(index: Int, cannotReferenceYourSelf: Boolean = true, offline: Boolean = false, informIfNot: Boolean = true): FPlayer? {
        val nameRaw = args.getOrNull(index) ?: run {
            if (informIfNot) {
                message(Message.commandParsingPlayerDoesNotExist)
            }
            return null
        }
        val player = Bukkit.getPlayer(nameRaw)
        if (offline) {
            val fplayer = PlayerManager.getFPlayer(nameRaw)
            if (cannotReferenceYourSelf && fplayer?.uuid == this.player!!.uniqueId) {
                if (informIfNot) {
                    message(Message.commandParsingPlayerIsYou)
                }
                return null
            }
            if (fplayer != null) return fplayer
        }
        if (player == null) {
            if (informIfNot) {
                message(Message.commandParsingPlayerDoesNotExist)
            }
            return null
        }
        if (cannotReferenceYourSelf && player == this.player) {
            if (informIfNot) {
                message(Message.commandParsingPlayerIsYou)
            }
            return null
        }
        return PlayerManager.getFPlayer(player)
    }

    fun getArgAsFaction(index: Int, cannotReferenceYourSelf: Boolean = true, informIfNot: Boolean = true): Faction? {
        val faction = FactionManager.getFaction(args.getOrNull(index)?.replace("_", " "))
        if (faction == null) {
            if (informIfNot) {
                message(Message.commandParsingFactionDoesNotExist)
            }
            return null
        }
        if (cannotReferenceYourSelf && faction == this.fPlayer?.getFaction()) {
            if (informIfNot) {
                message(Message.commandParsingCannotReferenceYourOwnFaction)
            }
            return null
        }
        return faction
    }

    fun getArgAsRelation(index: Int, informIfNot: Boolean = true): Relation? {
        return enumValueOrNull(args.getOrNull(index)?.toUpperCase() ?: "") ?: run {
            if (informIfNot) message(Message.commandParsingRelationDoesNotExist)
            return null
        }
    }

    fun getArgAsInt(index: Int, informIfNot: Boolean = true): Int? {
        return args.getOrNull(index)?.toIntOrNull() ?: run {
            if (informIfNot) {
                message(Message.commandParsingArgIsNotInt)
            }
            return null
        }
    }

    fun getArgAsLong(index: Int, informIfNot: Boolean = true): Long? {
        return args.getOrNull(index)?.toLongOrNull() ?: run {
            if (informIfNot) {
                message(Message.commandParsingArgIsNotInt)
            }
            return null
        }
    }

    fun getArgAsDouble(index: Int, informIfNot: Boolean = true): Double? {
        try {
            return args.getOrNull(index)?.toDouble() ?: kotlin.run {
                if (informIfNot) {
                    message(Message.commandParsingArgIsNotDouble)
                }
                return null
            }
        } catch (e: Exception) {
            if (informIfNot) {
                message(Message.commandParsingArgIsNotDouble)
            }
            return null
        }

    }

    fun getArgAsBoolean(index: Int, informIfNot: Boolean = true): Boolean? {
        val arg = args.getOrNull(index)?.toLowerCase() ?: run {
            if (informIfNot) message(Message.commandParsingArgIsNotBoolean)
            return null
        }
        if (arg == "true" || arg == "on" || arg == "1") return true
        if (arg == "false" || arg == "off" || arg == "0") return false
        if (informIfNot) message(Message.commandParsingArgIsNotBoolean)
        return null
    }

    fun isPlayer(): Boolean {
        return player != null
    }

    fun message(message: String, prefix: Boolean = true) {
        if (message.isEmpty()) return
        commandSender.sendMessage(color((if (prefix) Message.messagePrefix else "") + message))
    }

    fun message(message: String, vararg args: String) {
        if (message.isEmpty()) return
        message(String.format(message, *args))
    }
}