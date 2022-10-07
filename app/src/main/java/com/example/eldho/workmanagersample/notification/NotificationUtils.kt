package com.example.eldho.workmanagersample.notification

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import com.example.eldho.workmanagersample.MainActivity
import com.example.eldho.workmanagersample.R
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class NotificationUtils @Inject constructor(@ApplicationContext private val context: Context) {

    companion object {
        private const val TAG = "NotificationUtils"

        //Notification Channels
        private const val FCM_NOTIFICATION_CHANNEL_ID = "reminder_notification_channel"
        private const val NOTIFICATION_ID = 1234
        private const val PENDING_INTENT_ID = 3417
        private const val CHANNEL_NAME = "eldhopj"
    }

    //Allows to relaunch the app when we click the notification
    private fun contentIntent(context: Context): PendingIntent {
        val startActivityIntent = Intent(context, MainActivity::class.java)
        return PendingIntent.getActivity(
            context,
            PENDING_INTENT_ID,
            startActivityIntent,  //FLAG_UPDATE_CURRENT -> keeps this instance valid and just updates the extra data associated with it coming from new intent
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    //This method is responsible for creating the notification and notification channel in which the notification belongs to and displaying it
    fun createNotifications(title: String?, body: String?) {
        /**From Oreo we need to display notifications in the notification channel */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val mChannel = NotificationChannel(
                FCM_NOTIFICATION_CHANNEL_ID,  //String ID
                CHANNEL_NAME,  //Name for the channel
                NotificationManager.IMPORTANCE_HIGH
            ) //Importance for the notification , In high we get headsUp notification
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(mChannel)
        }

        //Notification Builder
        val notificationBuilder = NotificationCompat.Builder(context, FCM_NOTIFICATION_CHANNEL_ID)
            .setColor(ContextCompat.getColor(context, R.color.colorPrimary))
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setLargeIcon(
                ContextCompat.getDrawable(context, R.drawable.ic_launcher_foreground)?.toBitmap()
            )
            .setContentTitle(title) // Title
            // check different styles ref: https://developer.android.com/training/notify-user/expanded
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .setDefaults(Notification.DEFAULT_VIBRATE) // needed to add vibration permission on the manifest
            .setContentIntent(contentIntent(context)) //pending Intent (check its fn)
            .setAutoCancel(true) //Notification will go away when user clicks on it
        /**this will give heads up notification on versions below Oreo */
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            notificationBuilder.priority = NotificationCompat.PRIORITY_HIGH
        }
        //Notification Manager
        val notificationManager = NotificationManagerCompat.from(context)
        //NOTIFICATION_ID -> this ID can be used if the notification have to ba cancelled
        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build())
    }
}
