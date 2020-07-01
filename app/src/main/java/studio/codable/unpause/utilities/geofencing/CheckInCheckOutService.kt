package studio.codable.unpause.utilities.geofencing

import android.app.IntentService
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.*
import studio.codable.unpause.R
import studio.codable.unpause.model.Shift
import studio.codable.unpause.repository.FirebaseShiftRepository
import studio.codable.unpause.screens.activity.start.StartActivity
import studio.codable.unpause.utilities.Constants
import studio.codable.unpause.utilities.Constants.Notifications.NOTIFICATION_CHANNEL_ID
import studio.codable.unpause.utilities.manager.NotificationManagerUnpause
import studio.codable.unpause.utilities.manager.SessionManager
import studio.codable.unpause.utilities.networking.Result
import timber.log.Timber
import java.util.*
import kotlin.coroutines.CoroutineContext


class CheckInCheckOutService : IntentService("CheckInCheckOutService"), CoroutineScope {

    private val sessionManager by lazy { SessionManager(this.applicationContext) }
    private val shiftRepository by lazy { FirebaseShiftRepository(
            FirebaseFirestore.getInstance(),
            sessionManager) }
    private val notificationManager by lazy {
        NotificationManagerUnpause.getInstance(
            this,
            NOTIFICATION_CHANNEL_ID
        )
    }

    private val errorHandler = CoroutineExceptionHandler { _, throwable -> Timber.w(throwable) }
    private val job = Job()
    override val coroutineContext: CoroutineContext = Dispatchers.IO + job + errorHandler

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Timber.i("CheckInCheckOutService started")
        startForeground()
        return super.onStartCommand(intent, flags, startId)
    }

    private fun startForeground() {
        val notificationIntent = Intent(this, StartActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this, 123,
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
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForeground(2121, notification.build())
        } else {
            notification.priority = NotificationCompat.PRIORITY_MIN
            startForeground(2121, notification.build())
        }
    }


    @ExperimentalStdlibApi
    override fun onHandleIntent(intent: Intent?) {
        if (intent?.action == Constants.Actions.ACTION_CHECK_IN) {
            checkIn()
        } else if (intent?.action == Constants.Actions.ACTION_CHECK_OUT) {
            checkOut(getDescription(intent))
        }
    }

    @ExperimentalStdlibApi
    private fun checkIn() {
        launch {
            process(shiftRepository.getCurrent()) { shift ->
                if (shift == null) {
                    val newShift = Shift(arrivalTime = Date())
                    Timber.d("New shift added")
                    launch {
                        process(shiftRepository.addNew(newShift)) {
                            notificationManager.cancelCheckInNotification()
                        }
                    }
                } else {
                    Toast.makeText(applicationContext, "You are already checked in.", Toast.LENGTH_SHORT).show()
                    Timber.i("You are already checked in.")
                }
            }
        }
    }

    @ExperimentalStdlibApi
    private fun checkOut(description: String?) {
        launch {
            process(shiftRepository.getCurrent()) { shift ->
                if (shift != null) {
                    val updatedShift = shift.copy()
                        .addExit(Date(), description ?: "description")
                    Timber.d("Shift updated")
                    launch {
                        process(shiftRepository.update(updatedShift)) {
                            // for remoteInput notifications you have update
                            // the current notification to let the user know everything is ok
                            notificationManager.updateCheckOutNotification()
                        }
                    }
                } else {
                    Toast.makeText(
                        applicationContext, "You need to check in before you check out.",
                        Toast.LENGTH_SHORT
                    ).show()
                    Timber.i("You need to check in before you check out.")
                }
            }
        }
    }

    private inline fun <T> process(result: Result<T>, onSuccess: (value: T) -> Unit) {
        when (result) {
            is Result.Success -> {
                Timber.tag(this::class.java.simpleName)
                        .i("Network call successful: ${result.value}")
                onSuccess(result.value)
            }
            is Result.GenericError -> {
                Timber.tag(this::class.java.simpleName).e("Network call failed: $result")
                Toast.makeText(this.applicationContext, result.errorResponse.toString(), Toast.LENGTH_SHORT).show()
            }
            is Result.IOError -> {
                Timber.tag(this::class.java.simpleName).e("Network call failed: network error")
                Toast.makeText(this.applicationContext, "Network error", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getDescription(intent: Intent): String? {
        return intent.getStringExtra(Constants.Notifications.KEY_DESCRIPTION)
    }


}