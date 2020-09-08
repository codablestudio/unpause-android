package studio.codable.unpause.utilities.helperFunctions

import java.util.*

fun areDatesEqual(first: Date?, second: Date?): Boolean {
    if (first == null && second == null) return true
    if (first != null && second == null) return false
    if (first == null && second != null) return false

    return first!!.time / 1000 == second!!.time / 1000
}

/**
 * Returns week as a range of two dates (Monday and Sunday)
 */
fun getCurrentWeek() : DateRange {
    val calendar = Calendar.getInstance()
    val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
    val firstDayOfWeek: Date
    val lastDayOfWeek: Date

    if (dayOfWeek == Calendar.SUNDAY) {
        calendar.add(
            Calendar.DATE, -1 * (dayOfWeek -
                    Calendar.MONDAY) - 7
        )
        firstDayOfWeek = calendar.time
        calendar.add(Calendar.DATE, 6)
        lastDayOfWeek = calendar.time
    } else {
        calendar.add(
            Calendar.DATE, -1 * (dayOfWeek -
                    Calendar.MONDAY)
        );
        firstDayOfWeek = calendar.time;
        calendar.add(Calendar.DATE, 6);
        lastDayOfWeek = calendar.time;
    }

    return DateRange(firstDayOfWeek,lastDayOfWeek)
}

/**
 * Used to define a date range by its first and last day,
 * represented by dates
 */
data class DateRange(var firstDate : Date, var lastDate : Date)
