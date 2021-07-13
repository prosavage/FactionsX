package net.prosavage.factionsx.persist

import net.prosavage.factionsx.addonframework.AddonPlugin
import java.io.File

object RosterConfig {
    @Transient
    private val instance = this

    var maxRosterMembers = 10

    var rosterLocked = false

    var rosterListPermission = "factionsx.player.roster.list"
    var rosterListHelp = "&7list roster members."
    var rosterListHeader = "&7--- &6Roster Members &7---"
    var rosterListEntry = "&7%1\$s. - &6%2\$s &7as &6%3\$s&7."

    var rosterAddMessage = "&7You have added &6%1\$s&7 to the roster with role: &6%2\$s&7."
    var rosterAddApex = "&7You cannot set a roster member's role to &6%1\$s."
    var rosterAddHaveBeenAdded = "&7You have been added to &6%1\$s's&7 roster."
    var rosterAddAdded = "&6%1\$s &7is already in the roster."
    var rosterAddFull = "&7Your roster is &6full&7, please remove someone first."

    var rosterRemoveMessage = "&7You have removed &6%1\$s&7 from the roster."

    var rosterAddPermission = "factionsx.player.roster.add"
    var rosterRemovePermission = "factionsx.player.roster.remove"
    var rosterAddHelp = "&7Add a member to roster."

    var rosterHelp = "&7View roster commands."


    var rosterJoinDefault = "&7You have joined &6%1\$s&7 without removing anyone as the faction is not full."
    var rosterJoinRemoved = "&7You have replaced &6%1\$s&7 in the faction."
    var rosterJoinSuccess = "&6%1\$s has joined the faction using roster."
    var rosterJoinFailed = "&7There are no available spots in the faction, please ask a member to go offline."
    var rosterJoinRoleDoesNotExist = "&7Your roster entry's role no longer exists, defaulting to lowest role."
    var rosterJoinNotInRoster = "&7You are not in this faction's roster."
    var rosterJoinAlreadyHave = "&7You already are a part of a faction, please &6leave&7 it first."
    var rosterJoinHelp = "&7join a faction's roster."

    fun save(addon: AddonPlugin) {
        addon.configSerializer.save(instance, File(addon.dataFolder, "config.json"))
    }

    fun load(addon: AddonPlugin) {
        addon.configSerializer.load(instance, RosterConfig::class.java, File(addon.dataFolder, "config.json"))
    }
}