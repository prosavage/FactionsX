package net.prosavage.factionsx.util

object TimeHelper {
    /**
     * Time unit constants.
     */
    private const val SECONDS_IN_MILLIS: Long = 1000
    private const val MINUTES_IN_MILLIS: Long = SECONDS_IN_MILLIS * 60
    private const val HOURS_IN_MILLIS: Long = MINUTES_IN_MILLIS * 60
    private const val DAYS_IN_MILLIS: Long = HOURS_IN_MILLIS * 24
    private const val WEEKS_IN_MILLIS: Long = DAYS_IN_MILLIS * 7
    private const val MONTHS_IN_MILLIS: Long = DAYS_IN_MILLIS * 30
    private const val YEARS_IN_MILLIS: Long = DAYS_IN_MILLIS * 365

    /**
     * Default check to be used when appending accordingly using [prettyFormat].
     */
    private val defaultCheck: (Long) -> Boolean = { it > 0 }

    /**
     * Append a [StringBuilder] accordingly with plural and singular strings.
     * The append will not take effect if the predicate is false.
     */
    private fun StringBuilder.appendAccordingly(
            predicate: (Long) -> Boolean,
            toCheck: Long,
            plural: String,
            singular: String,
            separator: String = ", "
    ) {
        if (!predicate(toCheck)) return
        val preciseString = if (toCheck == 1L) singular else plural
        append(toCheck).append(" $preciseString").append(separator)
    }

    /**
     * Get pretty formatted time of milliseconds in a [String].
     */
    fun Long.prettyFormat(): String {
        var millis = this

        val years = millis / YEARS_IN_MILLIS; millis -= YEARS_IN_MILLIS * years
        val months = millis / MONTHS_IN_MILLIS; millis -= MONTHS_IN_MILLIS * months
        val weeks = millis / WEEKS_IN_MILLIS; millis -= WEEKS_IN_MILLIS * weeks
        val days = millis / DAYS_IN_MILLIS; millis -= DAYS_IN_MILLIS * days
        val hours = millis / HOURS_IN_MILLIS; millis -= HOURS_IN_MILLIS * hours
        val minutes = millis / MINUTES_IN_MILLIS; millis -= MINUTES_IN_MILLIS * minutes
        val seconds = millis / SECONDS_IN_MILLIS

        return StringBuilder().apply {
            appendAccordingly(defaultCheck, years, "years", "year")
            appendAccordingly(defaultCheck, months, "months", "month")
            appendAccordingly(defaultCheck, weeks, "weeks", "week")
            appendAccordingly(defaultCheck, days, "days", "day")
            appendAccordingly(defaultCheck, hours, "hours", "hour")
            appendAccordingly(defaultCheck, minutes, "minutes", "minute")
            appendAccordingly(defaultCheck, seconds, "seconds", "second")
        }.toString().trim().dropLast(1)
    }
}