package studio.codable.unpause.utilities.geofencing

import android.app.IntentService
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofenceStatusCodes
import com.google.android.gms.location.GeofencingEvent
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.*
import studio.codable.unpause.R
import studio.codable.unpause.repository.FirebaseShiftRepository
import studio.codable.unpause.screens.activity.start.StartActivity
import studio.codable.unpause.utilities.Constants.Notifications.LOCATION_NOTIFICATION_CHANNEL_ID
import studio.codable.unpause.utilities.LambdaNoArgumentsUnit
import studio.codable.unpause.utilities.LambdaShiftToBool
import studio.codable.unpause.utilities.manager.NotificationManagerUnpause
import studio.codable.unpause.utilities.manager.SessionManager
import studio.codable.unpause.utilities.networking.Result
import timber.log.Timber
import kotlin.coroutines.CoroutineContext

class GeofenceProcessingService : IntentService("Geofence processing service"), CoroutineScope {

    companion object {
        private const val NOTIF_ID = 23456

        private val userIsCheckedIn : LambdaShiftToBool = { it!=null }
        private val userIsNotCheckedIn : LambdaShiftToBool = { it==null }
    }

    private val errorHandler = CoroutineExceptionHandler { _, throwable -> Timber.w(throwable) }
    private val job = Job()
    override val coroutineContext: CoroutineContext = Dispatchers.IO + job + errorHandler

    private val sessionManager by lazy { SessionManager(this.applicationContext) }
    private val shiftRepository by lazy { FirebaseShiftRepository(
        FirebaseFirestore.getInstance(),
        sessionManager) }
    private val notificationManager: NotificationManagerUnpause by lazy {
        NotificationManagerUnpause.getInstance(
            applicationContext,
            LOCATION_NOTIFICATION_CHANNEL_ID
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
            this, 122,
            notificationIntent, 0
        )

        val notification = NotificationCompat.Builder(
                this,
                LOCATION_NOTIFICATION_CHANNEL_ID
            )
            .setOngoing(true)
            .setSmallIcon(R.drawable.ic_app_icon)
            .setContentTitle(getString(R.string.app_name))
            .setContentText(getString(R.string.im_watching_you))
            .setContentIntent(pendingIntent)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)

        Timber.i("StartForeground")
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

                handleNotificationDisplay(userIsNotCheckedIn, {
                    notificationManager.sendCheckInNotification(
                        applicationContext.getString(R.string.arrived_at_location, sb.toString().trim()),
                        applicationContext.getString(R.string.your_location_has_changed))
                },{
                    //no action
                })

                Timber.i("You entered geofence perimeter!")

            } else if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {
                triggeringGeofences.forEach {
                    sb.append(it.requestId + ", ")
                }
                sb.deleteCharAt(sb.indexOf(","))

                handleNotificationDisplay(userIsCheckedIn, {
                    notificationManager.sendCheckOutNotification(
                        applicationContext.getString(R.string.left_location, sb.toString().trim()),
                        applicationContext.getString(R.string.your_location_has_changed))
                },{
                    //no action
                })
                Timber.i("You exited geofence perimeter!")
            }
            Timber.d(triggeringGeofences.toString())
        } else {
            Timber.e("Geofence detection error")
        }
    }

    private fun handleNotificationDisplay(
        condition: LambdaShiftToBool,
        onConditionTrueAction: LambdaNoArgumentsUnit,
        onConditionFalseAction: LambdaNoArgumentsUnit
    ) {
        launch {
            process(shiftRepository.getCurrent()) { shift ->
                if (condition(shift)) onConditionTrueAction() else onConditionFalseAction()
            }
        }
    }

    private inline fun <T> process(result: Result<T>, onSuccess: (value: T) -> Unit) {
        when (result) {
            is Result.Success -> {
                Timber.i("Network call successful: ${result.value}")
                onSuccess(result.value)
            }
            is Result.GenericError -> {
                Timber.e("Network call failed: $result")
            }
            is Result.IOError -> {
                Timber.e("Network call failed: network error")
            }
        }
    }
}