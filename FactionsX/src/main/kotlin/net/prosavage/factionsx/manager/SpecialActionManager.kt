package net.prosavage.factionsx.manager

import net.prosavage.factionsx.util.SpecialAction

object SpecialActionManager {

    private val specialActions = mutableSetOf<SpecialAction>()


    fun getActionFromString(action: String): SpecialAction? {
        return specialActions.find { specialAction -> specialAction.name.equals(action, true) }
    }

    fun getRegisteredActionNames(): List<String> {
        return specialActions.map(SpecialAction::name).toList()
    }

    fun getAllRegisteredActions(): Set<SpecialAction> {
        return specialActions
    }

    fun addSpecialAction(specialAction: SpecialAction) {
        specialActions.add(specialAction)
    }

    fun removeSpecialAction(specialAction: SpecialAction) {
        specialActions.remove(specialAction)
    }

}