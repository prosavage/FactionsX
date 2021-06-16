package net.prosavage.factionsx.util

import net.prosavage.factionsx.persist.config.ProtectionConfig

class RelationPerms(var info: HashMap<Relation, HashMap<PlayerAction, Boolean>>) {
    fun setPermForRelation(relation: Relation, playerAction: PlayerAction, boolean: Boolean) {
        info[relation]?.set(playerAction, boolean)
    }

    fun getPermForRelation(relation: Relation, playerAction: PlayerAction): Boolean {
        if (ProtectionConfig.overrideActionsForRelation.containsKey(relation) && ProtectionConfig.overrideActionsForRelation[relation]!!.containsKey(playerAction)) {
            return ProtectionConfig.overrideActionsForRelation[relation]!![playerAction]!!
        }

        // We check nullable style if not true to future proof, a simple if (!bool) wont work.
        val perms = info[relation] ?: ProtectionConfig.defaultActionsInOtherFactionsLand.info[relation]
        return perms?.get(playerAction) ?: false
    }
}



