package studio.codable.unpause.model.firestore

import com.google.firebase.firestore.DocumentId
import studio.codable.unpause.model.Shift
import studio.codable.unpause.model.User

data class FirestoreUser(
    @DocumentId
    val documentId: String = "",
    val email: String = "",
    val firstName: String? = "",
    val lastName: String? = "",
    val shifts: List<FirestoreShift>? = null
) {
    fun toUser(): User {
        return User(documentId, email, firstName, lastName)
    }

    fun extractShifts(): List<Shift> {
        return shifts?.map { fs ->
            Shift(
                arrivalTime = fs.arrivalTime?.toDate(),
                exitTime = fs.exitTime?.toDate(),
                description = fs.description
            )
        } ?: arrayListOf()
    }
}