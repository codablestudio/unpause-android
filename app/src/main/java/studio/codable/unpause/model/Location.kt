package studio.codable.unpause.model

import com.google.android.gms.maps.model.LatLng

data class Location(
    var position: LatLng? = null,
    var name: String? = null
)