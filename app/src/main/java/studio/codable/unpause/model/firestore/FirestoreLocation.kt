package studio.codable.unpause.model.firestore

import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.GeoPoint
import studio.codable.unpause.model.Location
import java.util.HashMap

data class FirestoreLocation(
@DocumentId
var documentId: String = "",
var geopoint: GeoPoint? = null,
var name: String = ""
) {
    companion object {
        const val GEOPOINT = "geopoint"
        const val NAME = "name"

    }

    constructor(location: Location) : this() {
        this.geopoint = GeoPoint(location.position!!.latitude, location.position!!.longitude)
        this.name = location.name!!
    }

    fun toLocation() : Location {
        return Location(LatLng(geopoint!!.latitude, geopoint!!.longitude), name)
    }

    fun asHashMap(): HashMap<String, Any?> {
        return hashMapOf(
            GEOPOINT to geopoint,
            NAME to name
        )
    }
}