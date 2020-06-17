package studio.codable.unpause.model.firestore

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.GeoPoint
import studio.codable.unpause.model.Company
import studio.codable.unpause.utilities.latlng.LatLng

data class FirestoreCompany(
    @DocumentId
    var documentId: String = "",
    var email: String = "",
    var name: String = "",
    var passcode: String = "",
    var locations: List<GeoPoint>? = null
) {
    fun toCompany(): Company {
        val loc = locations?.map {
            LatLng(it.latitude,it.longitude)
        }
        return Company(documentId, email, name, loc)
    }
}