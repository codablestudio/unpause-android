package studio.codable.unpause.screens.fragment.locations

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.fragment_map.*
import studio.codable.unpause.R
import studio.codable.unpause.base.activity.BaseActivity
import studio.codable.unpause.base.fragment.BaseFragment
import studio.codable.unpause.model.Location
import studio.codable.unpause.utilities.manager.DialogManager
import studio.codable.unpause.utilities.manager.PermissionManager
import javax.inject.Inject

class MapFragment : BaseMapFragment(), GoogleMap.OnMarkerDragListener {

    @Inject
    lateinit var permissionManager : PermissionManager

    private val dialogManager: DialogManager by lazy { DialogManager(activity as BaseActivity) }

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fusedLocationProviderClient = FusedLocationProviderClient(requireActivity())
    }

    override fun onMapReady(map: GoogleMap?) {
        googleMap = map ?: return
        if (permissionManager.checkFineLocationPermission()) {
            googleMap.isMyLocationEnabled = true
            googleMap.uiSettings.isMyLocationButtonEnabled = true
            googleMap.uiSettings.isZoomControlsEnabled = true
            getCurrentLocation()
            userVm.getLocations()
            defineMapActions()
        } else {
            permissionManager.requestFineLocationPermission(layout_map_fragment as BaseFragment)
        }
        googleMap.setOnMarkerDragListener(this)

        initObservers()
    }

    private fun defineMapActions() {
        defineAddLocation()
        defineEditLocation()
        defineDeleteLocation()
    }

    private fun defineDeleteLocation() {
        googleMap.setOnInfoWindowLongClickListener {
            dialogManager.openConfirmDialog(R.string.are_you_sure_you_want_to_delete_location) {
                userVm.deleteLocation(Location(it))
            }
        }
    }

    private fun defineEditLocation() {
        googleMap.setOnInfoWindowClickListener { marker ->
            dialogManager.openDescriptionDialog(
                getString(R.string.edit_location_name),
                marker.title,
                false,
                {
                    updateLocation(Location(marker), Location(marker.position, it))
                },
                {})
        }
    }

    private fun defineAddLocation() {
        googleMap.setOnMapClickListener { position ->
            dialogManager.openDescriptionDialog(
                R.string.enter_location_name,
                null,
                false,
                {
                    userVm.addLocation(Location(position, it))
                },
                {})
        }
    }

    private fun initObservers() {
        userVm.locations.observe(viewLifecycleOwner, Observer {
            //refresh markers
            googleMap.clear()
            markers = createMarkersFromLocations(it) as MutableList<Marker>
        })
    }

    /**
     * Sends request to find out user location
     */
    private fun getCurrentLocation() {
        val locationRequest = LocationRequest()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = (10 * 1000).toLong()
        locationRequest.fastestInterval = 2000
        val locationSettingsRequest =
            LocationSettingsRequest.Builder().addLocationRequest(locationRequest).build()
        val result =
            LocationServices.getSettingsClient(requireActivity()).checkLocationSettings(locationSettingsRequest)
        result.addOnCompleteListener { task ->
            try {
                val response = task.getResult(ApiException::class.java)
                if (response!!.locationSettingsStates.isLocationPresent) {
                    getLastLocation()
                }
            } catch (exception: ApiException) {
                showMessage("Error: " + exception.localizedMessage)
            }
        }
    }

    /**
     * Retrieves last known user location
     */
    private fun getLastLocation() {
        fusedLocationProviderClient.lastLocation
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful && task.result != null) {
                    val mLastLocation = task.result
                    setStartLocation(mLastLocation!!.latitude, mLastLocation.longitude)
                } else {
                    showMessage("No current location found")
                }
            }
    }

    override fun onMarkerDragEnd(p0: Marker?) {
        //after marker is dragged, update location
        p0?.let {marker ->
            //find old location
            val oldLocation = markers.find {
                it.title == marker.title
            }
            updateLocation(Location(oldLocation!!), Location(p0))
        }
    }

    override fun onMarkerDragStart(p0: Marker?) {
    }

    override fun onMarkerDrag(p0: Marker?) {
    }

    override fun createMarkersFromLocations(locations: List<Location>): List<Marker> {
        return locations.map {
            googleMap.addMarker(MarkerOptions().position(it.position).title(it.name).draggable(true))
        }
    }

    private fun updateLocation(old: Location, new: Location) {
        userVm.deleteLocation(old)
        userVm.addLocation(new)
    }
}