package net.prosavage.factionsx.persist.config.gui

import com.cryptomorin.xseries.XMaterial
import net.prosavage.baseplugin.serializer.Serializer
import net.prosavage.factionsx.FactionsX
import net.prosavage.factionsx.persist.IConfigFile
import net.prosavage.factionsx.upgrade.UpgradeScope
import net.prosavage.factionsx.util.CommandInterfaceItem
import net.prosavage.factionsx.util.Coordinate
import net.prosavage.factionsx.util.InterfaceItem
import net.prosavage.factionsx.util.SerializableItem
import java.io.File

object UpgradesGUIConfig : IConfigFile {


    @Transient
    private val instance = this


    var scopesMenuTitle = "&7Choose a scope."
    var scopesMenuRows = 3
    var scopesMenuBackgroundItem = SerializableItem(
            XMaterial.BLACK_STAINED_GLASS_PANE,
            "&7",
            listOf(),
            1
    )
    var scopesMenuItems = mapOf(
            UpgradeScope.TERRITORY to InterfaceItem(
                    false,
                    Coordinate(1, 3),
                    SerializableItem(
                            XMaterial.GRASS_BLOCK,
                            "&a&lTerritory Upgrades",
                            listOf(
                                    "&7Manage &aTerritory&7 upgrade.",
                                    "&7Only affect &aspecfic&7 chunks."
                            ),
                            1
                    )
            ),
            UpgradeScope.GLOBAL to InterfaceItem(
                    false,
                    Coordinate(1, 5),
                    SerializableItem(
                            XMaterial.BEACON,
                            "&b&lGlobal Upgrades",
                            listOf(
                                    "&7Manage &bGlobal&7 upgrades.",
                                    "&7Affect your faction &beverywhere&7."
                            ),
                            1
                    )
            )
    )


    var territoryChooseMenuTitle = "&aChoose a claim."
    var territoryChooseMenuRows = 6
    var territoryChooseMenuBackgroundItem = SerializableItem(
            XMaterial.BLACK_STAINED_GLASS_PANE,
            "&7",
            listOf(),
            1
    )
    var territoryChooseMenuChunkHereItem = InterfaceItem(
            false,
            Coordinate(1, 4),
            SerializableItem(
                    XMaterial.NETHER_STAR,
                    "&aCurrent Claim",
                    listOf("&7World: &a{world}", "&7X: &a{x}", "&7Z: &a{z}", "&7Claim you are &astanding&7 in.", "&7Click to &a&lMANAGE"),
                    1
            )
    )
    var territoryBrowserStartCoordinate = Coordinate(3, 0)
    var territoryBrowserItemsPerPage = 27
    var territoryItemFormat = SerializableItem(
            XMaterial.GRASS_BLOCK,
            "&aClaim",
            listOf(
                    "&7World: &a{world}", "&7X: &a{x}", "&7Z: &a{z}", "&7Click to &a&lMANAGE"
            ),
            1

    )
    var territoryBrowserPreviousPageItem = InterfaceItem(
            false,
            Coordinate(2, 1),
            SerializableItem(
                    XMaterial.STONE_BUTTON,
                    "&7Previous Page",
                    listOf("&7View claims on the previous page."),
                    1
            )
    )
    var territoryBrowserNextPageItem = InterfaceItem(
            false,
            Coordinate(2, 7),
            SerializableItem(
                    XMaterial.STONE_BUTTON,
                    "&7Next Page",
                    listOf("&7View claims on the next page."),
                    1
            )
    )

    var territoryBrowserSpecialItems = listOf(
            CommandInterfaceItem(
                    listOf("/f upgrade"),
                    InterfaceItem(
                            false,
                            Coordinate(2, 8),
                            SerializableItem(
                                    XMaterial.OAK_DOOR,
                                    "&cBack",
                                    listOf("&7Click to go back to &cmain menu&7."),
                                    1
                            )
                    )
            )
    )


    var territoryManageMenuTitle = "&7Manage Territory"
    var territoryManageMenuRows = 6
    var defaultClaimName = "&aClaim"
    var defaultClaimRenameCancelMessage = "cancel"
    var defaultClaimIcon = XMaterial.GRASS_BLOCK
    var defaultClaimIconAssignCancelMessage = "cancel"
    var territoryManageMenuBackgroundItem = SerializableItem(
            XMaterial.BLACK_STAINED_GLASS_PANE,
            "&7",
            listOf(),
            1
    )

    var territoryManageMenuNicknameItem = InterfaceItem(
            false,
            Coordinate(1, 3),
            SerializableItem(
                    XMaterial.NAME_TAG,
                    "&aRename Claim",
                    listOf("&7Give this claim a &anickname&7.", "&7Nicknames make it easier to find again", "&7in &aterritory search menu."),
                    1
            )
    )


    var territoryManageMenuIconItem = InterfaceItem(
            false,
            Coordinate(1, 5),
            SerializableItem(
                    XMaterial.DIAMOND,
                    "&bIcon",
                    listOf("&7Give this claim a &bicon&7.", "&bIcons make it easier to find again", "&7in &aterritory search menu."),
                    1
            )
    )

    var territoryManageMenuCurrentItem = InterfaceItem(
            false,
            Coordinate(2, 4),
            SerializableItem(
                    XMaterial.GRASS_BLOCK,
                    "&bCurrent Claim",
                    listOf("&7World: &a{world}", "&7X: &a{x}", "&7Z: &a{z}", "&7You are currently &a&lMANAGING&7 this chunk."),
                    1
            )
    )


    var territoryManageMenuPreviousPageItem = InterfaceItem(
            false,
            Coordinate(3, 1),
            SerializableItem(
                    XMaterial.STONE_BUTTON,
                    "&7Previous Page",
                    listOf("&7View claims on the previous page."),
                    1
            )
    )
    var territoryManageMenuNextPageItem = InterfaceItem(
            false,
            Coordinate(3, 7),
            SerializableItem(
                    XMaterial.STONE_BUTTON,
                    "&7Next Page",
                    listOf("&7View claims on the next page."),
                    1
            )
    )

    var territoryManageMenuSpecialItems = listOf(
            CommandInterfaceItem(
                    listOf("/f upgrade TERRITORY"),
                    InterfaceItem(
                            false,
                            Coordinate(3, 8),
                            SerializableItem(
                                    XMaterial.OAK_DOOR,
                                    "&cBack",
                                    listOf("&7Click to go back to &cmain menu&7."),
                                    1
                            )
                    )
            )
    )

    var territoryManageMenuItemsPerPage = 18
    var territoryManageMenuPageStartCoordinate = Coordinate(4, 0)


    var globalUpgradesTitle = "&bGlobal Upgrades"
    var globalUpgradesRows = 4
    var globalUpgradesBackgroundItem = SerializableItem(
            XMaterial.BLACK_STAINED_GLASS_PANE,
            "&7",
            listOf(),
            1
    )

    var globalUpgradesStartCoordinate = Coordinate(1, 0)
    var globalUpgradesItemsPerPage = 18

    var globalPageItem = InterfaceItem(
            false,
            Coordinate(3, 1),
            SerializableItem(
                    XMaterial.STONE_BUTTON,
                    "&7Previous Page",
                    listOf("&7View upgrades on the previous page."),
                    1
            )
    )
    var globalNextPageItem = InterfaceItem(
            false,
            Coordinate(3, 7),
            SerializableItem(
                    XMaterial.STONE_BUTTON,
                    "&7Next Page",
                    listOf("&7View upgrades on the next page."),
                    1
            )
    )

    var globalMenuSpecialItems = listOf(
            CommandInterfaceItem(
                    listOf("/f upgrade"),
                    InterfaceItem(
                            false,
                            Coordinate(3, 8),
                            SerializableItem(
                                    XMaterial.OAK_DOOR,
                                    "&cBack",
                                    listOf("&7Click to go back to &cmain menu&7."),
                                    1
                            )
                    )
            )
    )


    override fun save(factionsx: FactionsX) {
        Serializer(false, factionsx.dataFolder, factionsx.logger)
                .save(instance, File("${factionsx.dataFolder}/config/GUI/", "upgrades-gui-config.json"))
    }

    override fun load(factionsx: FactionsX) {
        Serializer(false, factionsx.dataFolder, factionsx.logger)
                .load(instance, UpgradesGUIConfig::class.java, File("${factionsx.dataFolder}/config/GUI/", "upgrades-gui-config.json"))

    }


}