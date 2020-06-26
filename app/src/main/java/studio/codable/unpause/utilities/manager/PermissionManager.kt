package studio.codable.unpause.utilities.manager

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat
import studio.codable.unpause.base.fragment.BaseFragment
import studio.codable.unpause.utilities.Constants
import timber.log.Timber
import javax.inject.Inject

class PermissionManager @Inject constructor(private val context: Context) {
    fun checkFineLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }
    fun requestFineLocationPermission(fragment: BaseFragment) {
        fragment.requestPermissions(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                Constants.RequestCode.FINE_LOCATION_PERMISSION
        )
        Timber.d("Requested ACCESS_FINE_LOCATION permission")
    }

    fun requestBackgroundLocationPermission(fragment: BaseFragment) {
        fragment.requestPermissions(
                arrayOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION),
                Constants.RequestCode.FINE_LOCATION_PERMISSION
        )
        Timber.d("Requested ACCESS_FINE_LOCATION permission")
    }

    fun requestLocationPermission(fragment: BaseFragment) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            requestBackgroundLocationPermission(fragment)
        } else {
            requestFineLocationPermission(fragment)
        }
    }

    fun shouldShowFineLocationPermissionExplanation(fragment: BaseFragment): Boolean {
        val res = fragment.shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)
        if (res) {
            Timber.d("ACCESS_FINE_LOCATION permission hasn't been denied")
        } else {
            Timber.d("ACCESS_FINE_LOCATION permission has been previously denied")
        }
        return res
    }
}