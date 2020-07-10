package studio.codable.unpause.utilities.helperFunctions

import androidx.annotation.Nullable
import java.text.SimpleDateFormat
import java.util.*

fun Date?.toPattern(@Nullable pattern: String?): String {

    if (this == null) return ""

    val pat = pattern ?: "HH:mm dd.MM.yyyy"

    val format = SimpleDateFormat(pat, Locale.GERMANY)
    return format.format(this)
}

/**
 * @param month January = 0, December = 11
 */
fun createDate(
    year: Int,
    month: Int,
    day: Int,
    hour: Int,
    minute: Int,
    second: Int = 0,
    millisecond: Int = 0
): Date {
    val cal = Calendar.getInstance()
    cal.apply {
        clear()
        set(Calendar.YEAR, year)
        set(Calendar.MONTH, month)
        set(Calendar.DAY_OF_MONTH, day)
        set(Calendar.HOUR_OF_DAY, hour)
        set(Calendar.MINUTE, minute)
        set(Calendar.SECOND, second)
        set(Calendar.MILLISECOND, millisecond)
    }
    return cal.time
}

fun Date?.minute(): Int {
    if (this == null) return -1
    val cal = Calendar.getInstance()
    cal.clear()
    cal.time = this
    return cal.get(Calendar.MINUTE)
}

fun Date?.hour(): Int {
    if (this == null) return -1
    val cal = Calendar.getInstance()
    cal.clear()
    cal.time = this
    return cal.get(Calendar.HOUR_OF_DAY)
}

fun Date?.day(): Int {
    if (this == null) return -1
    val cal = Calendar.getInstance()
    cal.clear()
    cal.time = this
    return cal.get(Calendar.DAY_OF_MONTH)
}

fun Date?.month(): Int {
    if (this == null) return -1
    val cal = Calendar.getInstance()
    cal.clear()
    cal.time = this
    return cal.get(Calendar.MONTH)
}

fun Date?.year(): Int {
    if (this == null) return -1
    val cal = Calendar.getInstance()
    cal.clear()
    cal.time = this
    return cal.get(Calendar.YEAR)
}

/**
 * Returns the same date but at 00:00 (useful for date comparison)
 */
fun Date?.date() : Date {
    val cal = Calendar.getInstance()
    if (this != null) {
        cal.clear()
        cal.time = this
    }
    cal.set(Calendar.HOUR_OF_DAY, 0)
    cal.set(Calendar.MINUTE, 0)
    cal.set(Calendar.SECOND, 0)
    cal.set(Calendar.MILLISECOND, 0)
    return cal.time
}

/**
 * Returns day of a week as a number
 */
fun Date?.dayOfWeek() : Int {
    if (this == null) return -1
    val cal = Calendar.getInstance()
    cal.clear()
    cal.time = this
    val day = cal.get(Calendar.DAY_OF_WEEK)-2
    return if (day<0) 6 else day
}

/**
 * Returns day of a month as a number (starting with 1)
 */
fun Date?.dayOfMonth() : Int {
    if (this == null) return -1
    val cal = Calendar.getInstance()
    cal.clear()
    cal.time = this
    return cal.get(Calendar.DAY_OF_MONTH)
}