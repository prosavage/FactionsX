package net.prosavage.factionsx.util

import net.prosavage.factionsx.persist.config.Config

enum class Relation(val tagReplacement: String) {
    ALLY(Config.relationAllyReplacement),
    ENEMY(Config.relationEnemyReplacement),
    TRUCE(Config.relationTruceReplacement),
    NEUTRAL(Config.relationNeutralReplacement);
}