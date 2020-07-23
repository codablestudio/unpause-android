package studio.codable.unpause.model

import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import studio.codable.unpause.utilities.Constants.Geofencing.DEFAULT_RADIUS
import studio.codable.unpause.utilities.geofencing.GeofenceModel

data class Location(
    var position: LatLng,
    var name: String? = null
) {
    constructor(marker: Marker) : this(marker.position, marker.title)
}