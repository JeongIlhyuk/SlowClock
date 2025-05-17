package com.example.slowclock.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.example.slowclock.MainActivity
import com.example.slowclock.R
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class FCMService : FirebaseMessagingService() {
    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        val title = message.notification?.title ?: "일정 알림"
        val body = message.notification?.body ?: "새로운 알림이 있습니다."

        sendNotification(title, body)
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        // 토큰 서버에 저장하는 로직 필요 (필요시 추가)
    }

    private fun sendNotification(title: String, body: String) {
        val channelId = "schedule_channel"

        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(body)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // 채널 생성 (Android 8.0 이상)
        val channel = NotificationChannel(
            channelId,
            "일정 알림",
            NotificationManager.IMPORTANCE_HIGH
        )
        notificationManager.createNotificationChannel(channel)

        notificationManager.notify(0, notificationBuilder.build())
    }
}