package studio.codable.unpause.model.firestore

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.GeoPoint
import studio.codable.unpause.model.Company

data class FirestoreCompany(
    @DocumentId
    var documentId: String = "",
    var email: String = "",
    var name: String = "",
    var passcode: String = "",
    var locations: List<GeoPoint>? = null
) {
    fun toCompany(): Company {
        return Company(documentId, email, name, passcode, locations)
    }
}