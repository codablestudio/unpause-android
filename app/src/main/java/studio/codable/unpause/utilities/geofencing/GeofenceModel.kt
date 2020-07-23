package studio.codable.unpause.utilities.geofencing

import studio.codable.unpause.model.Location
import studio.codable.unpause.utilities.Constants.Geofencing.DEFAULT_RADIUS
import java.io.Serializable

/**
 * @param requestId unique string representing a geofence (e.g. name)
 */
data class GeofenceModel(
    val requestId: String,
    val lat: Double,
    val long: Double,
    val radius: Float
) : Serializable {
    constructor(location: Location) : this(
        location.name!!,
        location.position.latitude,
        location.position.longitude,
        DEFAULT_RADIUS
    )
}