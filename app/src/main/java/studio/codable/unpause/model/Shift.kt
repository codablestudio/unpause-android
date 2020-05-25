package studio.codable.unpause.model

import java.util.*

data class Shift(
    var arrivalTime: Date?,
    var exitTime: Date? = null,
    var description: String? = null,
    val id: String = ""
) {
    fun addExit(exitTime: Date, description: String): Shift {
        this.exitTime = exitTime
        this.description = description
        return this
    }

    fun isCheckedOut(): Boolean {
        return exitTime != null
    }
}