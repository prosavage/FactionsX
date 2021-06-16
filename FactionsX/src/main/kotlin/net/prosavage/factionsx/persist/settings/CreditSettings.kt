package net.prosavage.factionsx.persist.settings

data class CreditSettings(val enabled: Boolean) {
    val startingCredits: Double = 50.0
    val maximumCredits: Double = 500.0
    val minimumCreditsPay: Double = 1.0
}