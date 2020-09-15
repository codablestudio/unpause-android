package studio.codable.unpause.model

import com.google.android.gms.maps.model.LatLng


data class Company(
    var id: String,
    var email: String,
    var name: String,
    var locations: List<LatLng>? = null
) {
    fun extractLocations() : List<Location> {
        return locations?.map {
            Location(it, name)
        } ?: arrayListOf()
    }
}