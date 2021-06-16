package net.prosavage.factionsx.persist.config

import net.prosavage.baseplugin.serializer.Serializer
import net.prosavage.factionsx.FactionsX
import net.prosavage.factionsx.persist.IConfigFile
import net.prosavage.factionsx.scoreboard.ScoreboardType
import net.prosavage.factionsx.scoreboard.ScoreboardType.*
import java.io.File

object ScoreboardConfig : IConfigFile {
    @Transient
    private val instance = this

    @Transient
    private val defaultHeader: List<String> = listOf(
            "&f",
            "&e\u2B9A Information",
            "&6 * Username: &f%player_name%",
            "&6 * Balance: &f%vault_eco_balance_formatted%",
            "&f"
    )

    @Transient
    private val relationBody: List<String> = defaultHeader + listOf(
            "&e\u2B9A Faction",
            "&6 * Name: &f%factionx_faction_name_custom%",
            "&6 * Leader: &f%factionx_faction_leader%",
            "&6 * Relation: &f%relation%",
            "&f",
            "&ehttps://www.mywebsite.com/"
    )

    /**
     * [Map] of all scoreboards.
     */
    var scoreboards: Map<ScoreboardType, ScoreboardData> = mapOf(
            makeBoard {
                this.type = FACTIONLESS
                this.title = "&6&lFactionsX"
                this.lines = defaultHeader + relationBody.dropWhile { relationBody.indexOf(it) != 5 }
            },
            makeBoard {
                this.type = DEFAULT
                this.title = "&6&lFactionsX"
                this.lines = relationBody.drop(8)
            },
            makeBoard {
                this.type = SYSTEM
                this.title = "&6&lFactionsX"
                this.lines = defaultHeader + listOf("&ehttps://www.mywebsite.com/")
            },
            makeBoard {
                this.type = OWN
                this.title = "&6&lFactionsX"
                this.lines = relationBody.map { it.replace("%relation%", "Own") }
            },
            makeBoard {
                this.type = ALLY
                this.title = "&6&lFactionsX"
                this.lines = relationBody.map { it.replace("%relation%", "Ally") }
            },
            makeBoard {
                this.type = ENEMY
                this.title = "&6&lFactionsX"
                this.lines = relationBody.map { it.replace("%relation%", "Enemy") }
            },
            makeBoard {
                this.type = TRUCE
                this.title = "&6&lFactionsX"
                this.lines = relationBody.map { it.replace("%relation%", "Truce") }
            },
            makeBoard {
                this.type = NEUTRAL
                this.title = "&6&lFactionsX"
                this.lines = relationBody.map { it.replace("%relation%", "Neutral") }
            }
    )
        private set

    override fun save(factionsx: FactionsX) {
        Serializer(false, factionsx.dataFolder, factionsx.logger)
                .save(instance, File("${factionsx.dataFolder}/config", "scoreboard-config.json"))
    }

    /**
     * Load the Board data.
     */
    override fun load(factionsx: FactionsX) {
        Serializer(false, factionsx.dataFolder, factionsx.logger).load(
                instance,
                ScoreboardConfig::class.java,
                File("${factionsx.dataFolder}/config", "scoreboard-config.json")
        )
    }

    /**
     * Build a board.
     */
    private fun makeBoard(data: ScoreboardData.() -> Unit): Pair<ScoreboardType, ScoreboardData> = ScoreboardData().apply(data).run { type to this }
}

/**
 * Class with Scoreboard data for all implementations.
 */
class ScoreboardData {
    lateinit var type: ScoreboardType
    lateinit var title: String
    lateinit var lines: List<String>
}