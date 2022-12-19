package com.home.myweather.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.home.myweather.R

class MyFirebaseMessagingService : FirebaseMessagingService() {

    companion object {
        private const val NOTIFICATION_ID_1 = 1
        private const val CHANNEL_ID_1 = " channel_id_1"
        private const val MY_TITLE = "myTitle"
        private const val MY_MESSAGE = "myMessage"
    }

    override fun onMessageReceived(message: RemoteMessage) {
        val data = message.data.toMap()
        if (data.isEmpty()) {
            val title = data[MY_TITLE]
            val message = data[MY_MESSAGE]
            if (!title.isNullOrBlank() && !message.isNullOrBlank())
                pushNotification(title, message)
        }
    }

    private fun pushNotification(title: String, message: String) {
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val notificationBuilderFirst = NotificationCompat.Builder(this, CHANNEL_ID_1).apply {
            setSmallIcon(R.drawable.ic_kotlin_logo)
            setContentTitle(title)
            setContentText(message)
            priority = NotificationCompat.PRIORITY_MAX
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelNameFirst = (getString(R.string.channel_name) + CHANNEL_ID_1)
            val channelDescriptionFirst =
                (getString(R.string.channel_description_priority_max) + CHANNEL_ID_1)
            val channelPriorityFirst = NotificationManager.IMPORTANCE_HIGH

            val channelFirst =
                NotificationChannel(CHANNEL_ID_1, channelNameFirst, channelPriorityFirst).apply {
                    description = channelDescriptionFirst
                }
            notificationManager.createNotificationChannel(channelFirst)
        }
        notificationManager.notify(NOTIFICATION_ID_1, notificationBuilderFirst.build())
    }
}