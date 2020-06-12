package studio.codable.unpause.utilities.helperFunctions

import java.util.*

fun areDatesEqual(first: Date?, second: Date?): Boolean {
    if (first == null && second == null) return true
    if (first != null && second == null) return false
    if (first == null && second != null) return false

    return first!!.time / 1000 == second!!.time / 1000
}




