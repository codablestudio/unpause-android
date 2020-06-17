package studio.codable.unpause.utilities.manager

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import studio.codable.unpause.R
import studio.codable.unpause.screens.activity.start.StartActivity
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

    fun sendNotification(title: String, content: String) {

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

        NotificationManagerCompat.from(context)
            .notify(System.currentTimeMillis().toInt(), builder.build())

        Timber.d("Notification should be delivered")
    }
}