package studio.codable.unpause.utilities.manager

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import studio.codable.unpause.R
import studio.codable.unpause.screens.activity.start.StartActivity
import studio.codable.unpause.utilities.Constants
import studio.codable.unpause.utilities.broadcastReceiver.UnpauseBroadcastReceiver
import timber.log.Timber

class NotificationManagerUnpause private constructor(
    private val context: Context,
    private val notificationChannelId: String
) {
    companion object {
        @Volatile
        private var instance: NotificationManagerUnpause? = null

        fun getInstance(context: Context, notificationChannelId: String) =
            instance ?: synchronized(this) {
                instance ?: NotificationManagerUnpause(
                    context,
                    notificationChannelId
                ).also { instance = it }
            }
    }

    private val checkInIntent: PendingIntent by lazy {
        val intent = Intent(context, UnpauseBroadcastReceiver::class.java).apply {
            action = Constants.Actions.ACTION_CHECK_IN
        }
        PendingIntent.getBroadcast(context, 21, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    private val checkOutIntent: PendingIntent by lazy {
        val intent = Intent(context, UnpauseBroadcastReceiver::class.java).apply {
            action = Constants.Actions.ACTION_CHECK_OUT
        }
        PendingIntent.getBroadcast(context, 20, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    fun sendNotification(title: String, content: String, action: PendingIntent?, buttonTitle: String) {

        val notificationIntent = Intent(context, StartActivity::class.java)

        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)

        val intent = PendingIntent.getActivity(
            context, 0,
            notificationIntent, 0
        )

        val builder =
            NotificationCompat.Builder(
                    context,
                    notificationChannelId // todo add channel for location based notifications
                )
                .setSmallIcon(R.drawable.ic_app_icon)
                .setContentTitle(title)
                .setContentText(content)
                .setContentIntent(intent)
                .setAutoCancel(true)
                .setStyle(NotificationCompat.BigTextStyle().bigText(content))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .addAction(R.drawable.ic_app_icon, buttonTitle,
                            action)


        NotificationManagerCompat.from(context)
            .notify(System.currentTimeMillis().toInt(), builder.build())

        Timber.d("Notification should be delivered")
    }

    fun sendCheckInNotification(title: String, content: String) {
        sendNotification(title, content, checkInIntent, "CHECK IN")
    }

    fun sendCheckOutNotification(title: String, content: String) {
        sendNotification(title, content, checkOutIntent, "CHECK OUT")
    }
}