package studio.codable.unpause.utilities.geofencing

import android.app.IntentService
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofenceStatusCodes
import com.google.android.gms.location.GeofencingEvent
import studio.codable.unpause.R
import studio.codable.unpause.screens.activity.start.StartActivity
import studio.codable.unpause.utilities.Constants.NOTIFICATION_CHANNEL_ID
import studio.codable.unpause.utilities.manager.NotificationManagerUnpause
import timber.log.Timber

class GeofenceProcessingService : IntentService("Geofence processing service") {

    companion object {
        private const val NOTIF_ID = 23456
    }

    private val notificationManager: NotificationManagerUnpause by lazy {
        NotificationManagerUnpause.getInstance(
            applicationContext,
            NOTIFICATION_CHANNEL_ID
        )
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Timber.i("GeofenceProcessingService started")
        startForeground()
        return super.onStartCommand(intent, flags, startId)
    }

    private fun startForeground() {
        val notificationIntent = Intent(this, StartActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this, 0,
            notificationIntent, 0
        )

        val notification = NotificationCompat.Builder(
                this,
                NOTIFICATION_CHANNEL_ID
            )
            .setOngoing(true)
            .setSmallIcon(R.drawable.ic_app_icon)
            .setContentTitle(getString(R.string.app_name))
            .setContentText(getString(R.string.im_watching_you))
            .setContentIntent(pendingIntent)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForeground(NOTIF_ID, notification.build())
        } else {
            notification.priority = NotificationCompat.PRIORITY_MIN
            startForeground(NOTIF_ID, notification.build())
        }
    }

    override fun onHandleIntent(intent: Intent?) {
        val geofencingEvent = GeofencingEvent.fromIntent(intent)
        if (geofencingEvent.hasError()) {
            val errorMessage =
                    GeofenceStatusCodes.getStatusCodeString(GeofenceStatusCodes.ERROR)
            Timber.e(errorMessage)
            return
        }

        val geofenceTransition = geofencingEvent.geofenceTransition

        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER ||
            geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {

            val triggeringGeofences = geofencingEvent.triggeringGeofences

            val sb = StringBuilder()
            var title = ""
            if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER) {
                triggeringGeofences.forEach {
                    sb.append(it.requestId + ", ")
                }
                sb.deleteCharAt(sb.indexOf(","))
                title =
                        applicationContext.getString(R.string.arrived_at_location, sb.toString().trim())
                notificationManager.sendCheckInNotification(title,
                        applicationContext.getString(R.string.your_location_has_changed))

            } else if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {
                triggeringGeofences.forEach {
                    sb.append(it.requestId + ", ")
                }
                sb.deleteCharAt(sb.indexOf(","))
                title = applicationContext.getString(R.string.left_location, sb.toString().trim())
                notificationManager.sendCheckOutNotification(title,
                        applicationContext.getString(R.string.your_location_has_changed))
            }
            Timber.d(triggeringGeofences.toString())
        } else {
            Timber.e("Geofence detection error")
        }
    }
}