package net.prosavage.factionsx.persist.config

import com.cryptomorin.xseries.XMaterial
import net.prosavage.baseplugin.serializer.Serializer
import net.prosavage.factionsx.FactionsX
import net.prosavage.factionsx.core.CustomRole
import net.prosavage.factionsx.core.FactionRoles
import net.prosavage.factionsx.persist.IConfigFile
import net.prosavage.factionsx.util.MemberAction
import net.prosavage.factionsx.util.PlayerAction
import java.io.File

object RoleConfig : IConfigFile {

    @Transient
    private val instance = this

    var wildernessMemberRoleTag = "member"


    // Marked as transient so the person cannot edit it anyways :D
    @Transient
    var defaultWildernessRole = CustomRole(
            "",
            wildernessMemberRoleTag,
            PlayerAction.values().toMutableList(),
            mutableListOf(),
            HashMap(),
            XMaterial.GREEN_BANNER
    )


    var defaultRoles =
            FactionRoles(
                    hashMapOf(
                            // The top of the role ladder does not have an actual perm check
                            // The values are there in case we need to add another role up on top when we are the faction leader.
                            4 to CustomRole(
                                    "***",
                                    "Leader",
                                    PlayerAction.values().filter { action -> action != PlayerAction.HURT_PLAYER }.toMutableList(),
                                    MemberAction.values().toMutableList(),
                                    HashMap(),
                                    XMaterial.DIAMOND_HELMET
                            ),
                            3 to CustomRole(
                                    "**",
                                    "Administrator",
                                    PlayerAction.values().filter { action -> action != PlayerAction.HURT_PLAYER }.toMutableList(),
                                    MemberAction.values().toMutableList().filter { action -> action != MemberAction.DISBAND }
                                            .toMutableList(),
                                    HashMap(),
                                    XMaterial.IRON_HELMET
                            ),
                            2 to CustomRole(
                                    "*",
                                    "Moderator",
                                    PlayerAction.values().filter { action -> action != PlayerAction.HURT_PLAYER }.toMutableList(),
                                    mutableListOf(MemberAction.INVITE, MemberAction.KICK, MemberAction.PROMOTE, MemberAction.RELATION),
                                    HashMap(),
                                    XMaterial.GOLDEN_HELMET
                            ),
                            1 to CustomRole(
                                    "+",
                                    "Member",
                                    PlayerAction.values().filter { action -> action != PlayerAction.HURT_PLAYER }.toMutableList(),
                                    mutableListOf(),
                                    HashMap(),
                                    XMaterial.CHAINMAIL_HELMET
                            ),
                            0 to CustomRole(
                                    "-",
                                    "Recruit",
                                    mutableListOf(PlayerAction.BREAK_BLOCK, PlayerAction.PLACE_BLOCK),
                                    mutableListOf(),
                                    HashMap(),
                                    XMaterial.LEATHER_HELMET
                            )
                    )
            )


    override fun save(factionsx: FactionsX) {
        Serializer(false, factionsx.dataFolder, factionsx.logger)
                .save(RoleConfig.instance, File("${factionsx.dataFolder}/config", "role-config.json"))
    }

    override fun load(factionsx: FactionsX) {
        Serializer(false, factionsx.dataFolder, factionsx.logger)
                .load(
                        RoleConfig.instance,
                        RoleConfig::class.java,
                        File("${factionsx.dataFolder}/config", "role-config.json")
                )
    }

}