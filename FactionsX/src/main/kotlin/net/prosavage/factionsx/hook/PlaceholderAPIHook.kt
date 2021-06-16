package net.prosavage.factionsx.hook

import me.clip.placeholderapi.expansion.PlaceholderExpansion
import me.clip.placeholderapi.expansion.Relational
import net.prosavage.factionsx.FactionsX
import net.prosavage.factionsx.core.FPlayer
import net.prosavage.factionsx.manager.GridManager.getAllClaims
import net.prosavage.factionsx.manager.PlaceholderManager
import net.prosavage.factionsx.manager.PlayerManager
import net.prosavage.factionsx.manager.formatMillis
import net.prosavage.factionsx.persist.config.Config
import net.prosavage.factionsx.persist.config.Config.placeholderNumberInvalidParse
import net.prosavage.factionsx.persist.config.Config.playerRoleTagNoFactionPlaceholder
import net.prosavage.factionsx.persist.config.EconConfig
import net.prosavage.factionsx.persist.config.ProtectionConfig
import net.prosavage.factionsx.util.Relation
import net.prosavage.factionsx.util.color
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import kotlin.math.ceil
import kotlin.math.floor

class PlaceholderAPIIntegration : PlaceholderExpansion(), Relational {
    override fun getIdentifier(): String {
        return "factionsx"
    }

    override fun getAuthor(): String {
        return "ProSavage"
    }

    override fun getVersion(): String {
        return FactionsX.instance.description.version
    }

    override fun persist(): Boolean {
        return true
    }

    override fun canRegister(): Boolean {
        return true
    }

    override fun onPlaceholderRequest(player: Player?, string: String): String? {
        // necessity
        val fPlayer = PlayerManager.getFPlayer(player ?: return null)
        val faction = fPlayer.getFaction()
        val isWilderness = faction.isWilderness()

        // work identifier
        return when (string) {
            // Player
            "player_power" -> format(fPlayer.power())
            "player_power_rounded" -> fPlayer.power().toInt().toString()
            "player_maxpower" -> format(fPlayer.getMaxPower())
            "player_name" -> fPlayer.name
            "player_role" -> if (isWilderness) playerRoleTagNoFactionPlaceholder else fPlayer.role.roleTag
            "player_role_chattag" -> if (isWilderness) playerRoleTagNoFactionPlaceholder else fPlayer.role.chatTag
            "player_isleader" -> fPlayer.isLeader().toString()
            "player_credits" -> fPlayer.credits.toString()
            "player_credits_rounded" -> fPlayer.credits.toInt().toString()
            // Faction
            "faction_at_name" -> if (fPlayer.getFactionAt().isWilderness()) Config.placeholderNameCoundntBeParsed else fPlayer.getFactionAt().tag
            "faction_name" -> if (isWilderness) Config.placeholderNameCoundntBeParsed else faction.tag
            "faction_name_custom" -> if (isWilderness) Config.placeholderFactionNameCustomWilderness else {
                Config.placeholderFactionNameCustom.replace("{name}", faction.tag)
            }
            "faction_name_stripped" -> ChatColor.stripColor(color(faction.tag))
            "faction_name_raw" -> faction.tag
            "faction_creation_date" -> if (isWilderness) Config.placeholderCouldntBeParsedValue else faction.getFormattedCreationDate()
            "faction_power" -> if (isWilderness) placeholderNumberInvalidParse else format(faction.getPower())
            "faction_power_rounded" -> if (isWilderness) placeholderNumberInvalidParse else faction.getPower().toInt().toString()
            "faction_maxpower" -> if (isWilderness) placeholderNumberInvalidParse else format(faction.getMaxPower())
            "faction_maxpower_rounded" -> if (isWilderness) placeholderNumberInvalidParse else faction.getMaxPower().toInt().toString()
            "faction_description" -> faction.description
            "faction_strikes" -> faction.strikes.size.toString()
            "faction_strikes_max" -> Config.factionStrikeMax.toString()
            "faction_claims" -> if (isWilderness) placeholderNumberInvalidParse else getAllClaims(faction).size.toString()
            "faction_maxclaims" -> if (isWilderness) placeholderNumberInvalidParse
                else if (faction.getMaxClaims() < 0) floor(faction.getMaxPower()).toInt().toString()
                else floor(faction.getMaxPower()).toInt().coerceAtMost(faction.getMaxClaims()).toString()
            "faction_maxclaims_abs" -> if (faction.getMaxClaims() < 0) "∞" else faction.getMaxClaims().toString()
            "faction_maxunconnectedclaims" -> if (isWilderness) placeholderNumberInvalidParse else floor(faction.getMaxPower()).toInt()
                .coerceAtMost(ProtectionConfig.maxUnConnectedClaimsAllowed).toString()
            "faction_maxunconnectedclaims_abs" -> ProtectionConfig.maxUnConnectedClaimsAllowed.toString()
            "faction_at_leader" -> fPlayer.getFactionAt().getLeader()?.name ?: Config.systemOwnedFactionPlaceholderValue
            "faction_leader" -> faction.getLeader()?.name ?: Config.systemOwnedFactionPlaceholderValue
            "faction_warps" -> faction.warps.size.toString()
            "faction_maxwarps" -> faction.getMaxWarps().toString()
            "faction_allies" -> if (isWilderness) placeholderNumberInvalidParse else
                faction.relations.filterValues { rel -> rel.relation == Relation.ALLY }.size.toString()
            "faction_maxallies" -> if (isWilderness) placeholderNumberInvalidParse else
                faction.getMaxAllies().toString()
            "faction_enemies" -> if (isWilderness) placeholderNumberInvalidParse else
                faction.relations.filterValues { rel -> rel.relation == Relation.ENEMY }.size.toString()
            "faction_maxenemies" -> if (isWilderness) placeholderNumberInvalidParse else
                faction.getMaxEnemies().toString()
            "faction_truce" -> if (isWilderness) placeholderNumberInvalidParse else
                faction.relations.filterValues { rel -> rel.relation == Relation.TRUCE }.size.toString()
            "faction_maxtruce" -> if (isWilderness) placeholderNumberInvalidParse else
                faction.getMaxTruces().toString()
            "faction_online" -> if (isWilderness) placeholderNumberInvalidParse else {
                faction.getOnlineMembers().filter { member -> !member.isVanished() }.size.toString()
            }
            "faction_offline" -> if (isWilderness) placeholderNumberInvalidParse else {
                (faction.getOfflineMembers().size + faction.getOnlineMembers().filter { member -> member.isVanished() }.size).toString()
            }
            "faction_size" -> faction.getMembers().size.toString()
            "faction_shield_active" -> if (faction.shielded) Config.shieldActivePlaceholderTrue else Config.shieldActivePlaceholderFalse
            "faction_shield_duration" -> if (faction.shielded) formatMillis(faction.getShieldTime() ?: 1L) else Config.shieldTimeLeftInvalid
            "faction_alts" -> faction.getAlts().let {
                if (it.isEmpty()) Config.placeholderFactionAltsEmpty
                else it.joinToString(", ", transform = FPlayer::name)
            }
            "faction_alts_count" -> faction.getAlts().size.toString()
            "faction_bank_balance" -> if (isWilderness) placeholderNumberInvalidParse else {
                if (EconConfig.economyEnabled) Config.numberFormat.format(faction.bank.amount) else "Economy Disabled."
            }
            else -> null
        }
    }

    fun format(num: Double): String {
        return Config.numberFormat.format(num)
    }

    override fun onPlaceholderRequest(p1: Player?, p2: Player?, placeholder: String?): String {
        val fplayer1 = PlayerManager.getFPlayer(p1 ?: return "")
        val fplayer2 = PlayerManager.getFPlayer(p2 ?: return "")
        return when (placeholder) {
            "relation" -> fplayer1.getFaction().getRelationTo(fplayer2.getFaction()).tagReplacement
            "relation_color" -> PlaceholderManager.getRelationPrefix(fplayer1.getFaction(), fplayer2.getFaction())
            else -> ""
        }
    }
}