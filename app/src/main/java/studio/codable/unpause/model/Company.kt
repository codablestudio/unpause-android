package studio.codable.unpause.model

import studio.codable.unpause.utilities.latlng.LatLng


data class Company(
    var id: String,
    var email: String,
    var name: String,
    var locations: List<LatLng>? = null
)