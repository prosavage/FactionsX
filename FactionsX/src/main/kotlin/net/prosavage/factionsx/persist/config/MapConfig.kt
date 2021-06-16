package net.prosavage.factionsx.persist.config

import net.prosavage.baseplugin.serializer.Serializer
import net.prosavage.factionsx.FactionsX
import net.prosavage.factionsx.persist.IConfigFile
import java.io.File

object MapConfig : IConfigFile {

    @Transient
    private val instance = this

    var mapBarTitle = "&8&m=======================<&r&6 Map &8&m>&8&m=======================\n&r &6Facing -> &f{DIRECTION}&7"
    var mapRows = 10
    var mapWidth = 50
    var mapWorldBorderProtected = "&0#"
    var mapWorldBorderProtectedTooltip = "&7This claim is outside the worldborder."
    var mapYouAreHere = "&8+"
    var mapWildernessChar = "-"
    var systemFactionMapToolTip = listOf(
            "&6Tag &l&7» &6{tag}",
            "&7",
            "&8&o(( &7&oClick to claim &8&o))"
    )
    var mapTooltip = listOf(
            "&7Tag &l&7» &6{tag}",
            "&7Leader &l&7» &6{leader}",
            "&7Claims &l&7» &6{claims}&7/&6{max-claims}",
            "&7Power &l&7» &6{faction-power}&7/&6{faction-max-power}",
            "&7",
            "&8&o(( &7&oClick to claim &8&o))"
    )

    var ownFactionMapToolTip = listOf(
            "&7Tag &l&7» &6{tag}",
            "&7Leader &l&7» &6{leader}",
            "&7Claims &l&7» &6{claims}&7/&6{max-claims}",
            "&7Power &l&7» &6{faction-power}&7/&6{faction-max-power}",
            "&7",
            "&8&o(( &7&oClick to unclaim &8&o))"
    )
    var mapToolTipError = "N/A"
    var mapLegendDelimiter = "&7, &r"
    var mapLegendNoFactions = "&7No factions present on map."
    var mapChars = "\\/#$%=&^ABCDEFGHJKLMNOPQRSTUVWXYZ1234567890abcdeghjmnopqrsuvwxyz?".toCharArray()


    override fun save(factionsx: FactionsX) {
        Serializer(false, factionsx.dataFolder, factionsx.logger)
                .save(MapConfig.instance, File("${factionsx.dataFolder}/config", "map-config.json"))
    }

    override fun load(factionsx: FactionsX) {
        Serializer(false, factionsx.dataFolder, factionsx.logger)
                .load(MapConfig.instance, MapConfig::class.java, File("${factionsx.dataFolder}/config", "map-config.json"))
    }
}