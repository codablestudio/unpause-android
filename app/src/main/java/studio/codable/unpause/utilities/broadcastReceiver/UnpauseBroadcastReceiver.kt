package studio.codable.unpause.utilities.broadcastReceiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.RemoteInput
import studio.codable.unpause.utilities.Constants
import studio.codable.unpause.utilities.Constants.Actions.ACTION_CHECK_IN
import studio.codable.unpause.utilities.Constants.Actions.ACTION_CHECK_OUT
import studio.codable.unpause.utilities.Constants.Actions.GEOFENCING_ACTION
import studio.codable.unpause.utilities.geofencing.CheckInCheckOutService
import studio.codable.unpause.utilities.geofencing.GeofenceProcessingService
import timber.log.Timber

class UnpauseBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {

        if (intent?.action == GEOFENCING_ACTION) {
            Timber.d( "Geofence triggered")
            val i = Intent(context, GeofenceProcessingService::class.java).apply {
                putExtras(intent)
            }
            startService(context, i)
            Timber.d( "Started GeofenceProcessingService")
        } else if (intent?.action == ACTION_CHECK_IN || intent?.action == ACTION_CHECK_OUT) {
            val i = Intent(context, CheckInCheckOutService::class.java).apply {
                action = intent.action
                putExtra(Constants.Notifications.KEY_DESCRIPTION, getDescription(intent))
            }
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
