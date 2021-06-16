package net.prosavage.factionsx.util

import com.cryptomorin.xseries.XMaterial
import net.prosavage.factionsx.core.Faction
import net.prosavage.factionsx.manager.FactionManager
import net.prosavage.factionsx.persist.config.Config
import net.prosavage.factionsx.persist.config.RoleConfig
import net.prosavage.factionsx.persist.data.Players
import java.util.*
import kotlin.Comparator
import kotlin.collections.ArrayList

object Migration {
    /**
     * Migrate all existing factions.
     */
    fun migrateFactions() {
        for (faction in FactionManager.getFactions()) {
            if (faction.isSystemFaction()) continue

            faction.factionRoles.roleHierarchy.values.forEach { role ->
                if (role.iconMaterial == null) {
                    role.iconMaterial = RoleConfig.defaultRoles
                            .getRoleFromString(role.roleTag)
                            ?.iconMaterial ?: XMaterial.DIAMOND_HELMET
                    logColored("PermGUIUpdate: Migrated ${faction.tag}'s ${role.roleTag} role icon from null -> ${role.iconMaterial}")
                }
            }

            // If we toggle shields off, disable any factions with shields on startup.
            if (Config.factionShieldEnabled.not() && faction.isShielded()) {
                faction.disableShield()
            }

            if (Config.factionShieldEnabled && faction.shieldTimeStart != null) this.handleShield(faction)
            if (faction.bank == null) faction.bank = Faction.Bank(0.0, 1, ArrayList())
        }
    }

    /**
     * Migrate all existing players.
     */
    fun migratePlayers() {
        val inactiveMigration = Config.deleteInactivePlayers
        if (!inactiveMigration.enabled) return

        val iterator = Players.fplayers.values.iterator()
        while (iterator.hasNext()) {
            val player = iterator.next()

            if (player.isOnline() || System.currentTimeMillis() < (player.timeAtLastLogin + inactiveMigration.toMillis())) {
                continue
            }

            if (!player.hasFaction()) {
                iterator.remove()
                continue
            }

            val faction = player.getFaction()
            val members = faction.getMembers().filter { it != player }

            if (!player.isLeader()) {
                faction.removeMember(player, true)
                iterator.remove()
                continue
            }

            if (members.isEmpty()) {
                FactionManager.deleteFaction(faction)
                iterator.remove()
                continue
            }

            val roles = faction.factionRoles
            val highestRoleMember = members.maxWith(Comparator { m1, m2 ->
                val firstRole = m1.role
                val secondRole = m2.role

                if (firstRole == secondRole) return@Comparator 0
                if (roles.isHigherRole(firstRole, secondRole)) 1 else -1
            })

            highestRoleMember?.let {
                faction.setLeader(it)
                it.role = roles.getMaximumRole()
            }
            iterator.remove()
        }
    }

    /**
     * Handle the shield of a faction.
     *
     * @param faction [Faction] instance of the faction to handle shield for.
     */
    private fun handleShield(faction: Faction) {
        if (faction.isShielded() && faction.shieldEndDate === null) {
            faction.shielded = false
        }

        if (faction.isShielded() && faction.shieldEndDate!!.before(Date())) {
            faction.shielded = false
            faction.shieldEndDate = null
            faction.setShield()
            return
        }

        if (faction.isShielded()) {
            faction.setShield(faction.shieldEndDate!!)
            faction.registerShieldEndTimer(faction.shieldEndDate!!)
            return
        }

        faction.setShield()
    }
}