package net.prosavage.factionsx.command.engine

import net.prosavage.factionsx.core.Permission
import net.prosavage.factionsx.util.MemberAction
import net.prosavage.factionsx.util.SpecialAction


class CommandRequirementsBuilder {

    var asPlayer = false
    var asFactionMember = false
    var asLeader = false
    var permission: Permission? = null
    var memberAction: MemberAction? = null
    var specialAction: SpecialAction? = null
    var rawPermission: String? = null
    var price: Double? = null


    fun asPlayer(value: Boolean): CommandRequirementsBuilder {
        this.asPlayer = value
        return this
    }

    fun asLeader(value: Boolean): CommandRequirementsBuilder {
        this.asLeader = value
        return this
    }

    fun withPermission(permission: Permission): CommandRequirementsBuilder {
        this.permission = permission
        return this
    }

    fun asFactionMember(value: Boolean): CommandRequirementsBuilder {
        // Gotta be a player to be a faction member
        this.asFactionMember = value
        this.asPlayer = true
        return this
    }

    fun withPrice(price: Double): CommandRequirementsBuilder {
        this.price = price
        this.asFactionMember = true
        this.asPlayer = true
        return this
    }


    fun withSpecialAction(specialAction: SpecialAction): CommandRequirementsBuilder {
        this.specialAction = specialAction
        this.asFactionMember = true
        this.asPlayer = true
        return this
    }

    fun withMemberAction(memberAction: MemberAction): CommandRequirementsBuilder {
        this.memberAction = memberAction
        this.asFactionMember = true
        this.asPlayer = true
        return this
    }

    fun withRawPermission(node: String): CommandRequirementsBuilder {
        this.rawPermission = node
        return this
    }

    fun build(): CommandRequirements {
        return CommandRequirements(permission, asPlayer, asFactionMember, asLeader, memberAction, specialAction, rawPermission, price
                ?: 0.0)
    }


}