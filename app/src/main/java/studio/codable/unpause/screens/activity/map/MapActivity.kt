package studio.codable.unpause.screens.activity.map

import android.content.Context
import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.activity_map.*
import studio.codable.unpause.R
import studio.codable.unpause.base.activity.BaseActivity
import studio.codable.unpause.base.fragment.BaseFragment
import studio.codable.unpause.screens.activity.home.HomeActivity
import studio.codable.unpause.utilities.manager.PermissionManager
import java.io.IOException
import java.util.*
import javax.inject.Inject

class MapActivity : BaseActivity(), OnMapReadyCallback, GoogleMap.OnMarkerDragListener {

    override val navController: NavController by lazy { findNavController(R.id.navigation_map_activity) }

    @Inject
    lateinit var permissionManager : PermissionManager

    private lateinit var googleMap: GoogleMap
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var marker: Marker

    companion object {
        fun getIntent(context: Context): Intent {
            return Intent(context, MapActivity::class.java)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.location_chooser) as SupportMapFragment?
        mapFragment!!.getMapAsync(this)
        fusedLocationProviderClient = FusedLocationProviderClient(this)
    }

    override fun onMapReady(map: GoogleMap?) {
        googleMap = map?: return
        if (permissionManager.checkFineLocationPermission()){
            googleMap.isMyLocationEnabled = true
            googleMap.uiSettings.isMyLocationButtonEnabled = true
            googleMap.uiSettings.isZoomControlsEnabled = true
            getCurrentLocation()
            googleMap.setOnMapClickListener {
                map.addMarker(MarkerOptions().position(it).title("Location"))
            }
        } else {
            permissionManager.requestFineLocationPermission(location_chooser as BaseFragment)
        }
        googleMap.setOnMarkerDragListener(this)
    }

    private fun getCurrentLocation() {
        val locationRequest = LocationRequest()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = (10 * 1000).toLong()
        locationRequest.fastestInterval = 2000
        val builder = LocationSettingsRequest.Builder()
        builder.addLocationRequest(locationRequest)
        val locationSettingsRequest = builder.build()
        val result = LocationServices.getSettingsClient(this).checkLocationSettings(locationSettingsRequest)
        result.addOnCompleteListener { task ->
            try {
                val response = task.getResult(ApiException::class.java)
                if (response!!.locationSettingsStates.isLocationPresent){
                    getLastLocation()
                }
            } catch (exception: ApiException) {
                showMessage("Error")
            }
        }
    }

    private fun getLastLocation() {
        fusedLocationProviderClient.lastLocation
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful && task.result != null) {
                    val mLastLocation = task.result
                    setStartLocation(mLastLocation!!.latitude, mLastLocation.longitude, "")
                } else {
                    showMessage("No current location found")
                }
            }
    }
    //Set the user’s starting point and add marker to it.
    private fun setStartLocation(lat: Double, lng: Double, addr: String){
        var address = "Current address"
        if (addr.isEmpty()){
            val gcd = Geocoder(this, Locale.getDefault())
            val addresses: List<Address>
            try {
                addresses = gcd.getFromLocation(lat, lng, 1)
                if (addresses.isNotEmpty()) {
                    address = addresses[0].getAddressLine(0)
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        } else {
            address = addr
        }
        marker = googleMap.addMarker(
            MarkerOptions()
                .position(LatLng(lat, lng))
                .title("Selected Location")
                .snippet(address)
                .draggable(true)
        )
        val cameraPosition = CameraPosition.Builder()
            .target(LatLng(lat, lng))
            .zoom(17f)
            .build()
        googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
    }

    override fun onMarkerDragEnd(p0: Marker?) {
        p0?.position?.let {

            val geocoder = Geocoder(this, Locale.getDefault())
            try {
                val address =
                    geocoder.getFromLocation(it.latitude, it.longitude, 1)[0]
                showMessage(address.toString())
            } catch (e: IOException) {
                e.printStackTrace()
            }

        }
    }

    override fun onMarkerDragStart(p0: Marker?) {
    }

    override fun onMarkerDrag(p0: Marker?) {
    }


}