package net.prosavage.factionsx.persist.config

import com.cryptomorin.xseries.XMaterial
import net.prosavage.baseplugin.serializer.Serializer
import net.prosavage.factionsx.FactionsX
import net.prosavage.factionsx.persist.IConfigFile
import net.prosavage.factionsx.util.PlayerAction
import net.prosavage.factionsx.util.Relation
import net.prosavage.factionsx.util.RelationPerms
import org.bukkit.Material
import org.bukkit.entity.EntityType
import java.io.File

object ProtectionConfig : IConfigFile {

    @Transient
    private val instance = this

    var blackListedInteractionBlocksInOtherFactionLand = emptyList<Material>()

    var whiteListedBreakableBlocksInOtherFactionLand = listOf(XMaterial.TORCH.parseMaterial(), XMaterial.REDSTONE_TORCH.parseMaterial())

    var whiteListedBreakableBlocksIncludesSystemFactions = true

    data class ActionsWhenOffline internal constructor(val enabled: Boolean, val allowTntExplosions: Boolean, val actions: Map<PlayerAction, Boolean>, val hasBeenOfflineForSeconds: Int)
    var overrideActionsWhenFactionOffline = ActionsWhenOffline(false, true, mapOf(PlayerAction.BREAK_BLOCK to true), 600)

    var allowedInteractableEntitiesInOtherFactionLand = hashMapOf<EntityType, Boolean>()

    var allowedInteractableEntitiesInWarzone = hashMapOf<EntityType, Boolean>()
    var allowGadgetDamageInWarzone = false

    var allowedInteractableEntitiesInSafezone = hashMapOf<EntityType, Boolean>()
    var allowGadgetDamageInSafezone = false

    var allowMaterialInteractionGlobally = mapOf(
            XMaterial.ENDER_PEARL to XMaterial.values().filter { it.name.endsWith("DOOR") }.toSet()
    )

    var playerActionsInWarzone = hashMapOf(PlayerAction.HURT_PLAYER to true, PlayerAction.HURT_MOB to true)
    var playerActionsInWilderness = hashMapOf<PlayerAction, Boolean>()
    var playerActionsInSafezone = hashMapOf(PlayerAction.ENDER_CHEST to true, PlayerAction.ANVIL to true, PlayerAction.ENCHANTING_TABLE to true)
    var defaultActionsInOtherFactionsLand = RelationPerms(hashMapOf())
    var overrideActionsForRelation = hashMapOf(Relation.ENEMY to hashMapOf(PlayerAction.HURT_PLAYER to true, PlayerAction.HURT_MOB to true, PlayerAction.SPAWN_EGG to true),
            Relation.TRUCE to hashMapOf(PlayerAction.HURT_MOB to true),
            Relation.ALLY to hashMapOf(PlayerAction.HURT_MOB to true),
            Relation.NEUTRAL to hashMapOf(PlayerAction.HURT_MOB to true)
    )

    var disablePvpBetweenNeutralInWilderness = false

    var materialWhitelist = listOf<String>()

    var allowMobsToDamagePlayersInWarzone = true
    var allowMobsToDamagePlayersInSafezone = false
    var allowMobsToDamagePlayersInWilderness = true

    var allowExplosionsInWarZone = false
    var allowExplosionsInSafeZone = false
    var allowExplosionsInWilderness = true

    var disableCreeperExplosionsGlobally = false

    var denyPvPBetweenAllies = true
    var denyPvPBetweenTruce = true
    var denyPvPBetweenNeutral = false


    var denyBreakingBlocksEnemyNearByRadius = 50.0
    var denyBreakingBlocksWhenEnemyNear = listOf(
            XMaterial.SPAWNER
    )


    var shiftRightClickableWhiteListInOtherFactionsLand = hashMapOf(
            XMaterial.CREEPER_SPAWN_EGG.parseMaterial() to listOf(
                    XMaterial.CHEST.parseMaterial(),
                    XMaterial.TRAPPED_CHEST.parseMaterial(),
                    XMaterial.ENDER_CHEST.parseMaterial()
            )
    )

    var denyUnConnectedClaim = false
    var maxUnConnectedClaimsAllowed = 1
    var minDistanceFromOtherFactions = 0
    var denyConnectedOverClaim = false
    var denyAdjacentClaimsToOtherFactions = false
    var allowUnconnectedOverClaimOfEnemies = false

    var allowPistonExtensionsInOtherFaction = false
    var allowPistonRetractionsInOtherFaction = false

    // really need to fix this, but just temporary....
    // way to add tiny support for mods
    @Transient
    private val allowedEntities = setOf(
            "DROPPED_ITEM", "EXPERIENCE_ORB", "AREA_EFFECT_CLOUD", "ELDER_GUARDIAN",
            "WITHER_SKELETON", "STRAY", "EGG", "LEASH_HITCH",
            "PAINTING", "ARROW", "SNOWBALL", "FIREBALL",
            "SMALL_FIREBALL", "ENDER_PEARL", "ENDER_SIGNAL", "SPLASH_POTION",
            "THROWN_EXP_BOTTLE", "ITEM_FRAME", "WITHER_SKULL", "PRIMED_TNT",
            "FALLING_BLOCK", "FIREWORK", "HUSK", "SPECTRAL_ARROW",
            "SHULKER_BULLET", "DRAGON_FIREBALL", "ZOMBIE_VILLAGER", "SKELETON_HORSE",
            "ZOMBIE_HORSE", "ARMOR_STAND", "DONKEY", "MULE",
            "EVOKER_FANGS", "EVOKER", "VEX", "VINDICATOR",
            "ILLUSIONER", "MINECART_COMMAND", "BOAT", "MINECART",
            "MINECART_CHEST", "MINECART_FURNACE", "MINECART_TNT", "MINECART_HOPPER",
            "MINECART_MOB_SPAWNER", "CREEPER", "SKELETON", "SPIDER",
            "GIANT", "ZOMBIE", "SLIME", "GHAST",
            "ZOMBIFIED_PIGLIN", "ENDERMAN", "CAVE_SPIDER", "SILVERFISH",
            "BLAZE", "MAGMA_CUBE", "ENDER_DRAGON", "WITHER",
            "BAT", "WITCH", "ENDERMITE", "GUARDIAN",
            "SHULKER", "PIG", "SHEEP", "COW", "CHICKEN",
            "SQUID", "WOLF", "MUSHROOM_COW", "SNOWMAN",
            "OCELOT", "IRON_GOLEM", "HORSE", "RABBIT",
            "POLAR_BEAR", "LLAMA", "LLAMA_SPIT", "PARROT",
            "VILLAGER", "ENDER_CRYSTAL", "TURTLE", "PHANTOM",
            "TRIDENT", "COD", "SALMON", "PUFFERFISH",
            "TROPICAL_FISH", "DROWNED", "DOLPHIN", "CAT",
            "PANDA", "PILLAGER", "RAVAGER", "TRADER_LLAMA",
            "WANDERING_TRADER", "FOX", "BEE", "HOGLIN",
            "PIGLIN", "STRIDER", "ZOGLIN", "PIGLIN_BRUTE",
            "FISHING_HOOK","LIGHTNING","PLAYER","UNKNOWN",
            "PIG_ZOMBIE"
    )

    fun populateOptions(instance: FactionsX) {
        PlayerAction.values().forEach { action ->
            playerActionsInWarzone.putIfAbsent(action, false)
            playerActionsInSafezone.putIfAbsent(action, false)
            playerActionsInWarzone.putIfAbsent(action, true)
            playerActionsInWilderness.putIfAbsent(action, true)
            Relation.values().forEach { rel ->
                // GSON makes it null since its empty when read.
                if (defaultActionsInOtherFactionsLand.info == null) defaultActionsInOtherFactionsLand.info = hashMapOf<Relation, HashMap<PlayerAction, Boolean>>()
                defaultActionsInOtherFactionsLand.info.putIfAbsent(rel, HashMap<PlayerAction, Boolean>())
                defaultActionsInOtherFactionsLand.info[rel]?.putIfAbsent(action, false)
            }
        }

        EntityType.values().forEach { entityType ->
            // smol mod support
            if (entityType.name !in allowedEntities) {
                return@forEach
            }

            if (entityType != EntityType.PLAYER) {
                allowedInteractableEntitiesInOtherFactionLand.putIfAbsent(entityType, false)
            }

            allowedInteractableEntitiesInSafezone.putIfAbsent(entityType, false)
            allowedInteractableEntitiesInWarzone.putIfAbsent(entityType, false)
        }

        save(instance)
    }

    override fun save(instance: FactionsX) {
        Serializer(false, instance.dataFolder, instance.logger)
                .save(ProtectionConfig.instance, File("${instance.dataFolder}/config", "protection-config.json"))
    }

    override fun load(instance: FactionsX) {
        Serializer(false, instance.dataFolder, instance.logger)
                .load(ProtectionConfig.instance, ProtectionConfig::class.java, File("${instance.dataFolder}/config", "protection-config.json"))
        populateOptions(instance)
    }
}