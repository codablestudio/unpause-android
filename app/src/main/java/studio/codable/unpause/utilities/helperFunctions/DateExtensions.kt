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
    cal.clear()
    cal.set(Calendar.YEAR, year)
    cal.set(Calendar.MONTH, month)
    cal.set(Calendar.DAY_OF_MONTH, day)
    cal.set(Calendar.HOUR_OF_DAY, hour)
    cal.set(Calendar.MINUTE, minute)
    cal.set(Calendar.SECOND, second)
    cal.set(Calendar.MILLISECOND, millisecond)

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