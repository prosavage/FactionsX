package net.prosavage.factionsx.persist.config.gui

import com.cryptomorin.xseries.XMaterial
import net.prosavage.baseplugin.serializer.Serializer
import net.prosavage.factionsx.FactionsX
import net.prosavage.factionsx.persist.IConfigFile
import net.prosavage.factionsx.persist.config.Config
import net.prosavage.factionsx.util.*
import org.apache.commons.lang.WordUtils
import java.io.File

object PermsGUIConfig : IConfigFile {

    @Transient
    private val instance = this

    var mainMenuTitle = "&aPermissions"
    var mainMenuRows = 4
    var mainMenuBackgroundItem = SerializableItem(
            XMaterial.BLACK_STAINED_GLASS_PANE,
            "&c",
            listOf(),
            1
    )
    var mainMenuRelationItems = mutableMapOf(
            Relation.NEUTRAL to InterfaceItem(
                    false,
                    Coordinate(1, 2),
                    SerializableItem(
                            XMaterial.WHITE_TULIP,
                            "&7Edit perms for &fneutral&7 factions.",
                            listOf(
                                    "&7Affects permissions for any &fneutral&7 faction.",
                                    "&7This is the default relation."
                            ),
                            1
                    )
            ),
            Relation.ENEMY to InterfaceItem(
                    false,
                    Coordinate(1, 3),
                    SerializableItem(
                            XMaterial.BLAZE_POWDER,
                            "&7Edit perms for &cenemy&7 factions.",
                            listOf(
                                    "&7Affects permissions for &cenemy&7 factions."
                            ),
                            1
                    )
            ),
            Relation.ALLY to InterfaceItem(
                    false,
                    Coordinate(1, 5),
                    SerializableItem(
                            XMaterial.PINK_TULIP,
                            "&7Edit perms for &dally&7 factions.",
                            listOf(
                                    "&7Affects permissions for &dally&7 factions."
                            ),
                            1
                    )
            ),
            Relation.TRUCE to InterfaceItem(
                    false,
                    Coordinate(1, 6),
                    SerializableItem(
                            XMaterial.ALLIUM,
                            "&7Edit perms for &5truce&7 factions.",
                            listOf(
                                    "&7Affects permissions for &5truce&7 factions."
                            ),
                            1
                    )
            )
    )

    var mainMenuRoleInterfaceItem = RoleInterfaceItem(
            hideItem = false,
            name = "&7{role-tag}",
            lore = listOf(
                    "&7Edit permissions for {role-tag}."
            )
    )

    var mainMenuPreviousPageItem = InterfaceItem(
            false,
            Coordinate(2, 1),
            SerializableItem(
                    XMaterial.STONE_BUTTON,
                    "&7Previous Page",
                    listOf("&7View roles on the previous page."),
                    1
            )
    )
    var mainMenuNextPageItem = InterfaceItem(
            false,
            Coordinate(2, 7),
            SerializableItem(
                    XMaterial.STONE_BUTTON,
                    "&7Next Page",
                    listOf("&7View roles on the next page."),
                    1
            )
    )
    var roleItemsPerPage = 3
    var roleItemsStartCoordinate = Coordinate(2, 3)

    var hurtPlayerAction = "hurt_players"
    var hurtMobsAction = "hurt_mobs"
    var buttonAction = "push_buttons"
    var leverAction = "flip_levers"
    var pressurePlateAction = "stand_on_pressure_plates"
    var fenceGateAction = "use_fence_gates"
    var trapdoorAction = "use_trapdoors"
    var hookAction = "use_hooks"
    var hopperAction = "open_hoppers"
    var lecternAction = "use_lecterns"
    var comparatorAction = "use_comparators"
    var repeaterAction = "use_repeaters"
    var dispenserAction = "use_dispensers"
    var doorAction = "use_doors"
    var chestAction = "open_chests"
    var shulkerAction = "open_shulker_box"
    var barrelAction = "open_barrels"
    var enderChestAction = "open_enderchest"
    var anvilAction = "use_anvil"
    var brewingStandAction = "use_brewing_stand"
    var enchantingTableAction = "use_enchanting_table"
    var dropperAction = "use_dropper"
    var furnaceAction = "use_furnace"
    var cauldronAction = "use_cauldron"
    var spawnEggAction = "use_spawn_eggs"
    var breakBlockAction = "break_blocks"
    var placeBlockAction = "place_blocks"
    var emptyBucketAction = "empty_buckets"
    var fillBucketAction = "fill_buckets"
    var blackListedBlockAction = "use_blacklisted_blocks"
    var useEntityAction = "use_entity"
    var trampleSoilAction = "trample_soil"
    var useGadgetAction = "use_gadget"
    var damageGadgetAction = "damage_gadget"


    var hurtPlayerActionIcon = XMaterial.IRON_SWORD
    var hurtMobActionIcon = XMaterial.BONE
    var buttonActionIcon = XMaterial.STONE_BUTTON
    var leverActionIcon = XMaterial.LEVER
    var pressurePlaceActionIcon = XMaterial.STONE_PRESSURE_PLATE
    var fenceGateActionIcon = XMaterial.OAK_FENCE_GATE
    var trapdoorActionIcon = XMaterial.OAK_TRAPDOOR
    var hookActionIcon = XMaterial.TRIPWIRE_HOOK
    var hopperActionIcon = XMaterial.HOPPER
    var lecternActionIcon = XMaterial.BOOKSHELF
    var comparatorActionIcon = XMaterial.COMPARATOR
    var repeaterActionIcon = XMaterial.REPEATER
    var dispenserActionIcon = XMaterial.DISPENSER
    var doorActionIcon = XMaterial.OAK_DOOR
    var chestActionIcon = XMaterial.CHEST
    var shulkerActionIcon = if (XMaterial.supports(11)) XMaterial.SHULKER_BOX else XMaterial.AIR
    var barrelActionIcon = if (XMaterial.supports(14)) XMaterial.BARREL else XMaterial.AIR
    var enderChestActionIcon = XMaterial.ENDER_CHEST
    var anvilActionIcon = XMaterial.ANVIL
    var brewingStandActionIcon = XMaterial.BREWING_STAND
    var enchantingTableActionIcon = XMaterial.ENCHANTING_TABLE
    var furnanceActionIcon = XMaterial.FURNACE
    var dropperActionIcon = XMaterial.DROPPER
    var cauldronActionIcon = XMaterial.CAULDRON
    var spawnEggActionIcon = XMaterial.EGG
    var breakBlockActionIcon = XMaterial.DIAMOND_PICKAXE
    var placeBlockActionIcon = XMaterial.OAK_PLANKS
    var emptyBucketActionIcon = XMaterial.BUCKET
    var fillBucketActionIcon = XMaterial.WATER_BUCKET
    var blackListedBlocksActionIcon = XMaterial.BEDROCK
    var useEntityActionIcon = XMaterial.CREEPER_SPAWN_EGG
    var trampleSoilActionIcon = XMaterial.LEATHER_BOOTS
    var useGadgetActionIcon = XMaterial.ITEM_FRAME
    var damageGadgetActionIcon = XMaterial.ARMOR_STAND


    var kickActionIcon = XMaterial.LEATHER_BOOTS
    var disbandActionIcon = XMaterial.TNT
    var inviteActionIcon = XMaterial.WRITABLE_BOOK
    var deInviteActionIcon = XMaterial.WRITTEN_BOOK
    var demoteActionIcon = XMaterial.IRON_BARS
    var prefixActionIcon = XMaterial.CYAN_DYE
    var renameActionIcon = XMaterial.NAME_TAG
    var unClaimAllActionIcon = XMaterial.MYCELIUM
    var changeDescriptionActionIcon = XMaterial.GREEN_DYE
    var flyActionIcon = XMaterial.FEATHER
    var homeActionIcon = XMaterial.COMPASS
    var setHomeActionIcon = XMaterial.RED_BED
    var claimActionIcon = XMaterial.GRASS_BLOCK
    var unClaimActionIcon = XMaterial.COARSE_DIRT
    var warpActionIcon = XMaterial.ENDER_PEARL
    var setWarpActionIcon = XMaterial.BEACON
    var delWarpActionIcon = XMaterial.FIRE_CHARGE
    var viewWarpPasswordIcon = XMaterial.ENDER_EYE
    var relationActionIcon = XMaterial.ARROW
    var promoteActionIcon = XMaterial.LADDER
    var openActionIcon = XMaterial.CLOCK
    var paypalActionIcon = XMaterial.GOLD_INGOT
    var discordActionIcon = XMaterial.DIAMOND
    var bankWithdrawIcon = XMaterial.SUNFLOWER
    var bankDepositIcon = XMaterial.FEATHER
    var bankPayIcon = XMaterial.GOLD_NUGGET
    var bankLogsIcon = XMaterial.PAPER
    var altsInviteIcon = XMaterial.PLAYER_HEAD
    var altsKickIcon = XMaterial.WOODEN_SWORD
    var altsRevokeIcon = XMaterial.BARRIER
    var altsOpenIcon = XMaterial.OAK_DOOR
    var altsCloseIcon = XMaterial.IRON_DOOR
    var altsInvitesIcon = XMaterial.MAP
    var altsListIcon = XMaterial.FILLED_MAP
    var accessFactionsIcon = XMaterial.DIAMOND_SWORD
    var accessPlayersIcon = XMaterial.PLAYER_HEAD
    var upgradeIcon = XMaterial.ENCHANTED_BOOK

    var permsMenuStatusPlaceholderLocked = "&4&lLOCKED BY SERVER"
    var permsMenuStatusPlaceholderTrue = "&a&lALLOWED"
    var permsMenuStatusPlaceholderFalse = "&c&lDENIED"


    var relationMenuTitle = "{relation} permissions."
    var relationMenuRows = 5
    var relationMenuBackgroundItem = SerializableItem(
            XMaterial.BLACK_STAINED_GLASS_PANE,
            "&c",
            listOf(),
            1
    )

    var relationMenuSpecialItems = listOf(
            CommandInterfaceItem(
                    listOf("/f perms"),
                    InterfaceItem(
                            false,
                            Coordinate(4, 8),
                            SerializableItem(
                                    XMaterial.OAK_DOOR,
                                    "&cBack",
                                    listOf("&7Click to go back to &cmain menu&7."),
                                    1
                            )
                    )
            )
    )
    var relationMenuItems = hashMapOf<PlayerAction, InterfaceItem>()

    var roleMenuTitle = "{role} permissions."
    var roleMenuRows = 6
    var roleMenuBackgroundItem = SerializableItem(
            XMaterial.BLACK_STAINED_GLASS_PANE,
            "&c",
            listOf(),
            1
    )
    var roleMenuSpecialItems = listOf(
            CommandInterfaceItem(
                    listOf("/f perms"),
                    InterfaceItem(
                            false,
                            Coordinate(5, 8),
                            SerializableItem(
                                    XMaterial.OAK_DOOR,
                                    "&cBack",
                                    listOf("&7Click to go back to &cmain menu&7."),
                                    1
                            )
                    )
            )
    )

    data class LazySerializableItem(val hide: Boolean, val item: SerializableItem)

    var roleMenuPlayerActionItems = hashMapOf<PlayerAction, LazySerializableItem>()
    var roleMenuMemberActionItems = hashMapOf<MemberAction, LazySerializableItem>()

    var roleMenuPreviousPageItem = InterfaceItem(
            false,
            Coordinate(5, 1),
            SerializableItem(
                    XMaterial.STONE_BUTTON,
                    "&7Previous Page",
                    listOf("&7View permissions on the previous page."),
                    1
            )
    )
    var roleMenuNextPageItem = InterfaceItem(
            false,
            Coordinate(5, 7),
            SerializableItem(
                    XMaterial.STONE_BUTTON,
                    "&7Next Page",
                    listOf("&7View permissions on the next page."),
                    1
            )
    )
    var roleMenuItemsPerPage = 36
    var roleMenuStartCoordinate = Coordinate(1, 0)


    override fun save(factionsx: FactionsX) {
        Serializer(false, factionsx.dataFolder, factionsx.logger)
                .save(PermsGUIConfig.instance, File("${factionsx.dataFolder}/config/GUI/", "perms-gui-config.json"))
    }

    override fun load(factionsx: FactionsX) {
        Serializer(false, factionsx.dataFolder, factionsx.logger)
                .load(PermsGUIConfig.instance, PermsGUIConfig::class.java, File("${factionsx.dataFolder}/config/GUI/", "perms-gui-config.json"))
        populateOptions(factionsx)
    }

    private fun populateOptions(instance: FactionsX) {
        if (relationMenuItems == null) relationMenuItems = hashMapOf()
        if (roleMenuPlayerActionItems == null) roleMenuPlayerActionItems = hashMapOf()
        if (roleMenuMemberActionItems == null) roleMenuMemberActionItems = hashMapOf()

        val relationItemCoord = Coordinate(0, 0)


        PlayerAction.values().forEach { playerAction ->
            relationMenuItems[playerAction] ?: run {
                relationMenuItems[playerAction] = InterfaceItem(
                        false,
                        Coordinate(relationItemCoord.row, relationItemCoord.column),
                        SerializableItem(
                                playerAction.icon, "&a${formatName(playerAction.actionName)}", listOf("&7Click to &atoggle&7 status.", "&7Currently: {status}"), 1
                        )
                )
                relationItemCoord.increment()
            }

            roleMenuPlayerActionItems[playerAction] ?: run {
                roleMenuPlayerActionItems[playerAction] = LazySerializableItem(false, SerializableItem(
                        playerAction.icon, "&a${formatName(playerAction.actionName)}", listOf("&7Click to &atoggle&7 status.", "&7Currently: {status}"), 1
                ))

            }
        }
        MemberAction.values().forEach { memberAction ->
            roleMenuMemberActionItems[memberAction] ?: run {
                roleMenuMemberActionItems[memberAction] = LazySerializableItem(false, SerializableItem(
                        memberAction.icon, "&a${formatName(memberAction.actionName)}", listOf("&7Click to &atoggle&7 status.", "&7Currently: {status}"), 1
                ))

            }

        }

        save(instance)
    }

    private fun formatName(name: String): String = WordUtils.capitalizeFully(name.replace("_", " "))
}