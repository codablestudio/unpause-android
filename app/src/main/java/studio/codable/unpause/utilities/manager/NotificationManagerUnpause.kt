package studio.codable.unpause.utilities.manager

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.RemoteInput
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

        fun createNotificationChannel(context: Context, notificationChannelId: String) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                // Create the NotificationChannel
                val name = context.getString(R.string.channel_name)
                val descriptionText = context.getString(R.string.channel_description)
                val importance = NotificationManager.IMPORTANCE_DEFAULT
                val mChannel = NotificationChannel(notificationChannelId, name, importance)
                    .apply {
                    description = descriptionText
                }
                // Register the channel with the system; you can't change the importance
                // or other notification behaviors after this
                val notificationManager = context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.createNotificationChannel(mChannel)
            }
        }
    }

    private val checkInIntent: NotificationCompat.Action by lazy {
        val intent = Intent(context, UnpauseBroadcastReceiver::class.java).apply {
            action = Constants.Actions.ACTION_CHECK_IN
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            Constants.RequestCode.CHECK_IN,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        NotificationCompat.Action.Builder(
            R.drawable.ic_app_icon,
            context.getString(R.string.check_in), pendingIntent
        )
        .build()
    }


    private val checkOutIntent: NotificationCompat.Action by lazy {
        val replyLabel: String = context.getString(R.string.enter_description)
        val remoteInput: RemoteInput = RemoteInput.Builder(Constants.Notifications.KEY_DESCRIPTION)
            .run {
                setLabel(replyLabel)
                build()
            }
        val intent = Intent(context, UnpauseBroadcastReceiver::class.java).apply {
            action = Constants.Actions.ACTION_CHECK_OUT
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            Constants.RequestCode.CHECK_OUT,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        NotificationCompat.Action.Builder(
            R.drawable.ic_app_icon,
            context.getString(R.string.check_out), pendingIntent
        )
        .addRemoteInput(remoteInput)
        .build()
    }

    private val startActivityIntent: PendingIntent by lazy {
        PendingIntent.getActivity(
            context,
            0,
            Intent(
                context,
                StartActivity::class.java
            ).apply {
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            },
            0
        )
    }

    private fun sendNotification(title: String, content: String, action : NotificationCompat.Action, tag : String) {
        val builder =
            NotificationCompat.Builder(
                    context,
                    notificationChannelId
                )
                .setSmallIcon(R.drawable.ic_app_icon)
                .setContentTitle(title)
                .setContentText(content)
                .setContentIntent(startActivityIntent)
                .setAutoCancel(true)
                .setStyle(NotificationCompat.BigTextStyle().bigText(content))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .addAction(action)


        createNotificationChannel(context, notificationChannelId)
        NotificationManagerCompat.from(context).notify(tag, Constants.Notifications.CHECK_IN_CHECK_OUT_ID, builder.build())

        Timber.d("Notification should be delivered")
    }

    fun sendCheckInNotification(title: String, content: String) {
        sendNotification(title, content, checkInIntent, Constants.Notifications.CHECK_IN_TAG)
    }

    fun sendCheckOutNotification(title: String, content: String) {
        sendNotification(title, content, checkOutIntent, Constants.Notifications.CHECK_OUT_TAG)
    }

    fun updateCheckOutNotification(content: String) {
        // Build a new notification, which informs the user that the system
        // handled their interaction with the previous notification.
        val builder = NotificationCompat.Builder(
            context,
            notificationChannelId
        )
            .setSmallIcon(R.drawable.ic_app_icon)
            .setContentText(content)
            .setContentIntent(startActivityIntent)
            .setAutoCancel(true)

        // Issue the new notification.
        NotificationManagerCompat.from(context).notify(
            Constants.Notifications.CHECK_OUT_TAG,
            Constants.Notifications.CHECK_IN_CHECK_OUT_ID,
            builder.build()
        )
    }

    fun updateCheckInNotification(content: String) {
        // Build a new notification, which informs the user that the system
        // handled their interaction with the previous notification.
        val builder = NotificationCompat.Builder(
            context,
            notificationChannelId
        )
            .setSmallIcon(R.drawable.ic_app_icon)
            .setContentText(content)
            .setContentIntent(startActivityIntent)
            .setAutoCancel(true)

        // Issue the new notification.
        NotificationManagerCompat.from(context).notify(
            Constants.Notifications.CHECK_IN_TAG,
            Constants.Notifications.CHECK_IN_CHECK_OUT_ID,
            builder.build()
        )
    }

    fun cancelCheckInNotification() {
        NotificationManagerCompat.from(context).cancel(
            Constants.Notifications.CHECK_IN_TAG,
            Constants.Notifications.CHECK_IN_CHECK_OUT_ID
        )
    }
}

