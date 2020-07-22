package studio.codable.unpause.model

import com.google.android.gms.maps.model.LatLng
import studio.codable.unpause.utilities.Constants.Geofencing.DEFAULT_RADIUS
import studio.codable.unpause.utilities.geofencing.GeofenceModel

data class Location(
    var position: LatLng? = null,
    var name: String? = null
) {
    fun toGeofence() : GeofenceModel =
        GeofenceModel(name!!, position!!.latitude, position!!.longitude, DEFAULT_RADIUS)
}