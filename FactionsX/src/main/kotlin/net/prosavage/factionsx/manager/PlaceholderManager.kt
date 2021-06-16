package net.prosavage.factionsx.manager

import net.prosavage.factionsx.core.FPlayer
import net.prosavage.factionsx.core.Faction
import net.prosavage.factionsx.manager.GridManager.getAllClaims
import net.prosavage.factionsx.persist.color
import net.prosavage.factionsx.persist.config.Config
import net.prosavage.factionsx.placeholder.DefaultPlaceholder
import net.prosavage.factionsx.placeholder.HolderFunction
import net.prosavage.factionsx.util.Patterns
import net.prosavage.factionsx.util.Relation

object PlaceholderManager {
    /**
     * Whether or not PlaceholderAPI has been hooked.
     */
    var isPlaceholderApi: Boolean = false

    /**
     * [HashMap] containing all available internal placeholders.
     */
    private val holders: HashMap<String, HolderFunction> = DefaultPlaceholder
            .values()
            .associate { it.identifier to it.processor }
            .toMutableMap() as HashMap

    /**
     * Register an internal placeholder by identifier and it's processor.
     *
     * @param identifier [String] the identifier to be used for placeholder access.
     * @param processor [HolderFunction] the holder function to be used for placeholder processing.
     * @return [Boolean]
     */
    fun register(identifier: String, processor: HolderFunction): Boolean {
        if (holders.containsKey(identifier)) return false
        holders[identifier] = processor
        return true
    }

    /**
     * Unregister an internal placeholder by it's identifier.
     *
     * @param identifier [String] the identifier of the placeholder to be erased.
     * @return [Boolean]
     */
    fun unregister(identifier: String): Boolean = holders.remove(identifier) != null

    /**
     * Unregister all internal placeholders.
     */
    internal fun unregisterAll() = holders.clear()

    /**
     * Process all placeholders in a single string.
     *
     * @param contentPlayer [FPlayer] the player involved in this process, null if none.
     * @param context [Faction] the faction involved in this process.
     * @param line [String] specific line to process all placeholders in.
     * @return [String]
     */
    fun processPlaceholders(contentPlayer: FPlayer?, context: Faction, line: String): String {
        var message = line
        var lastStartChar = -1
        var lastEndChar = -1

        for ((index, char) in line.withIndex()) {
            if (char == '{') lastStartChar = index
            if (char == '}') lastEndChar = index
            if (lastStartChar == -1 || lastEndChar == -1) continue

            var placeholder = ""
            for (charIndex in lastStartChar + 1 until lastEndChar) {
                placeholder += line[charIndex]
            }
            val result = processPlaceholder(contentPlayer, context, placeholder)
            if (result != placeholder) message = message.replace("{$placeholder}", result)

            lastStartChar = -1
            lastEndChar = -1
        }
        return message
    }

    /**
     * Get the relation prefix between two factions.
     *
     * @param faction [Faction] the faction whom's relation we want to check.
     * @param relationTo [Faction] the faction to check relation with.
     * @return [String]
     */
    fun getRelationPrefix(faction: Faction, relationTo: Faction): String {
        var relString = ""
        if (faction.id == relationTo.id) relString = Config.relationOwnColor
        if (relationTo.isWilderness()) relString = Config.relationNeutralColor
        if (relationTo.isSafezone()) relString = Config.relationSafezoneColor
        if (relationTo.isWarzone()) relString = Config.relationWarzoneColor
        if (relString.isBlank()) {
            val relation = faction.getRelationTo(relationTo)
            relString = getRelationPrefix(relation)
        }
        return color(relString)
    }

    /**
     * Get the (colour) prefix of a relation.
     *
     * @param relation [Relation] the relation to get prefix of.
     * @return [String]
     */
    fun getRelationPrefix(relation: Relation): String = when (relation) {
        Relation.NEUTRAL -> Config.relationNeutralColor
        Relation.ENEMY -> Config.relationEnemyColor
        Relation.ALLY -> Config.relationAllyColor
        Relation.TRUCE -> Config.relationTruceColor
    }

    /**
     * Process a single placeholder if present, otherwise return back the passed placeholder.
     *
     * @param contextPlayer [FPlayer] the player whom was involved in this process, null if none.
     * @param faction [Faction] the faction involved in this process.
     * @param placeholder [String] the single specific placeholder to process.
     * @return [String]
     */
    private fun processPlaceholder(contextPlayer: FPlayer?, faction: Faction, placeholder: String): String {
        val processor = holders[placeholder]

        if (processor != null) {
            return processor.process(contextPlayer, faction)
        }

        if (placeholder.contains("claims") && placeholder.matches("^claims:(\\w+)$".toRegex())) {
            val match = Patterns.CLAIMS_HOLDER.find(placeholder)
            val world = match?.groupValues?.getOrNull(1)
            return getAllClaims(faction).filter { it.world == world }.size.toString()
        }

        return placeholder
    }

    internal fun Faction.getExactOnlineMembers(from: FPlayer?): Set<FPlayer> {
        return getMembers().filter { member ->
            member.isOnline() && !member.isVanished() && from?.getPlayer()?.canSee(member.getPlayer()!!) ?: true
        }.toSet()
    }

    internal fun Faction.getExactOfflineMembers(from: FPlayer?): Set<FPlayer> {
        return getMembers().filter { member ->
            member.isOffline() || member.isVanished() || !(from?.getPlayer()?.canSee(member.getPlayer()!!) ?: false)
        }.toSet()
    }

    internal fun getFactionsOfRelationFormatted(faction: Faction, relation: Relation): String {
        return faction.getRelationalFactions(relation).joinToString(", ") { fac ->
            if (Config.internalPlaceholderRelationsColor) getRelationPrefix(faction, fac) + fac.tag else fac.tag
        }.ifEmpty { Config.placeholderRelationNonePresent }
    }

    internal fun processMemberFormat(contextPlayer: FPlayer): String {
        return processPlaceholders(contextPlayer, contextPlayer.getFaction(), Config.whoMemberFormat)
    }
}