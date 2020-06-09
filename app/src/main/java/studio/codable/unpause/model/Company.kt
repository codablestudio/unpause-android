package studio.codable.unpause.model

import com.google.firebase.firestore.GeoPoint

data class Company(
    var id: String,
    var email: String,
    var name: String,
    var passcode: String,
    var locations: List<GeoPoint>? = null
)