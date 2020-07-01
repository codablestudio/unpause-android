package studio.codable.unpause.utilities.broadcastReceiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.RemoteInput
import studio.codable.unpause.utilities.Constants
import studio.codable.unpause.utilities.geofencing.CheckInCheckOutService
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

        if (intent?.action == Constants.Actions.ACTION_CHECK_IN || intent?.action == Constants.Actions.ACTION_CHECK_OUT) {
            Timber.i(getDescription(intent))
            val i = Intent(context, CheckInCheckOutService::class.java)
            i.action = intent.action
            i.putExtra(Constants.Notifications.KEY_DESCRIPTION, getDescription(intent))
            startService(context, i)
            Timber.d( "Started CheckInCheckOutService")
        }

    }

    private fun startService(context: Context?, intent: Intent) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context?.startForegroundService(intent)
        } else {
            context?.startService(intent)
        }
    }

    private fun getDescription(intent: Intent): String? {
        return RemoteInput.getResultsFromIntent(intent)?.getString(Constants.Notifications.KEY_DESCRIPTION)
    }
}
