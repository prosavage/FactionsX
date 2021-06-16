package net.prosavage.factionsx.command.engine

class ConfirmData(var status: Boolean, var action: ConfirmAction) {

    fun startConfirmation(confirmAction: ConfirmAction) {
        this.apply {
            status = true
            action = confirmAction
        }
    }

    fun hasConfirmedAction(action: ConfirmAction): Boolean {
        return status && this.action == action
    }

    fun hasNotConfirmedAction(action: ConfirmAction): Boolean {
        return !status && this.action == action
    }

    fun clearConfirmation() {
        status = false
        action = ConfirmAction.NONE
    }
}


enum class ConfirmAction {
    PROMOTE,
    DISBAND,
    NONE
}