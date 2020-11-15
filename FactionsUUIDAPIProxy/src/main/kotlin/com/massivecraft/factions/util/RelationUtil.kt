package com.massivecraft.factions.util

import com.massivecraft.factions.iface.RelationParticipator
import com.massivecraft.factions.struct.Relation

class RelationUtil {
    companion object {
        @JvmStatic
        fun getRelationTo(rp1: RelationParticipator, rp2: RelationParticipator, boolean: Boolean): Relation {
            return Relation.NEUTRAL
        }
    }
}