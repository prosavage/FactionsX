package net.prosavage.factionsx.util

object Patterns {
    val COLOR_RGB = Regex("#([A-Fa-f0-9]{6})")
    val VALUE_HOLDER_PERCENTAGE = Regex("\\[faction:value] \\b([1-9]|[1-9][0-9]|100)\\b")
    val CLAIMS_HOLDER = Regex("^claims:(\\w+)$")
}