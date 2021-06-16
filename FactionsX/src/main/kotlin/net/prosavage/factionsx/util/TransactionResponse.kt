package net.prosavage.factionsx.util

import net.prosavage.factionsx.hook.vault.VaultHook
import net.prosavage.factionsx.persist.config.EconConfig

class TransactionResponse(
    /**
     * Amount modified by calling method
     */
    val amount: Double,
    /**
     * New balance of account
     */
    var balance: Double,
    val type: ResponseType,
    val errorMessage: String
) {
    /**
     * Enum for types of Responses indicating the status of a method call.
     */
    enum class ResponseType(val id: Int) {
        SUCCESS(1), FAILURE(2), ECON_DISABLED(3), NOT_IMPLEMENTED(4);

    }

    /**
     * Checks if an operation was successful
     * @return Value
     */
    fun transactionSuccess(): Boolean {
        return when (type) {
            ResponseType.SUCCESS -> true
            else -> false
        }
    }


    companion object {
        fun econDisabledResponse(amount: Double): TransactionResponse {
            return TransactionResponse(amount, 0.0, ResponseType.ECON_DISABLED, EconConfig.econDisabledMsg)
        }

        fun notEnoughResponse(amount: Double, balance: Double): TransactionResponse {
            return TransactionResponse(amount, 0.0, ResponseType.FAILURE, EconConfig.econNotEnoughMsg
                    .replace("{price}", VaultHook.format(amount))
                    .replace("{balance}", VaultHook.format(balance))
            )
        }
    }
}