package com.massivecraft.factions.iface

import com.massivecraft.factions.struct.Relation
import org.bukkit.ChatColor

interface RelationParticipator {
    fun describeTo(var1: RelationParticipator?): String?
    fun describeTo(var1: RelationParticipator?, var2: Boolean): String?
    fun getRelationTo(var1: RelationParticipator?): Relation?
    fun getRelationTo(var1: RelationParticipator?, var2: Boolean): Relation?
    fun getColorTo(var1: RelationParticipator?): ChatColor?
}