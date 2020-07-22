package studio.codable.unpause.utilities.extensions

import com.google.android.gms.maps.model.Marker
import studio.codable.unpause.model.Location

fun Marker.toLocation() : Location {
    return Location(position, title)
}