package com.indianstudygroup.notification.firebase

import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.indianstudygroup.app_utils.AppConstant
import com.indianstudygroup.notification.helper.NotificationHelper


class MyFirebaseMessagingService : FirebaseMessagingService() {


    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                AppConstant.CHANNEL_ID,
                AppConstant.CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            )
            channel.description = AppConstant.CHANNEL_DESC
            val manager = applicationContext.getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }



        Log.d(TAG, "From: ${remoteMessage.from}")

        // Check if message contains a data payload.
        remoteMessage.data.isNotEmpty().let {
            Log.d(TAG, "Message data payload: ${remoteMessage.data}")

        }




        remoteMessage.notification?.let {
            // Handle notification message
            val title = it.title
            val body = it.body
            NotificationHelper.displayNotification(applicationContext, title, body)
            Log.d(
                TAG, "Message Notification title: ${title}\nMessage Notification Body: $body   "
            )
        }
    }

    override fun onNewToken(token: String) {
        Log.d(TAG, "Refreshed token: $token")
        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // FCM registration token to your app server.
    }

    companion object {
        private const val TAG = "MyFirebaseMsgService"
    }
}