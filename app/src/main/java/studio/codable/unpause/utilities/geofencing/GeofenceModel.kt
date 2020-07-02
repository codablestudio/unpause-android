package studio.codable.unpause.utilities.geofencing

import java.io.Serializable

/**
 * @param requestId unique string representing a geofence (e.g. name)
 */
data class GeofenceModel(
    val requestId: String,
    val lat: Double,
    val long: Double,
    val radius: Float
) : Serializable