package studio.codable.unpause.screens.fragment.locations

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import studio.codable.unpause.R
import studio.codable.unpause.base.fragment.BaseFragment
import studio.codable.unpause.model.Location
import studio.codable.unpause.screens.UserViewModel

abstract class BaseMapFragment : BaseFragment(false), OnMapReadyCallback {

    protected val userVm: UserViewModel by activityViewModels()
    protected lateinit var googleMap: GoogleMap
    protected lateinit var markers: MutableList<Marker>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_map, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mapFragment = childFragmentManager.findFragmentById(R.id.layout_map_fragment) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    /**
     * Sets camera to the location
     */
    protected fun setStartLocation(lat: Double, lng: Double) {
        val cameraPosition = CameraPosition.Builder()
            .target(LatLng(lat, lng))
            .zoom(17f)
            .build()
        googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
    }

    protected abstract fun createMarkersFromLocations(locations: List<Location>) : List<Marker>
}