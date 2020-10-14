package com.massivecraft.factions.struct

enum class Relation {
    OWNERCLAIM,
    MEMBER,
    ALLY,
    TRUCE,
    NEUTRAL,
    ENEMY;

     fun isMember(): Boolean {
        return this == MEMBER
    }

    fun isAlly(): Boolean {
        return this == ALLY
    }

    fun isTruce(): Boolean {
        return this == TRUCE
    }

    fun isNeutral(): Boolean {
        return this == NEUTRAL
    }

    fun isEnemy(): Boolean {
        return this == ENEMY
    }
}