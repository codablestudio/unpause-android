package studio.codable.unpause.model.firestore

import com.google.firebase.Timestamp
import studio.codable.unpause.model.Shift

data class FirestoreShift(
    var arrivalTime: Timestamp? = null,
    var exitTime: Timestamp? = null,
    var description: String? = ""
) {
    constructor(shift: Shift) : this() {

        val exitTime: Timestamp? = if (shift.exitTime == null) {
            null
        } else {
            Timestamp(shift.exitTime!!)
        }

        this.arrivalTime = Timestamp(shift.arrivalTime!!)
        this.exitTime = exitTime
        this.description = shift.description
    }
}