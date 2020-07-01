package studio.codable.unpause.utilities.extensions

import studio.codable.unpause.model.Shift

fun List<Shift>?.active(): Shift {
    this?.let {
        for (shift in this) {
            if (!shift.isCheckedOut()) {
                return shift
            }
        }
    }
    throw IllegalStateException("There are no active shifts")
}

fun List<Shift>?.existActive(): Boolean {
    this?.let {
        for (shift in this) {
            if (!shift.isCheckedOut()) {
                return true
            }
        }
    }
    return false
}