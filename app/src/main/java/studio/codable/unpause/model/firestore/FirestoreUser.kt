package studio.codable.unpause.model.firestore

import com.google.firebase.firestore.DocumentId
import studio.codable.unpause.model.Shift
import studio.codable.unpause.model.User

data class FirestoreUser(
    @DocumentId
    var documentId: String = "",
    var email: String = "",
    var firstName: String? = "",
    var lastName: String? = "",
    var shifts: List<FirestoreShift>? = null
) {

    constructor(user: User, shifts: List<Shift>? = null) : this() {
        this.documentId = user.id
        this.email = user.email
        this.firstName = user.firstName
        this.lastName = user.lastName
        this.shifts = shifts?.map { FirestoreShift(it) }
    }

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