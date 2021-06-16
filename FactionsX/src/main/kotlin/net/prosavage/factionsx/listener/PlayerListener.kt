package net.prosavage.factionsx.listener

import com.cryptomorin.xseries.XMaterial
import com.cryptomorin.xseries.XMaterial.matchXMaterial
import me.rayzr522.jsonmessage.JSONMessage
import net.prosavage.factionsx.FactionsX.Companion.scoreboard
import net.prosavage.factionsx.FactionsX.Companion.worldGuard
import net.prosavage.factionsx.core.FPlayer
import net.prosavage.factionsx.core.Faction
import net.prosavage.factionsx.manager.FactionManager
import net.prosavage.factionsx.manager.PlaceholderManager
import net.prosavage.factionsx.manager.PlayerManager
import net.prosavage.factionsx.manager.actionbar
import net.prosavage.factionsx.persist.Message
import net.prosavage.factionsx.persist.config.Config
import net.prosavage.factionsx.persist.config.ProtectionConfig
import net.prosavage.factionsx.persist.config.ProtectionConfig.allowMaterialInteractionGlobally
import net.prosavage.factionsx.persist.config.ProtectionConfig.allowedInteractableEntitiesInOtherFactionLand
import net.prosavage.factionsx.persist.config.ProtectionConfig.blackListedInteractionBlocksInOtherFactionLand
import net.prosavage.factionsx.persist.config.ProtectionConfig.materialWhitelist
import net.prosavage.factionsx.persist.config.ProtectionConfig.overrideActionsForRelation
import net.prosavage.factionsx.persist.config.ProtectionConfig.overrideActionsWhenFactionOffline
import net.prosavage.factionsx.persist.data.getFLocation
import net.prosavage.factionsx.scoreboard.implementations.InternalBoard
import net.prosavage.factionsx.util.*
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.entity.Arrow
import org.bukkit.entity.Entity
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.event.*
import org.bukkit.event.block.Action
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.hanging.HangingBreakByEntityEvent
import org.bukkit.event.hanging.HangingPlaceEvent
import org.bukkit.event.player.*
import org.bukkit.inventory.ItemStack

class PlayerListener : Listener {
    @EventHandler(priority = EventPriority.HIGHEST)
    fun PlayerRespawnEvent.on() {
        val factionPlayer = player.getFPlayer()
        val faction = factionPlayer.getFaction()

        // make sure the option is enabled and it's not a system faction
        if (!Config.teleportToFactionHomeOnRespawn || faction.isSystemFaction()) {
            return
        }

        val home = faction.home ?: return
        this.respawnLocation = home.getLocation().clone().add(0.0, 0.5, 0.0)
    }

    @EventHandler
    fun handleDeath(playerDeathEvent: PlayerDeathEvent) {
        val player = playerDeathEvent.entity

        val worldIsBlackListed = Config.worldsNoPowerLoss.contains(player.world.name)
        if ((Config.turnBlacklistWorldsToWhitelist && !worldIsBlackListed) || (!Config.turnBlacklistWorldsToWhitelist && worldIsBlackListed)) return

        val playerLocation = player.location
        val factionAt = getFLocation(playerLocation).getFaction()

        if (
                factionAt.isWarzone() && Config.warZoneNoPowerLoss
                || factionAt.isSafezone() && Config.safeZoneNoPowerLoss
                || factionAt.isWilderness() && Config.wildernessNoPowerLoss
        ) return

        if (worldGuard.hooked && worldGuard.canPerformCustom(player, playerLocation, "no-power-loss", false)) {
            return
        }

        val factionPlayer = PlayerManager.getFPlayer(player)
        factionPlayer.handleDeath()
        factionPlayer.message(Message.deathPowerUpdate, Config.numberFormat.format(factionPlayer.power()))
    }

    @EventHandler
    fun handleFall(event: EntityDamageEvent) {
        if (checkWorldsNoInteractionHandling(event.entity.location)) return
        if (event.cause != EntityDamageEvent.DamageCause.FALL || event.entity !is Player) return

        val fPlayer = PlayerManager.getFPlayer(event.entity as Player)
        if (!fPlayer.isFalling) return

        fPlayer.isFalling = false
        event.isCancelled = true
    }

    @EventHandler(priority = EventPriority.LOWEST)
    fun onPlayerCommand(event: PlayerCommandPreprocessEvent) {
        val fplayer = event.player.getFPlayer()
        val faction = fplayer.getFaction()
        val facAt = fplayer.getFactionAt()

        if (fplayer.inBypass || facAt.isSystemFaction() || faction == facAt) return

        val relation = faction.getRelationTo(facAt)
        val commands = Config.territoryDeniedCommands[relation]

        if (commands == null || commands.none { event.message.startsWith(it, true) }) return

        event.isCancelled = true
        fplayer.message(Message.territoryCommandDenied)
    }

    @EventHandler
    fun handlePlayerDamage(event: EntityDamageByEntityEvent) {
        val damaged = event.entity
        val factionLocation = getFLocation(damaged.location)

        // Duel worlds and stuff.
        val worldIsBlacklisted = Config.worldsNoPvpHandling.contains(factionLocation.world)
        if ((Config.turnBlacklistWorldsToWhitelist && !worldIsBlacklisted) || (!Config.turnBlacklistWorldsToWhitelist && worldIsBlacklisted)) return

        val factionAt = factionLocation.getFaction()
        val damagerEntity = event.damager

        if (damagerEntity is Player || (damagerEntity is Arrow && damagerEntity.shooter is Player)) {
            // Player, we gotta get the shooter if the damager is an arrow
            val damagerPlayer =
                    if (damagerEntity is Player) damagerEntity else (event.damager as Arrow).shooter as Player

            val damager = PlayerManager.getFPlayer(damagerPlayer)
            if (!this.handleAlt(damager, "HURT", event)) return

            // These checks are for players being hit by either an arrow or another player.
            val damagerFaction = damager.getFaction()
            if (damaged is Player) {
                // Bowboosting
                if (damaged == damagerPlayer) return
                val damagee = PlayerManager.getFPlayer(damaged)

                // Check if the damager and damagee have same faction, if so deny.
                val damageeFaction = damagee.getFaction()
                val relation = damageeFaction.getRelationTo(damagerFaction)

                // used to check if the pvp-ignore-relation flag is presentV
                val worldGuardIgnoreRelationCheck = worldGuard.hooked && worldGuard.canPerformCustom(damagerPlayer, damagerPlayer.location, "pvp-ignore-relation", false)

                if (
                        damagerFaction == damageeFaction && damager.hasFaction()
                        && !damager.role.canDoPlayerAction(PlayerAction.HURT_PLAYER)
                        && !worldGuardIgnoreRelationCheck
                ) {
                    JSONMessage.actionbar(Message.listenerCannotHurtOwnFaction, damagerPlayer)
                    if (Config.triedToHurtYouFactionNotify) JSONMessage.actionbar(String.format(Message.listenerTriedToHurtYou, damager.name), damaged)
                    event.isCancelled = true
                } else if (factionAt.isWilderness() && !ProtectionConfig.playerActionsInWilderness.getOrDefault(
                                PlayerAction.HURT_PLAYER, true
                        )

                        // Check safezone, and check if config allows hurting players in safezone
                        || factionAt.isSafezone() && !ProtectionConfig.playerActionsInSafezone.getOrDefault(
                                PlayerAction.HURT_PLAYER, false
                        )

                        // Check warzone, and check if config allows hurting players in warzone
                        || factionAt.isWarzone() && !ProtectionConfig.playerActionsInWarzone.getOrDefault(
                                PlayerAction.HURT_PLAYER, true
                        )

                        // Check if the damagee is standing in their own land, if so, check their relational permissions.
                        || (damagee.hasFaction() && factionAt == damageeFaction
                                && !damageeFaction.relationPerms.getPermForRelation(relation, PlayerAction.HURT_PLAYER)
                                && !worldGuardIgnoreRelationCheck
                        )
                ) {
                    JSONMessage.actionbar(
                            String.format(
                                    Message.listenerPlayerCannotDoThisHere,
                                    PlayerAction.HURT_PLAYER.actionName,
                                    factionAt.tag
                            ), damagerPlayer
                    )

                    JSONMessage.actionbar(String.format(Message.listenerTriedToHurtYou, damager.name), damaged)
                    event.isCancelled = true
                }
                // Relational PvP checks :), needs to be BELOW factionAt check so faction permissions take precedence in own land.
                else if (
                        (ProtectionConfig.denyPvPBetweenAllies && relation == Relation.ALLY
                        || ProtectionConfig.denyPvPBetweenTruce && relation == Relation.TRUCE
                        || ProtectionConfig.denyPvPBetweenNeutral && relation == Relation.NEUTRAL)
                        && !worldGuardIgnoreRelationCheck
                ) {
                    JSONMessage.actionbar(
                            String.format(
                                    Message.listenerPlayerCannotHurtRelation,
                                    PlaceholderManager.getRelationPrefix(damagerFaction, damageeFaction) + relation.name
                            ), damagerPlayer
                    )

                    JSONMessage.actionbar(String.format(Message.listenerTriedToHurtYou, damager.name), damaged)
                    event.isCancelled = true
                } else {
                    // Check if player is in fly & disable if so, as we have allowed them to be damaged as they passed all the checks above.
                    if (damagee.isFFlying) damagee.setFly(false)
                    if (damager.isFFlying) damager.setFly(false)
                }

                return
            }

            // Check if the player can hit mobs.
            val allowedToHitMob: Boolean = when {
                factionAt.isWarzone() -> ProtectionConfig.playerActionsInWarzone
                        .getOrDefault(PlayerAction.HURT_MOB, true)

                factionAt.isWilderness() -> ProtectionConfig.playerActionsInWilderness[PlayerAction.HURT_MOB] ?: true

                factionAt.isSafezone() -> ProtectionConfig.playerActionsInSafezone
                        .getOrDefault(PlayerAction.HURT_MOB, false)

                damagerFaction != factionAt && damager.hasFaction() -> factionAt.relationPerms
                        .getPermForRelation(damagerFaction.getRelationTo(factionAt), PlayerAction.HURT_MOB)

                else -> damager.role.canDoPlayerAction(PlayerAction.HURT_MOB)
            }

            if (!allowedToHitMob) {
                JSONMessage.actionbar(
                        String.format(
                                Message.listenerPlayerCannotDoThisHere,
                                PlayerAction.HURT_MOB.actionName,
                                factionAt.tag
                        ), damagerPlayer
                )

                event.isCancelled = true
            }
        } else if (event.entity is Player && (factionAt.isWarzone() && !ProtectionConfig.allowMobsToDamagePlayersInWarzone
                        || factionAt.isSafezone() && !ProtectionConfig.allowMobsToDamagePlayersInSafezone
                        || factionAt.isWilderness() && !ProtectionConfig.allowMobsToDamagePlayersInWilderness)
        ) {
            event.isCancelled = true
        }
    }

    enum class BlockChangeAction {
        BREAK, PLACE, EMPTY_BUCKET, FILL_BUCKET;

        fun isBucketAction(): Boolean {
            return this == EMPTY_BUCKET || this == FILL_BUCKET
        }
    }

    private fun checkWorldsNoInteractionHandling(location: Location): Boolean {
        val worldIsBlacklisted = Config.worldsNoInteractionHandling.contains(location.world!!.name)
        return ((Config.turnBlacklistWorldsToWhitelist && !worldIsBlacklisted) || (!Config.turnBlacklistWorldsToWhitelist && worldIsBlacklisted))
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    fun handleInteract(event: PlayerInteractEvent) {
        val block = event.clickedBlock ?: return
        val player = event.player
        val fPlayer = player.getFPlayer()
        val location = block.location
        val factionLocation = getFLocation(location)
        val factionAt = factionLocation.getFaction()

        if (
            checkWorldsNoInteractionHandling(location)
            || !event.performWhitelistedMaterialCheck(event.item?.type?.name, factionAt, fPlayer)
        ) return

        if (event.action == Action.RIGHT_CLICK_BLOCK && !this.handleAlt(player.getFPlayer(), "INTERACT", event)) {
            return
        }

        val type = block.type
        if (!event.performWhitelistedMaterialCheck(type.name, factionAt, fPlayer)) {
            return
        }

        val itemInHand = event.item?.type
        if (itemInHand != null && itemInHand != Material.AIR && allowMaterialInteractionGlobally[matchXMaterial(itemInHand)]?.contains(matchXMaterial(type)) == true) {
            return
        }

        event.isCancelled = !canPerformActionInFactionLocation(
            factionAt, player, event.item,
            event.action, block, true,
            location
        )
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    fun handleInteractAtEntity(event: PlayerInteractEntityEvent) {
        val rightClicked = event.rightClicked
        if (rightClicked.hasMetadata("NPC") || checkWorldsNoInteractionHandling(rightClicked.location)) return

        val factionLocation = getFLocation(rightClicked.location)
        if (Config.worldsNoInteractionHandling.contains(factionLocation.world)) return

        val player = event.player
        if (!this.handleAlt(player.getFPlayer(), "INTERACT_ENTITY", event)) return

        val factionAt = factionLocation.getFaction()
        event.isCancelled = !processInteractAtEntity(factionAt, player, rightClicked, true)
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    fun PlayerInteractAtEntityEvent.onGadgets() {
        val location = rightClicked.location
        if (checkWorldsNoInteractionHandling(location)) {
            return
        }

        val fLocation = getFLocation(location)
        if (Config.worldsNoInteractionHandling.contains(fLocation.world)) {
            return
        }

        val factionAt = fLocation.getFaction()
        this.isCancelled = !processInteractAtEntity(factionAt, player, rightClicked, true)
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    fun EntityDamageByEntityEvent.onGadgets() {
        if (damager.type != EntityType.PLAYER || entityType != EntityType.ARMOR_STAND && entityType != EntityType.ITEM_FRAME) {
            return
        }

        val fLocation = getFLocation(entity.location)
        val factionAt = fLocation.getFaction()

        this.isCancelled = !processDamageAtGadget(factionAt, damager as Player, entity, true)
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    fun HangingBreakByEntityEvent.onGadgets() {
        val player = remover as? Player
        if (player == null || entity.type != EntityType.ITEM_FRAME) {
            return
        }

        val fLocation = getFLocation(entity.location)
        val factionAt = fLocation.getFaction()

        this.isCancelled = !processDamageAtGadget(factionAt, player, entity, true)
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    fun HangingPlaceEvent.onGadgets() {
        val location = entity.location
        if (player == null || checkWorldsNoInteractionHandling(location) || entity.type != EntityType.ITEM_FRAME) {
            return
        }

        if (!handleAlt(player!!.getFPlayer(), "BLOCK", this)) {
            return
        }

        val fLocation = getFLocation(location)
        val factionAt = fLocation.getFaction()

        this.isCancelled = !processBlockChangeInFactionLocation(
            factionAt, player!!,
            BlockChangeAction.PLACE,
            Material.ITEM_FRAME, true,
            location
        )
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    fun handleBlockPlace(event: BlockPlaceEvent) {
        val location = event.blockPlaced.location
        if (checkWorldsNoInteractionHandling(location)) return

        val player = event.player
        if (!this.handleAlt(player.getFPlayer(), "BLOCK", event)) return

        val factionLocation = getFLocation(location)
        val factionAt = factionLocation.getFaction()

        event.isCancelled = !processBlockChangeInFactionLocation(
            factionAt, player,
            BlockChangeAction.PLACE,
            event.blockPlaced.type, true,
            location
        )
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    fun handleBlockBreak(event: BlockBreakEvent) {
        val location = event.block.location
        if (checkWorldsNoInteractionHandling(location)) return

        val player = event.player
        if (!this.handleAlt(player.getFPlayer(), "BLOCK", event)) return

        val factionLocation = getFLocation(location)
        val factionAt = factionLocation.getFaction()

        val type = event.block.type
        if (!event.performWhitelistedMaterialCheck(type.name, factionAt, player.getFPlayer())) {
            return
        }

        if (worldGuard.hooked && (
            factionAt.isSafezone() && worldGuard.canPerformCustom(player, location, "destroy-block-safezone", false) ||
            factionAt.isWarzone() && worldGuard.canPerformCustom(player, location, "destroy-block-warzone", false)
        )) {
            return
        }

        event.isCancelled = !processBlockChangeInFactionLocation(
            factionAt, player,
            BlockChangeAction.BREAK,
            type, true,
            location
        )
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    fun handleBucketEmpty(event: PlayerBucketEmptyEvent) {
        val location = event.blockClicked.location
        if (checkWorldsNoInteractionHandling(location)) return

        val player = event.player
        if (!this.handleAlt(player.getFPlayer(), "BUCKET", event)) return

        val factionLocation = getFLocation(location)
        val factionAt = factionLocation.getFaction()

        event.isCancelled = !processBlockChangeInFactionLocation(
            factionAt, player,
            BlockChangeAction.EMPTY_BUCKET,
            event.blockClicked.type, true,
            location
        )
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    fun handleBucketFill(event: PlayerBucketFillEvent) {
        val location = event.blockClicked.location
        if (checkWorldsNoInteractionHandling(location)) return

        val player = event.player
        if (!this.handleAlt(player.getFPlayer(), "BUCKET", event)) return

        val factionLocation = getFLocation(location)
        val factionAt = factionLocation.getFaction()

        event.isCancelled = !processBlockChangeInFactionLocation(
            factionAt, player,
            BlockChangeAction.FILL_BUCKET,
            event.blockClicked.type, true,
            location
        )
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun PlayerJoinEvent.onGeneral() {
        val fPlayer = this.player.getFPlayer()
        val faction = fPlayer.getFaction()

        if (Config.teleportFromEnemyClaimOnJoin && faction.getRelationTo(fPlayer.getFactionAt()) === Relation.ENEMY) {
            with(Config.teleportFromEnemyClaimLocation) { if (this != null) player.teleport(this) }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    fun PlayerJoinEvent.onScoreboard() {
        if (!Config.scoreboardOptions.enabled || scoreboard == null) return
        asyncIf(scoreboard!!.javaClass === InternalBoard::class.java) {
            scoreboard!!.show(player.getFPlayer())
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    fun PlayerQuitEvent.onScoreboard() {
        scoreboard?.hide(this.player.getFPlayer())
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    fun PlayerCommandPreprocessEvent.onEnemyNearby() {
        val fPlayer = this.player.getFPlayer()
        val command = this.message.split(" ")[0]

        if (
                Bukkit.getPluginCommand(command.substring(1)) == null ||
                !Config.nearbyEnemyDisallowedCommands.any { command.equals("/$it", ignoreCase = true) } ||
                fPlayer.noEnemiesNearby(Config.nearbyEnemyDisallowedCommandsDistance)
        ) return

        this.isCancelled = true
        fPlayer.message(Message.nearbyEnemyDisallowedCommands.format(command))
    }

    private fun <E> handleAlt(fPlayer: FPlayer, action: String, event: E): Boolean where E : Event, E : Cancellable {
        if (!fPlayer.alt) return true
        event.isCancelled = true

        val message = when (action) {
            "BUCKET" -> Message.altUseBucket
            "HURT" -> Message.altHurtEntities
            "BLOCK" -> Message.altBuildBreak
            "INTERACT_ENTITY" -> Message.altInteractEntity
            "INTERACT" -> Message.altInteract
            else -> null
        }

        if (message != null) fPlayer.message(message)
        return false
    }

    private fun processDamageAtGadget(factionAt: Faction, player: Player, entity: Entity, notify: Boolean): Boolean {
        if (factionAt.isWilderness()) {
            return true
        }

        val fPlayer = PlayerManager.getFPlayer(player)
        val faction = fPlayer.getFaction()

        if (fPlayer.inBypass || factionAt == faction) {
            return true
        }

        val notAllowed = when {
            factionAt.isWarzone() -> !ProtectionConfig.allowGadgetDamageInWarzone
            factionAt.isSafezone() -> !ProtectionConfig.allowGadgetDamageInSafezone
            else -> !factionAt.relationPerms.getPermForRelation(factionAt.getRelationTo(faction), PlayerAction.DAMAGE_GADGET)
        }

        if (notAllowed && notify) {
            actionbar(
                    player, Message.youCannotDamageThisGadget,
                    entity.type.name.toLowerCase(), factionAt.tag
            )
        }

        return !notAllowed
    }

    private fun processInteractAtEntity(factionAt: Faction, player: Player, entity: Entity, notify: Boolean): Boolean {
        if (factionAt.isWilderness() || entity is Player) {
            return true
        }

        val fPlayer = PlayerManager.getFPlayer(player)
        val faction = fPlayer.getFaction()

        if (fPlayer.inBypass || factionAt == faction || (factionAt != faction && allowedInteractableEntitiesInOtherFactionLand.getOrDefault(entity.type, false))) {
            return true
        }

        // Cancel if not in whitelist.
        val notAllowed = when {
            factionAt == faction -> false
            factionAt.isWarzone() -> !ProtectionConfig.allowedInteractableEntitiesInWarzone[entity.type]!!
            factionAt.isSafezone() -> !ProtectionConfig.allowedInteractableEntitiesInSafezone[entity.type]!!
            // We allowed wilderness in default at the beginning.
            else -> !factionAt.relationPerms.getPermForRelation(factionAt.getRelationTo(faction), when (entity.type) {
                EntityType.ARMOR_STAND, EntityType.ITEM_FRAME -> PlayerAction.USE_GADGET
                else -> PlayerAction.USE_ENTITY
            })
        }
        if (notAllowed && notify) {
            actionbar(
                    player, Message.youCannotInteractWithThisEntity,
                    entity.type.name.toLowerCase(), factionAt.tag
            )
        }
        return !notAllowed
    }

    private fun Cancellable.performWhitelistedMaterialCheck(
        type: String?,
        factionAt: Faction,
        factionPlayer: FPlayer
    ): Boolean = with (type) {
        if (this == null) {
            return true
        }

        val matchesWildCard = materialWhitelist.any { it.endsWith("_*") && this.matches("${it.substring(0..(it.length - 1) - 2)}(_.+)?".toRegex()) }
        if (!matchesWildCard && this !in materialWhitelist) {
            return true
        }

        if (factionAt != factionPlayer.getFaction() && !factionAt.isWilderness()) {
            this@performWhitelistedMaterialCheck.isCancelled = true
        }

        return false
    }

    companion object {
        fun processBlockChangeInFactionLocation(
            factionAt: Faction,
            player: Player,
            action: BlockChangeAction,
            changedMaterial: Material,
            notify: Boolean,
            location: Location
        ): Boolean {
            val factionPlayer = PlayerManager.getFPlayer(player)
            if (factionPlayer.inBypass) return true
            if (materialWhitelist.contains(changedMaterial.toString())) return true
            if (action == BlockChangeAction.BREAK && factionPlayer.getFaction() == factionAt
                && ProtectionConfig.denyBreakingBlocksWhenEnemyNear.contains(changedMaterial.getXMaterial())
                && !factionPlayer.noEnemiesNearby(ProtectionConfig.denyBreakingBlocksEnemyNearByRadius)
            ) {
                actionbar(player, Message.blockBreakDeniedEnemyNearby, changedMaterial.toString())
                return false
            }


            val allowed = when (action) {
                BlockChangeAction.PLACE ->
                    handleActionProcessing(PlayerAction.PLACE_BLOCK, player, factionPlayer, factionAt, false, location)

                BlockChangeAction.BREAK ->
                    handleActionProcessing(PlayerAction.BREAK_BLOCK, player, factionPlayer, factionAt, false, location)
                        || (ProtectionConfig.whiteListedBreakableBlocksInOtherFactionLand.contains(changedMaterial)
                            && (!factionAt.isSystemFaction() || ProtectionConfig.whiteListedBreakableBlocksIncludesSystemFactions))

                BlockChangeAction.EMPTY_BUCKET ->
                    handleActionProcessing(PlayerAction.EMPTY_BUCKET, player, factionPlayer, factionAt, false, location)

                BlockChangeAction.FILL_BUCKET ->
                    handleActionProcessing(PlayerAction.FILL_BUCKET, player, factionPlayer, factionAt, false, location)
            }


            if (!allowed && notify) {
                if (action.isBucketAction()) {
                    actionbar(player, Message.actionDeniedInOtherFactionsLand, action.toString(), factionAt.tag)
                } else {
                    actionbar(
                        player,
                        Message.blockChangeDeniedInOtherFactionsLand,
                        action.toString(),
                        changedMaterial.name.toLowerCase(),
                        factionAt.tag
                    )
                }
            }

            return allowed
        }

        fun canPerformActionInFactionLocation(
            factionAt: Faction,
            player: Player,
            inHand: ItemStack?,
            action: Action,
            clickedBlock: Block,
            notify: Boolean,
            location: Location
        ): Boolean {
            val factionPlayer = PlayerManager.getFPlayer(player)
            return factionPlayer.inBypass || processActionInFactionsLand(player, factionPlayer, factionAt, inHand, action, clickedBlock, notify, location)
        }


        fun processActionInFactionsLand(
            player: Player,
            fPlayer: FPlayer,
            factionAt: Faction,
            inHand: ItemStack?,
            action: Action,
            clickedBlock: Block,
            notify: Boolean,
            location: Location
        ): Boolean {

            if (materialWhitelist.contains(clickedBlock.type.toString())) return true


            if (action == Action.RIGHT_CLICK_BLOCK && player.isSneaking
                && (ProtectionConfig.shiftRightClickableWhiteListInOtherFactionsLand[inHand?.type]?.contains(clickedBlock.type) == true)
            ) return true


            val handTypeRaw = inHand?.type.toString()

            if (action == Action.PHYSICAL) {
                val exactMaterial = clickedBlock.type.getXMaterial()
                if (exactMaterial == XMaterial.TRIPWIRE
                    && !handleActionProcessing(PlayerAction.HOOK, player, fPlayer, factionAt, true, location)) {
                    return false
                }

                if (exactMaterial.isPressurePlate()
                    && !handleActionProcessing(PlayerAction.PRESSURE_PLATE, player, fPlayer, factionAt, true, location)
                ) {
                    // Handle action processing will notify them.
                    return false
                }

                if (exactMaterial === XMaterial.FARMLAND && !handleActionProcessing(PlayerAction.TRAMPLE_SOIL, player, fPlayer, factionAt, true, location)) {
                    return false
                }
            }

            if (action == Action.RIGHT_CLICK_BLOCK
                // MONSTER_EGG wont work on 1.13+, using string to make it more future proof than a mob egg whitelist.
                && (handTypeRaw == "MONSTER_EGG" || handTypeRaw.contains("SPAWN_EGG"))
                && !handleActionProcessing(PlayerAction.SPAWN_EGG, player, fPlayer, factionAt, true, location)
            ) {
                // Handle action processing will notify them.
                return false
            }

            if (action == Action.RIGHT_CLICK_BLOCK) {
                if (!factionAt.isWilderness() && (clickedBlock.type in blackListedInteractionBlocksInOtherFactionLand
                        || inHand?.type in blackListedInteractionBlocksInOtherFactionLand)) {
                    return handleActionProcessing(PlayerAction.USE_BLACKLISTED_BLOCKS, player, fPlayer, factionAt, notify, location)
                }

                val playerAction: PlayerAction? = when (XMaterial.matchXMaterial(clickedBlock.type)) {
                    XMaterial.BARREL -> PlayerAction.BARREL
                    XMaterial.SHULKER_BOX -> PlayerAction.SHULKER
                    XMaterial.CHEST, XMaterial.TRAPPED_CHEST -> PlayerAction.CHEST
                    XMaterial.ENDER_CHEST -> PlayerAction.ENDER_CHEST
                    XMaterial.ANVIL, XMaterial.CHIPPED_ANVIL, XMaterial.DAMAGED_ANVIL -> PlayerAction.ANVIL
                    XMaterial.ENCHANTING_TABLE -> PlayerAction.ENCHANTING_TABLE
                    XMaterial.FURNACE, XMaterial.BLAST_FURNACE -> PlayerAction.FURNACE
                    XMaterial.BREWING_STAND -> PlayerAction.BREWING_STAND
                    XMaterial.CAULDRON -> PlayerAction.CAULDRON
                    XMaterial.DROPPER -> PlayerAction.DROPPER
                    XMaterial.ACACIA_BUTTON, XMaterial.BIRCH_BUTTON, XMaterial.DARK_OAK_BUTTON, XMaterial.JUNGLE_BUTTON,
                    XMaterial.OAK_BUTTON, XMaterial.SPRUCE_BUTTON, XMaterial.STONE_BUTTON, XMaterial.CRIMSON_BUTTON,
                    XMaterial.WARPED_BUTTON -> PlayerAction.BUTTON
                    XMaterial.LEVER -> PlayerAction.LEVER
                    XMaterial.ACACIA_FENCE_GATE, XMaterial.BIRCH_FENCE_GATE, XMaterial.DARK_OAK_FENCE_GATE,
                    XMaterial.JUNGLE_FENCE_GATE, XMaterial.OAK_FENCE_GATE, XMaterial.SPRUCE_FENCE_GATE,
                    XMaterial.CRIMSON_FENCE_GATE, XMaterial.WARPED_FENCE_GATE -> PlayerAction.FENCE_GATE
                    XMaterial.HOPPER, XMaterial.HOPPER_MINECART -> PlayerAction.HOPPER
                    XMaterial.LECTERN -> PlayerAction.LECTERN
                    XMaterial.COMPARATOR -> PlayerAction.COMPARATOR
                    XMaterial.REPEATER -> PlayerAction.REPEATER
                    XMaterial.DISPENSER -> PlayerAction.DISPENSER
                    XMaterial.ACACIA_DOOR, XMaterial.BIRCH_DOOR, XMaterial.DARK_OAK_DOOR, XMaterial.JUNGLE_DOOR,
                    XMaterial.OAK_DOOR, XMaterial.SPRUCE_DOOR, XMaterial.IRON_DOOR, XMaterial.CRIMSON_DOOR, XMaterial.WARPED_DOOR -> PlayerAction.DOOR
                    XMaterial.ACACIA_TRAPDOOR, XMaterial.BIRCH_TRAPDOOR, XMaterial.DARK_OAK_TRAPDOOR, XMaterial.JUNGLE_TRAPDOOR,
                    XMaterial.OAK_TRAPDOOR, XMaterial.SPRUCE_TRAPDOOR, XMaterial.IRON_TRAPDOOR, XMaterial.CRIMSON_TRAPDOOR,
                    XMaterial.WARPED_TRAPDOOR -> PlayerAction.TRAPDOOR
                    else -> null
                }

                if (playerAction != null) return handleActionProcessing(playerAction, player, fPlayer, factionAt, notify, location)
            }

            // want it to reach this
            // trying to design a checks system
            return true
        }

        fun handleActionProcessing(
            playerAction: PlayerAction,
            player: Player,
            fPlayer: FPlayer,
            factionAt: Faction,
            notify: Boolean,
            location: Location
        ): Boolean {
            var result = true
            val faction = fPlayer.getFaction()
            val fLocation = getFLocation(location)
            val relation = factionAt.getRelationTo(faction)

            val permissionCheck = when {
                factionAt.isWarzone() -> ProtectionConfig.playerActionsInWarzone.getOrDefault(playerAction, false)
                factionAt.isSafezone() -> ProtectionConfig.playerActionsInSafezone.getOrDefault(playerAction, false)
                factionAt.isWilderness() -> ProtectionConfig.playerActionsInWilderness.getOrDefault(playerAction, true)
                factionAt == faction -> fPlayer.role.canDoPlayerAction(playerAction)
                else -> factionAt.relationPerms.getPermForRelation(relation, playerAction)
                    || ((fLocation.getAccessPoint().canPerform(fPlayer, playerAction) || fLocation.getAccessPoint().canPerform(faction, playerAction)) && overrideActionsForRelation[relation]?.get(playerAction) ?: true)
                    || (Config.powerSettings.dtrBased && factionAt.getPower() <= Config.powerSettings.dtrBasedRaidableAt)
            }

            if (!permissionCheck) {
                result = false
            }

            val actionsWhenOffline = overrideActionsWhenFactionOffline
            if (factionAt.isActionsWhenOfflineCompatible(actionsWhenOffline)) {
                val value = actionsWhenOffline.actions[playerAction]
                if (value != null) result = value
            }

            if (notify && !result) actionbar(
                player, Message.actionDeniedInOtherFactionsLand,
                playerAction.actionName, factionAt.tag
            )
            return result
        }
    }

    // --
}