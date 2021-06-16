package net.prosavage.factionsx.persist.config.gui

import com.cryptomorin.xseries.XMaterial.*
import net.prosavage.baseplugin.serializer.Serializer
import net.prosavage.factionsx.FactionsX
import net.prosavage.factionsx.persist.IConfigFile
import net.prosavage.factionsx.util.Coordinate
import net.prosavage.factionsx.util.InterfaceItem
import net.prosavage.factionsx.util.SerializableItem
import java.io.File

object AccessGUIConfig : IConfigFile {
    @Transient
    private val instance = this

    var allowed = "Yes"
    var disallowed = "No"

    var typeMenuRows = 3
    var typeMenuTitle = "Choose type of access"
    var typeMenuBackgroundFill = SerializableItem(BLACK_STAINED_GLASS_PANE, "&f", listOf(), 1)
    var typeMenuFactionsItem = InterfaceItem(
            false,
            Coordinate(1, 3),
            SerializableItem(DIAMOND_SWORD, "&e&l* &f&lFACTIONS &e&l*", listOf(
                    "&d",
                    "&7Click this to enter a menu where you will be",
                    "&7able to modify the &6&l&nACCESS&7 of specific claims",
                    "&7a &6&l&nFACTION&7 will be allowed to perform numerous of actions in.",
                    "&d",
                    "&7Allowed to modify: &e{ALLOWED_TO_EDIT}"
            ), 1)
    )
    var typeMenuPlayersItem = InterfaceItem(
            false,
            Coordinate(1, 5),
            SerializableItem(PLAYER_HEAD, "&e&l* &f&lPLAYERS &e&l*", listOf(
                    "&d",
                    "&7Click this to enter a menu where you will be",
                    "&7able to modify the &6&l&nACCESS&7 of specific claims",
                    "&7a &6&l&nPLAYER&7 will be allowed to perform numerous of actions in.",
                    "&d",
                    "&7Allowed to modify: &e{ALLOWED_TO_EDIT}"
            ), 1)
    )
    var typeMenuNoPermission = "&7You lack the permission &6%1\$s &7required to access this menu. Use &6/f perms&7 to fix this."

    var chunkMenuRows = 4
    var chunkMenuTitle = "%s - Choose a chunk"
    var chunkMenuTitleFactions = "Factions"
    var chunkMenuTitlePlayers = "Players"
    var chunkMenuBackgroundFill = SerializableItem(BLACK_STAINED_GLASS_PANE, "&f", listOf(), 1)
    var chunkMenuItemsPerPage = 18
    var chunkMenuStartCoordinate = Coordinate(1, 0)
    var chunkMenuName = "&e&l* &f&l{NAME} &e&l*"
    var chunkMenuLore = listOf("&7World: &6{WORLD}", "&7Coordinates: &6{X}&7, &6{Z}")
    var chunkMenuNextButton = InterfaceItem(
            false, Coordinate(3, 5),
            SerializableItem(STONE_BUTTON, "&6&lNEXT &8>>", listOf(
                    "&7Click this to navigate to the",
                    "&7next page with more chunks."
            ), 1)
    )
    var chunkMenuPreviousButton = InterfaceItem(
            false, Coordinate(3, 3),
            SerializableItem(STONE_BUTTON, "&8<< &6&lPREVIOUS", listOf(
                "&7Click this to navigate to the",
                "&7previous page with more chunks."
            ), 1)
    )

    var objectsMenuRows = 4
    var objectsMenuTitleFactions = "Factions - Choose a faction"
    var objectsMenuTitlePlayers = "Players - Choose a player"
    var objectsMenuBackgroundFill = SerializableItem(BLACK_STAINED_GLASS_PANE, "&f", listOf(), 1)
    var objectsMenuItemsPerPage = 18
    var objectsMenuStartCoordinate = Coordinate(1, 0)
    var objectsMenuName = "&e&l* &f&l{NAME} &e&l*"
    var objectsMenuMaterialFactions = GRAY_TERRACOTTA
    var objectsMenuMaterialPlayers = PLAYER_HEAD
    var objectsMenuLoreFactions = listOf(
            "&d",
            "&7Leader: &6{leader}",
            "&7Members: &6{online_members_count}&7/&6{members_count}",
            "&d",
            "&7&oClick this to modify &6&o{tag}&7&o's access."
    )
    var objectsMenuLorePlayers = listOf(
            "&d",
            "&7Faction: &6{faction}",
            "&7Leader: &6{leader}",
            "&d",
            "&7&oClick this to modify &6&o{tag}&7&o's access."
    )
    var objectsMenuPlayersNoFaction = "None"
    var objectsMenuPlayersNoLeader = "None"
    var objectsMenuNextButton = InterfaceItem(
            false, Coordinate(3, 5),
            SerializableItem(STONE_BUTTON, "&6&lNEXT &8>>", listOf(
                    "&7Click this to navigate to the",
                    "&7next page."
            ), 1)
    )
    var objectsMenuPreviousButton = InterfaceItem(
            false, Coordinate(3, 3),
            SerializableItem(STONE_BUTTON, "&8<< &6&lPREVIOUS", listOf(
                    "&7Click this to navigate to the",
                    "&7previous page."
            ), 1)
    )

    var modificationMenuRows = 4
    var modificationMenuTitle = "Modify {NAME}'s access"
    var modificationMenuBackgroundFill = SerializableItem(BLACK_STAINED_GLASS_PANE, "&f", listOf(), 1)
    var modificationMenuActionName = "&e&l* &f&l{NAME} &e&l*"
    var modificationMenuActionLore = listOf(
            "&d",
            "&7Allowed to perform: &6{ALLOWED}",
            "&d",
            "&7&oClick to toggle this action."
    )
    var modificationMenuItemsPerPage = 18
    var modificationMenuStartCoordinate = Coordinate(1, 0)
    var modificationMenuNextButton = InterfaceItem(
            false, Coordinate(3, 5),
            SerializableItem(STONE_BUTTON, "&6&lNEXT &8>>", listOf(
                    "&7Click this to navigate to the",
                    "&7page of next actions."
            ), 1)
    )
    var modificationMenuPreviousButton = InterfaceItem(
            false, Coordinate(3, 3),
            SerializableItem(STONE_BUTTON, "&8<< &6&lPREVIOUS", listOf(
                    "&7Click this to navigate to the",
                    "&7page of previous actions."
            ), 1)
    )

    override fun save(factionsx: FactionsX) {
        Serializer(false, factionsx.dataFolder, factionsx.logger).save(
                instance,
                File("${factionsx.dataFolder}/config/GUI/", "access-gui-config.json")
        )
    }

    override fun load(factionsx: FactionsX) {
        Serializer(false, factionsx.dataFolder, factionsx.logger).load(
                instance,
                AccessGUIConfig::class.java,
                File("${factionsx.dataFolder}/config/GUI/", "access-gui-config.json")
        )
    }
}