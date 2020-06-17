package studio.codable.unpause.utilities.broadcastReceiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import studio.codable.unpause.utilities.geofencing.GeofenceProcessingService
import studio.codable.unpause.utilities.manager.GeofencingManager
import timber.log.Timber

class UnpauseBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {

        if (intent?.action == GeofencingManager.GEOFENCING_ACTION) {
            Timber.d( "Geofence triggered")
            val i = Intent(context, GeofenceProcessingService::class.java)
            i.putExtras(intent)
            startService(context, i)
            Timber.d( "Started GeofenceProcessingService")
        }

    }

    private fun startService(context: Context?, intent: Intent) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context?.startForegroundService(intent)
        } else {
            context?.startService(intent)
        }
    }
}
