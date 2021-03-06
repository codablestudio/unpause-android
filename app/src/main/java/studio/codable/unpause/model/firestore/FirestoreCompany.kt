package studio.codable.unpause.model.firestore

import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.GeoPoint
import studio.codable.unpause.model.Company
import studio.codable.unpause.utilities.Constants
import studio.codable.unpause.utilities.geofencing.GeofenceModel

data class FirestoreCompany(
    @DocumentId
    var documentId: String = "",
    var email: String = "",
    var name: String = "",
    var passcode: String = "",
    var locations: List<Map<String, Any>>? = null
) {
    fun toCompany(): Company {
        val loc = locations?.map {
            //mapped example:
            //{name=Codable Studio Zagreb,
            //geopoint=GeoPoint { latitude=45.787424, longitude=15.949704 }}, size: 2
            val geopoint = it.values.elementAt(1) as GeoPoint
            LatLng(geopoint.latitude, geopoint.longitude)
        }

        return Company(documentId, email, name, loc)
    }

    fun extractGeofences() : List<GeofenceModel> {
        return locations?.map{
            val name = it.values.elementAt(0) as String
            val geopoint = it.values.elementAt(1) as GeoPoint
            GeofenceModel(name, geopoint.latitude, geopoint.longitude, Constants.Geofencing.DEFAULT_RADIUS)
        } ?: arrayListOf()
    }

}