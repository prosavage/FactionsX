package net.prosavage.factionsx.util

import com.cryptomorin.xseries.XMaterial
import net.prosavage.factionsx.persist.config.ProtectionConfig.overrideActionsForRelation
import net.prosavage.factionsx.persist.config.gui.PermsGUIConfig

enum class PlayerAction(val actionName: String, val icon: XMaterial) {
    HURT_PLAYER(PermsGUIConfig.hurtPlayerAction, PermsGUIConfig.hurtPlayerActionIcon),
    HURT_MOB(PermsGUIConfig.hurtMobsAction, PermsGUIConfig.hurtMobActionIcon),
    BUTTON(PermsGUIConfig.buttonAction, PermsGUIConfig.buttonActionIcon),
    LEVER(PermsGUIConfig.leverAction, PermsGUIConfig.leverActionIcon),
    PRESSURE_PLATE(PermsGUIConfig.pressurePlateAction, PermsGUIConfig.pressurePlaceActionIcon),
    FENCE_GATE(PermsGUIConfig.fenceGateAction, PermsGUIConfig.fenceGateActionIcon),
    TRAPDOOR(PermsGUIConfig.trapdoorAction, PermsGUIConfig.trapdoorActionIcon),
    HOOK(PermsGUIConfig.hookAction, PermsGUIConfig.hookActionIcon),
    HOPPER(PermsGUIConfig.hopperAction, PermsGUIConfig.hopperActionIcon),
    LECTERN(PermsGUIConfig.lecternAction, PermsGUIConfig.lecternActionIcon),
    COMPARATOR(PermsGUIConfig.comparatorAction, PermsGUIConfig.comparatorActionIcon),
    REPEATER(PermsGUIConfig.repeaterAction, PermsGUIConfig.repeaterActionIcon),
    DISPENSER(PermsGUIConfig.dispenserAction, PermsGUIConfig.dispenserActionIcon),
    DOOR(PermsGUIConfig.doorAction, PermsGUIConfig.doorActionIcon),
    CHEST(PermsGUIConfig.chestAction, PermsGUIConfig.chestActionIcon),
    SHULKER(PermsGUIConfig.shulkerAction, PermsGUIConfig.shulkerActionIcon),
    BARREL(PermsGUIConfig.barrelAction, PermsGUIConfig.barrelActionIcon),
    ENDER_CHEST(PermsGUIConfig.enderChestAction, PermsGUIConfig.enderChestActionIcon),
    ANVIL(PermsGUIConfig.anvilAction, PermsGUIConfig.anvilActionIcon),
    BREWING_STAND(PermsGUIConfig.brewingStandAction, PermsGUIConfig.brewingStandActionIcon),
    ENCHANTING_TABLE(PermsGUIConfig.enchantingTableAction, PermsGUIConfig.enchantingTableActionIcon),
    FURNACE(PermsGUIConfig.furnaceAction, PermsGUIConfig.furnanceActionIcon),
    DROPPER(PermsGUIConfig.dropperAction, PermsGUIConfig.dropperActionIcon),
    CAULDRON(PermsGUIConfig.cauldronAction, PermsGUIConfig.cauldronActionIcon),
    SPAWN_EGG(PermsGUIConfig.spawnEggAction, PermsGUIConfig.spawnEggActionIcon),
    BREAK_BLOCK(PermsGUIConfig.breakBlockAction, PermsGUIConfig.breakBlockActionIcon),
    PLACE_BLOCK(PermsGUIConfig.placeBlockAction, PermsGUIConfig.placeBlockActionIcon),
    EMPTY_BUCKET(PermsGUIConfig.emptyBucketAction, PermsGUIConfig.emptyBucketActionIcon),
    FILL_BUCKET(PermsGUIConfig.fillBucketAction, PermsGUIConfig.fillBucketActionIcon),
    USE_BLACKLISTED_BLOCKS(PermsGUIConfig.blackListedBlockAction, PermsGUIConfig.blackListedBlocksActionIcon),
    USE_ENTITY(PermsGUIConfig.useEntityAction, PermsGUIConfig.useEntityActionIcon),
    USE_GADGET(PermsGUIConfig.useGadgetAction, PermsGUIConfig.useGadgetActionIcon),
    DAMAGE_GADGET(PermsGUIConfig.damageGadgetAction, PermsGUIConfig.damageGadgetActionIcon),
    TRAMPLE_SOIL(PermsGUIConfig.trampleSoilAction, PermsGUIConfig.trampleSoilActionIcon);


    fun isLocked(relation: Relation): Boolean = overrideActionsForRelation[relation]?.containsKey(this) == true

    companion object {
        fun getFromConfigOptionName(name: String): PlayerAction? =
                values().find { action -> action.actionName.equals(name, true) }
    }

}