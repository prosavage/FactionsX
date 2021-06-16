package net.prosavage.factionsx.placeholder

import net.prosavage.factionsx.manager.GridManager.getAllClaims
import net.prosavage.factionsx.manager.PlaceholderManager.getExactOfflineMembers
import net.prosavage.factionsx.manager.PlaceholderManager.getExactOnlineMembers
import net.prosavage.factionsx.manager.PlaceholderManager.getFactionsOfRelationFormatted
import net.prosavage.factionsx.manager.PlaceholderManager.processMemberFormat
import net.prosavage.factionsx.manager.PlayerManager
import net.prosavage.factionsx.manager.formatMillis
import net.prosavage.factionsx.persist.Message
import net.prosavage.factionsx.persist.config.Config.maxClaimLimit
import net.prosavage.factionsx.persist.config.Config.systemOwnedFactionPlaceholderValue
import net.prosavage.factionsx.persist.config.Config.factionStrikeMax
import net.prosavage.factionsx.persist.config.Config.numberFormat
import net.prosavage.factionsx.persist.config.Config.placeholderCouldntBeParsedValue
import net.prosavage.factionsx.persist.config.Config.shieldActivePlaceholderFalse
import net.prosavage.factionsx.persist.config.Config.shieldActivePlaceholderTrue
import net.prosavage.factionsx.persist.config.Config.shieldTimeLeftInvalid
import net.prosavage.factionsx.persist.config.ProtectionConfig.maxUnConnectedClaimsAllowed
import net.prosavage.factionsx.persist.config.EconConfig
import net.prosavage.factionsx.util.Relation
import kotlin.math.roundToInt

internal enum class DefaultPlaceholder(val identifier: String, val processor: HolderFunction) {
    /**
     * GENERIC VALUES
     */
    MESSAGE_PREFIX("message-prefix", { _, _ ->
        Message.messagePrefix
    }),

    /**
     * FACTION VALUES
     */
    TAG("tag", { _, faction ->
        faction.tag
    }),
    DESCRIPTION("description", { _, faction ->
        faction.description
    }),
    CREATION_DATE("creation_date", { _, faction ->
        faction.getFormattedCreationDate()
    }),
    MEMBERS("members", { _, faction ->
        faction.getMembers().joinToString(transform = ::processMemberFormat)
    }),
    MEMBERS_COUNT("members_count", { _, faction ->
        faction.getMembers().size.toString()
    }),
    ONLINE_MEMBERS("online_members", { fPlayer, faction ->
        faction.getExactOnlineMembers(fPlayer).joinToString(transform = ::processMemberFormat)
    }),
    ONLINE_MEMBERS_COUNT("online_members_count", { fPlayer, faction ->
        faction.getExactOnlineMembers(fPlayer).size.toString()
    }),
    OFFLINE_MEMBERS("offline_members", { fPlayer, faction ->
        faction.getExactOfflineMembers(fPlayer).joinToString(transform = ::processMemberFormat)
    }),
    OFFLINE_MEMBERS_COUNT("offline_members_count", { fPlayer, faction ->
        faction.getExactOfflineMembers(fPlayer).size.toString()
    }),
    LEADER("leader", { _, faction ->
        PlayerManager.getFPlayer(faction.ownerId)?.name ?: systemOwnedFactionPlaceholderValue
    }),
    CLAIMS("claims", { _, faction ->
        getAllClaims(faction).size.toString()
    }),
    MAX_CLAIMS("max-claims", { _, faction ->
        if (faction.getMaxClaims() < 0) kotlin.math.floor(faction.getMaxPower()).toInt().toString()
        else kotlin.math.floor(faction.getMaxPower()).toInt().coerceAtMost(faction.getMaxClaims()).toString()
    }),
    MAX_UNCONNECTED_CLAIMS("max-unconnected-claims", { _, faction ->
        faction.getMaxPower().roundToInt().coerceAtMost(maxUnConnectedClaimsAllowed).toString()
    }),
    MAX_CLAIMS_ABS("max-claims-abs", { _, _ ->
        maxClaimLimit.toString()
    }),
    MAX_UNCONNECTED_CLAIMS_ABS("max-unconnected-claims-abs", { _, _ ->
        maxUnConnectedClaimsAllowed.toString()
    }),
    FACTION_POWER("faction-power", { _, faction ->
        faction.getPower().roundToInt().toString()
    }),
    FACTION_MAX_POWER("faction-max-power", { _, faction ->
        numberFormat.format(faction.getMaxPower())
    }),
    OPEN_STATUS("open-status", { _, faction ->
        faction.openStatus.toString()
    }),
    PAYPAL("paypal", { _, faction ->
        faction.paypal
    }),
    DISCORD("discord", { _, faction ->
        faction.discord
    }),
    SHIELD_ACTIVE("shield-active", { _, faction ->
        if (faction.shielded) shieldActivePlaceholderTrue else shieldActivePlaceholderFalse
    }),
    SHIELD_DURATION("shield-duration", { _, faction ->
        if (faction.shielded) formatMillis(faction.getShieldTime() ?: 1L) else shieldTimeLeftInvalid
    }),
    STRIKES("strikes", { _, faction ->
        faction.strikes.size.toString()
    }),
    STRIKES_MAX("strikes_max", { _, _ ->
        factionStrikeMax.toString()
    }),
    ALLIES("allies", { _, faction ->
        getFactionsOfRelationFormatted(faction, Relation.ALLY)
    }),
    ENEMIES("enemies", { _, faction ->
        getFactionsOfRelationFormatted(faction, Relation.ENEMY)
    }),
    TRUCES("truces", { _, faction ->
        getFactionsOfRelationFormatted(faction, Relation.TRUCE)
    }),
    ALTS("alts", { _, faction ->
        faction.getAlts().joinToString { it.name }
    }),
    FACTION_BANK_BALANCE("faction-bank-balance", { _, faction ->
        if (EconConfig.economyEnabled) faction.bank.amount.toString() else "Econ not enabled"
    }),

    /**
     * PLAYER VALUES
     */
    PREFIX("prefix", { player, _ ->
        player?.prefix ?: ""
    }),
    ROLE("role", { player, _ ->
        player?.role?.roleTag ?: placeholderCouldntBeParsedValue
    }),
    CHAT_TAG("chat-tag", { player, _ ->
        player?.role?.chatTag ?: placeholderCouldntBeParsedValue
    }),
    MEMBER_NAME("member-name", { player, _ ->
        player?.name ?: placeholderCouldntBeParsedValue
    }),
    LOCATION_WORLD("location-world", { player, _ ->
        player?.lastLocation?.world?.name ?: ""
    }),
    LOCATION_X("location-x", { player, _ ->
        player?.lastLocation?.blockX.toString()
    }),
    LOCATION_Y("location-y", { player, _ ->
        player?.lastLocation?.blockY.toString()
    }),
    LOCATION_Z("location-z", { player, _ ->
        player?.lastLocation?.blockZ.toString()
    });
}