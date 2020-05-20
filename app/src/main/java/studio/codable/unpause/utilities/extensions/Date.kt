package studio.codable.unpause.utilities.extensions

import java.text.DateFormat
import java.util.*

fun createDate(year: Int, month: Int, dayOfMonth: Int): Date {
    val cal = Calendar.getInstance()
    cal.clear()
    cal.set(Calendar.YEAR, year)
    cal.set(Calendar.MONTH, month)
    cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
    return cal.time
}

/**
 * @param style one of DateFormat.SHORT, MEDIUM, LONG
 */
fun Date.getDateString(style: Int): String {
    val dateFormat = DateFormat.getDateInstance(style)
    return dateFormat.format(this)
}

/**
 * @param style one of DateFormat.SHORT, MEDIUM, LONG
 */
fun Date.getTimeString(style: Int): String {
    val dateFormat = DateFormat.getTimeInstance(style)
    return dateFormat.format(this)
}