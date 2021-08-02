package net.prosavage.factionsx.core

import net.prosavage.factionsx.persist.config.Config
import org.bukkit.entity.HumanEntity
import org.bukkit.entity.Player
import org.bukkit.permissions.Permissible
import org.bukkit.permissions.PermissionAttachmentInfo
import org.bukkit.permissions.PermissionDefault
import org.bukkit.plugin.PluginManager
import java.util.concurrent.atomic.AtomicInteger

enum class Permission(val node: String, val description: String, val permissionDefault: PermissionDefault) {
    INTERNAL_CHAT_COLOR("player.chat.color", "ability to send coloured messages if handled internally", PermissionDefault.OP),
    CREATE("player.create", "create a faction.", PermissionDefault.TRUE),
    POWER("player.power", "view your own power.", PermissionDefault.TRUE),
    PROMOTE("player.promote", "promote a faction member", PermissionDefault.TRUE),
    DEMOTE("player.demote", "demote a faction member", PermissionDefault.TRUE),
    LEAVE("player.leave", "leave your faction.", PermissionDefault.TRUE),
    ENEMY("player.enemy", "enemy a faction", PermissionDefault.TRUE),
    ALLY("player.ally", "ally a faction", PermissionDefault.TRUE),
    ADMIN_RELATION("admin.relation", "set the relation between two factions", PermissionDefault.OP),
    CHAT("player.chat", "use chat channels", PermissionDefault.TRUE),
    NEAR("player.near", "view nearby faction members", PermissionDefault.TRUE),
    LIST("player.list", "list factions", PermissionDefault.TRUE),
    WARP("player.warp", "use warp commands", PermissionDefault.TRUE),
    WHO("player.who", "use who command", PermissionDefault.TRUE),
    IGNORE("player.ignore", "ignore specific chat channels", PermissionDefault.TRUE),
    PREFIX("player.prefix", "set the prefix of player in faction", PermissionDefault.TRUE),
    RENAME("player.rename", "rename your faction", PermissionDefault.TRUE),
    INVITES("player.invites", "view faction invites", PermissionDefault.TRUE),
    TRUCE("player.truce", "truce a faction", PermissionDefault.TRUE),
    NEUTRAL("player.neutral", "go back to neutral relations with a faction", PermissionDefault.TRUE),
    HERE("player.here", "view current faction's land", PermissionDefault.TRUE),
    KICK("player.kick", "kick people from your faction", PermissionDefault.TRUE),
    SHOW("player.show", "view your faction's stats.", PermissionDefault.TRUE),
    UPGRADE("player.upgrade", "use the upgrade menu", PermissionDefault.TRUE),
    DISBAND("player.disband", "disband a faction", PermissionDefault.TRUE),
    CREDITS("player.credits", "use credits commands", PermissionDefault.TRUE),
    ADMIN_UNCLAIMALL("admin.unclaimall", "unclaim ALL of a factions land", PermissionDefault.OP),
    UNCLAIMALL("player.unclaimall", "unclaim ALL your faction's land", PermissionDefault.TRUE),
    MAP("player.map", "view the faction map", PermissionDefault.TRUE),
    HOME("player.home", "go to your faction's home", PermissionDefault.TRUE),
    SETHOME("player.sethome", "set your faction's home", PermissionDefault.TRUE),
    ADMIN_CLAIM("admin.claim", "claim land for other factions", PermissionDefault.OP),
    ADMIN_UNCLAIM("admin.unclaim", "unclaim land for other factions", PermissionDefault.OP),
    ADMIN_CREDITS("admin.credits", "use credit management commands", PermissionDefault.OP),
    INVITE("player.invite", "invite a player", PermissionDefault.TRUE),
    DEINVITE("player.deinvite", "deinvite a player", PermissionDefault.TRUE),
    JOIN("player.join", "joins a faction", PermissionDefault.TRUE),
    ADMIN_POWERBOOST("admin.powerboost", "apply a change to a player's power", PermissionDefault.OP),
    ADMIN_POWER("admin.power", "edit the power of a faction", PermissionDefault.OP),
    ADMIN_MAX_POWER("admin.power.max", "edit the max power of players", PermissionDefault.OP),
    ADMIN_MAX_POWER_BOOST("admin.power.max.boost", "boost players' max power", PermissionDefault.OP),
    ADMIN_KICK("admin.kick", "kick a player from their faction", PermissionDefault.OP),
    ADMIN_JOIN("admin.join", "join a specified faction", PermissionDefault.OP),
    ADMIN_INVITE("admin.invite", "invite a player to a specified faction", PermissionDefault.OP),
    UNCLAIM("player.unclaim", "unclaim land for faction", PermissionDefault.TRUE),
    CLAIM("player.claim", "claim land for faction.", PermissionDefault.TRUE),
    CLAIM_AT("player.claimat", "claim land at specific position for faction.", PermissionDefault.TRUE),
    AUTOCLAIM("player.autoclaim", "toggle autoclaim mode", PermissionDefault.TRUE),
    ADMIN_SETROLE("admin.setrole", "set the role of another player", PermissionDefault.OP),
    ADMIN_DISBAND("admin.disband", "disband other player's factions", PermissionDefault.OP),
    ADMIN_RENAME("admin.rename", "rename other player's factions", PermissionDefault.OP),
    ADMIN("admin", "use admin commands", PermissionDefault.OP),
    ADMIN_HELP("admin.help", "view admin commands", PermissionDefault.OP),
    ADMIN_AUTOCLAIM("admin.autoclaim", "toggle autoclaim mode for a specfic faction", PermissionDefault.OP),
    ADMIN_CHATSPY("admin.chatspy", "toggle chatspy mode", PermissionDefault.OP),
    ADMIN_DESCRIPTION("admin.description", "modify a faction's description", PermissionDefault.OP),
    GIVE("player.give", "give tokens to a player", PermissionDefault.OP),
    REMOVE("player.remove", "removes tokens from a player", PermissionDefault.OP),
    PAY("player.pay", "pay tokens to any player", PermissionDefault.TRUE),
    RESET("player.reset", "resets a players tokens", PermissionDefault.OP),
    CHECK("player.check", "check a players tokens", PermissionDefault.TRUE),
    FLY("player.fly", "use the fly command", PermissionDefault.TRUE),
    FLY_OWN("player.fly.own", "fly in your own land", PermissionDefault.TRUE),
    FLY_WILDERNESS("player.fly.wilderness", "fly in wilderness", PermissionDefault.FALSE),
    FLY_WARZONE("player.fly.warzone", "fly in warzone", PermissionDefault.FALSE),
    FLY_SAFEZONE("player.fly.safezone", "fly in safezone", PermissionDefault.FALSE),
    ADMIN_BYPASS("player.bypass", "bypass factions checks", PermissionDefault.OP),
    PERMS("player.perms", "control your faction's permissions", PermissionDefault.TRUE),
    OPEN("player.open", "control if your faction open or not", PermissionDefault.TRUE),
    PAYPAL_SET("player.paypal.set", "set faction paypal", PermissionDefault.TRUE),
    DISCORD_SET("player.discord.set", "set faction discord", PermissionDefault.TRUE),
    PAYPAL_HELP("player.paypal.help", "view paypal help", PermissionDefault.TRUE),
    DISCORD_HELP("player.discord.help", "view discord help", PermissionDefault.TRUE),
    DISCORD_VIEW("player.discord.view", "view a factions discord", PermissionDefault.TRUE),
    PAYPAL_VIEW("player.paypal.view", "view a factions paypal", PermissionDefault.TRUE),
    COORDS("player.coords", "send coords to your faction", PermissionDefault.TRUE),
    PAYPAL_ADMIN("player.paypal.admin", "view a factions paypal as admin", PermissionDefault.OP),
    ADMIN_RELOAD("admin.reload", "reload the plugin", PermissionDefault.OP),
    ADMIN_STRIKES("admin.strikes", "use admin strikes commands", PermissionDefault.OP),
    STRIKES("player.strikes", "check your faction's strikes", PermissionDefault.TRUE),
    STRIKES_GIVE("player.strikes.give", "add a strike to a faction", PermissionDefault.OP),
    STRIKES_REMOVE("player.strikes.remove", "remove a strike by id from a faction", PermissionDefault.OP),
    STRIKES_EDIT("player.strikes.edit", "edit a strike by id for a faction", PermissionDefault.OP),
    STRIKES_CLEAR("player.strikes.clear", "clear the strikes of a faction", PermissionDefault.OP),
    STRIKES_CHECK("player.strikes.check", "check the strikes of a faction", PermissionDefault.OP),
    BANK("player.bank", "manage your faction's bank", PermissionDefault.TRUE),
    BANK_BALANCE("player.bank.balance", "check your bank's balance", PermissionDefault.TRUE),
    BANK_DEPOSIT("player.bank.deposit", "deposit an amount into your bank", PermissionDefault.TRUE),
    BANK_WITHDRAW("player.bank.withdraw", "withdraw an amount from your bank", PermissionDefault.TRUE),
    BANK_PAY("player.bank.pay", "pay an amount to another faction", PermissionDefault.TRUE),
    BANK_LOGS("player.bank.logs", "check the logs of your faction's bank", PermissionDefault.TRUE),
    ALTS("player.alts", "manage alts in your faction", PermissionDefault.TRUE),
    ALTS_INVITE("player.alts.invite", "invite an alt to your faction", PermissionDefault.TRUE),
    ALTS_KICK("player.alts.kick", "kick an alt from your faction", PermissionDefault.TRUE),
    ALTS_JOIN("player.alts.join", "join a faction as an alt", PermissionDefault.TRUE),
    ALTS_REVOKE("player.alts.revoke", "revoke a player's invitation to be an alt", PermissionDefault.TRUE),
    ALTS_OPEN("player.alts.open", "open up the faction for public alts", PermissionDefault.TRUE),
    ALTS_CLOSE("player.alts.close", "close down the faction from public alts", PermissionDefault.TRUE),
    ALTS_INVITES("player.alts.invites", "view a list of the current ALT invited players", PermissionDefault.TRUE),
    ALTS_LIST("player.alts.list", "view a list of current ALTs", PermissionDefault.TRUE),
    ACCESS("player.access", "modify access for factions and players in one's territories", PermissionDefault.TRUE);

    fun getFullPermissionNode(): String {
        return "${Config.factionsPermissionPrefix}.${this.node}"
    }

}

fun registerAllPermissions(pluginManager: PluginManager) {
    Permission.values().forEach { permission ->
        if (pluginManager.getPermission(permission.getFullPermissionNode()) == null)
            pluginManager.addPermission(
                    org.bukkit.permissions.Permission(
                            permission.getFullPermissionNode(),
                            permission.description,
                            permission.permissionDefault
                    )
            )
    }
}

fun hasPermission(humanEntity: HumanEntity, permission: Permission): Boolean {
    return humanEntity.hasPermission(permission.getFullPermissionNode())
}

fun hasPermission(humanEntity: HumanEntity, permission: String): Boolean {
    return humanEntity.hasPermission(permission)
}

fun getMaxPermission(permissable: Permissible, permission: String): Int {
    if (permissable.isOp) {
        return -1
    }

    // Atomic cuz values need to be final to be accessed from lambda.
    val max = AtomicInteger()

    permissable.effectivePermissions.stream()
            .map(PermissionAttachmentInfo::getPermission)
            .map { perm -> perm.toString().toLowerCase() }
            .filter { permString -> permString.startsWith(permission) }
            .map { permString -> permString.replace("$permission.", "") }
            .forEach { value ->
                // If the value is *, then its basically infinity
                if (value.equals("*", true)) {
                    max.set(-1)
                    return@forEach
                }

                // Other foreach set it to -1.
                if (max.get() == -1) {
                    return@forEach
                }

                try {
                    // Get the int from name
                    val amount = Integer.parseInt(value)

                    // Check if our value is bigger than the one we have.
                    if (amount > max.get()) {
                        max.set(amount)
                    }
                } catch (exception: NumberFormatException) {
                    // hehe you got ignored like women ignore me
                }
            }

    return max.get()
}

fun Player.hasPermission(permission: Permission): Boolean {
    return hasPermission(this, permission)
}