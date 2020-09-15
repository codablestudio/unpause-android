package studio.codable.unpause.screens.fragment.locations

import androidx.lifecycle.Observer
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import studio.codable.unpause.model.Location

class DisplayCompanyLocationsFragment : BaseMapFragment() {

    override fun createMarkersFromLocations(locations: List<Location>): List<Marker> {
        return locations.map {
            googleMap.addMarker(MarkerOptions().position(it.position).title(it.name).draggable(false))
        }
    }

    override fun onMapReady(map: GoogleMap?) {
        googleMap = map ?: return
        googleMap.uiSettings.isZoomControlsEnabled = true

        userVm.company.value?.let {
            markers = createMarkersFromLocations(it.extractLocations()) as MutableList<Marker>
            markers[0]?.let {marker ->
                setStartLocation( marker.position.latitude, marker.position.longitude)
            }
        }

        initObservers()
        userVm.user.value?.let {
            if (it.companyId != null)
                userVm.getCompany(it.companyId)
        }

    }

    private fun initObservers() {
        userVm.company.observe(viewLifecycleOwner, Observer {
            googleMap.clear()
            markers = createMarkersFromLocations(it.extractLocations()) as MutableList<Marker>
        })
    }


}