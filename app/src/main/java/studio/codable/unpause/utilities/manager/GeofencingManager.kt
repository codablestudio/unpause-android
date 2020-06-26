package studio.codable.unpause.utilities.manager

import android.Manifest
import android.Manifest.*
import android.app.Activity
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat.requestPermissions
import androidx.core.content.ContextCompat
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices
import com.google.gson.Gson
import studio.codable.unpause.utilities.broadcastReceiver.UnpauseBroadcastReceiver
import timber.log.Timber
import java.io.Serializable

class GeofencingManager private constructor(context: Context) {

    private val geofencingClient: GeofencingClient = LocationServices.getGeofencingClient(context)
    private val geofenceList: List<Geofence> = arrayListOf()
    private val geofencePendingIntent: PendingIntent by lazy {
        val intent = Intent(context, UnpauseBroadcastReceiver::class.java).apply {
            action = GEOFENCING_ACTION
        }
        PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    private val preferences: SharedPreferences by lazy {
        context.getSharedPreferences("Geofences", Context.MODE_PRIVATE)
    }

    private var savedGeofences: Set<GeofenceModel>
        get() {
            val set = preferences.getStringSet(GEOFENCES_STRING, null)
            val retVal = HashSet<GeofenceModel>()
            set?.forEach {
                retVal.add(Gson().fromJson(it, GeofenceModel::class.java))
            }
            return retVal
        }
        set(value) {
            val set = HashSet<String>()
            for (geofence in value) {
                set.add(Gson().toJson(geofence))
            }
            preferences.edit().putStringSet(GEOFENCES_STRING, set).apply()
        }

    companion object {
        @Volatile
        private var instance: GeofencingManager? = null

        fun getInstance(context: Context) = instance ?: synchronized(this) {
            instance ?: GeofencingManager(context).also { instance = it }
        }

        const val GEOFENCING_ACTION = "geofence broadcast"
        private const val GEOFENCES_STRING = "geofences"
    }

    /**
     * needs to be called before enabling geofences
     */
    fun addGeofence(geofence: GeofenceModel, enableImmediately: Boolean) {
        (geofenceList as ArrayList).add(
            Geofence.Builder()
                .setRequestId(geofence.requestId)
                .setCircularRegion(geofence.lat, geofence.long, geofence.radius)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER or Geofence.GEOFENCE_TRANSITION_EXIT)
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .build()
        )
        if (enableImmediately) {
            enableGeofences()
        }
    }

    /**
     * needs to be called after adding geofences with addGeofence()
     */
    fun enableGeofences() {
        geofencingClient.addGeofences(getGeofencingRequest(), geofencePendingIntent)?.run {
            addOnSuccessListener {
                Timber.d( "Enabled geofences")
            }

            addOnFailureListener {
                Timber.d( "Failed to enable geofences: ${it.localizedMessage}")
            }
        }
    }

    /**
     * doesn't touch the list of geofences that were provided before
     */
    fun disableAllGeofences() {
        geofencingClient.removeGeofences(geofencePendingIntent)
    }

    /**
     * removes the geofence from the list
     */
    fun removeGeofences(geofenceRequestIds: List<String>) {
        geofencingClient.removeGeofences(geofenceRequestIds)

        (geofenceList as ArrayList).forEach {
            if (geofenceRequestIds.contains(it.requestId)) {
                geofenceList.remove(it)
            }
        }

        savedGeofences.forEach {
            if (geofenceRequestIds.contains(it.requestId)) {
                (savedGeofences as HashSet).remove(it)
            }
        }

        saveGeofencesToPreferences(savedGeofences.toList())
    }

    /**
     * remove all saved geofences from memory
     */
    fun deleteAllGeofences() {
        geofencingClient.removeGeofences(geofencePendingIntent)
        saveGeofencesToPreferences(arrayListOf())
        (geofenceList as ArrayList).clear()
    }

    private fun getGeofencingRequest(): GeofencingRequest {
        return GeofencingRequest.Builder()
            .addGeofences(geofenceList)
            .setInitialTrigger(0) // no initial trigger
            .build()
    }

    /**
     * used to restore last geofences from SharedPreferences
     */
    fun reloadSavedGeofences(enableImmediately: Boolean): Boolean {
        (geofenceList as ArrayList).clear()
        val saved = getGeofencesFromPreferences()
        return if (saved.isNotEmpty()) {
            saved.forEach {
                addGeofence(it, false)
            }
            if (enableImmediately) {
                enableGeofences()
            }
            true
        } else {
            false
        }
    }

    private fun getGeofencesFromPreferences(): List<GeofenceModel> {
        return savedGeofences.toList()
    }

    fun saveGeofencesToPreferences(geofences: List<GeofenceModel>) {
        savedGeofences = geofences.toSet()
    }

    /**
     * @param requestId unique string representing a geofence (e.g. name)
     */
    data class GeofenceModel(
        val requestId: String,
        val lat: Double,
        val long: Double,
        val radius: Float
    ) : Serializable
}