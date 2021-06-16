package net.prosavage.factionsx.manager

import net.prosavage.factionsx.FactionsX
import net.prosavage.factionsx.core.FPlayer
import net.prosavage.factionsx.core.Faction
import net.prosavage.factionsx.persist.color
import net.prosavage.factionsx.persist.config.Config
import net.prosavage.factionsx.persist.config.Config.defaultRelation
import net.prosavage.factionsx.persist.config.ProtectionConfig
import net.prosavage.factionsx.persist.config.RoleConfig
import net.prosavage.factionsx.persist.data.Factions
import net.prosavage.factionsx.util.Relation
import org.bukkit.ChatColor

object FactionManager {

    const val WILDERNESS_ID = 0L
    const val WARZONE_ID = 1L
    const val SAFEZONE_ID = 2L

    fun getFactions(): Set<Faction> {
        return Factions.factions.values.toSet()
    }

    fun getFaction(id: Long): Faction {
        return Factions.factions[id] ?: getWilderness()
    }

    fun getFaction(name: String?): Faction? {
        if (name == null) return null
        return Factions.factions.values.find { faction -> ChatColor.stripColor(color(faction.tag.toLowerCase())) == ChatColor.stripColor(color(name.toLowerCase())) }
    }

    fun getWilderness(): Faction {
        return Factions.factions[WILDERNESS_ID]!!
    }

    fun getWarzone(): Faction {
        return Factions.factions[WARZONE_ID]!!
    }

    fun getSafezone(): Faction {
        return Factions.factions[SAFEZONE_ID]!!
    }


    fun isSystemFactionId(id: Long): Boolean {
        return id == WARZONE_ID || id == WILDERNESS_ID || id == SAFEZONE_ID
    }

    fun createNewFaction(instance: FactionsX, tag: String, desiredId: Long, owner: FPlayer?, extra: Faction.() -> Unit = {}): Faction {
        val newFaction = Faction(desiredId, tag, RoleConfig.defaultRoles, ProtectionConfig.defaultActionsInOtherFactionsLand, ownerId = owner?.uuid)
        Factions.factions[desiredId] = newFaction
        owner?.assignFaction(newFaction, RoleConfig.defaultRoles.getApexRole())
        val ownerTag = owner?.uuid?.toString() ?: "System"
        instance.logger.info("Created Faction called \"$tag\" owned by $ownerTag")

        if (defaultRelation !== Relation.NEUTRAL && ownerTag != "System") {
            for (faction in getFactions()) {
                if (faction.isSystemFaction() || faction == newFaction) continue
                forceRelation(faction, newFaction, defaultRelation)
            }
        }

        extra(newFaction)
        return newFaction
    }

    fun deleteFaction(faction: Faction) {
        faction.getMembers().forEach { member ->
            member.unassignFaction()
            member.runFlyChecks(true, preCheckNotification = false, ignoreFlyStatus = true)
        }

        // make this nicer, simpler and faster
        val factionId = faction.id
        faction.relations.keys.forEach { id ->
            getFaction(id).relations.remove(factionId)
        }

        GridManager.getAllClaims(faction).forEach { claim -> GridManager.unclaim(faction, claim) }
        Factions.factions.remove(factionId)
    }

    fun forceRelation(first: Faction, second: Faction, relation: Relation) = Faction.RelationEntry(relation, -1).let {
        first.relations[second.id] = it
        second.relations[first.id] = it
    }

    fun forceRelation(first: Faction, second: Faction, entry: Faction.RelationEntry) = entry.let {
        val relation = it.relation

        with (first) {
            val secondId = second.id
            if (relation === Relation.NEUTRAL) relations.remove(secondId) else relations[secondId] = it
            relationRequests.remove(secondId)
        }

        with (second) {
            val firstId = first.id
            if (relation === Relation.NEUTRAL) relations.remove(firstId) else relations[firstId] = it
            relationRequests.remove(firstId)
        }
    }

    fun isTagTaken(tag: String): Boolean {
        return Factions.factions.values.find { faction -> ChatColor.stripColor(color(faction.tag.toLowerCase())) == ChatColor.stripColor(color(tag.toLowerCase())) } != null
    }

    fun initializeFactions(instance: FactionsX) {
        this.preSystemFaction(WILDERNESS_ID, Config.defaultWildernessTag, Config.wildernessDescription, instance)
        this.preSystemFaction(WARZONE_ID, Config.defaultWarzoneTag, Config.warzoneDescription, instance)
        this.preSystemFaction(SAFEZONE_ID, Config.defaultSafezoneTag, Config.safezoneDescription, instance)
    }

    private fun preSystemFaction(id: Long, defaultTag: String, defaultDescription: String, instance: FactionsX) {
        val faction = Factions.factions[id] ?: createNewFaction(instance, defaultTag, id, null) { Factions.nextFactionId++ }
        faction.description = defaultDescription
        faction.tag = defaultTag
    }
}