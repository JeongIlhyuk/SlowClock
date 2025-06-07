package com.example.slowclock.reciever

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.slowclock.R

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val title = intent.getStringExtra("title") ?: "Schedule Reminder"
        val desc = intent.getStringExtra("desc") ?: ""

        Log.d("AlarmReceiver", "🔔 알람 수신됨 - 제목: $title, 내용: $desc")

        // 🆕 고유 채널 ID 생성
        val channelId = "schedule_channel"

        // 🆕 알림 채널 생성 (알람마다 다르게)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION) // or custom sound
            val audioAttributes = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()

            val channel = NotificationChannel(channelId, "일정 알림", NotificationManager.IMPORTANCE_HIGH).apply {
                description = "일정 시간에 울리는 알림"
                enableLights(true)
                enableVibration(true)
                setSound(soundUri, audioAttributes)
            }

            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.baseline_access_alarm_24)
            .setContentTitle(title)
            .setContentText(desc)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setDefaults(NotificationCompat.DEFAULT_ALL)

        // 🆕 고유 notificationId
        val notificationId = System.currentTimeMillis().toInt()
        Log.d("AlarmReceiver", "🔔 notify 실행: $notificationId, title=$title, channel=$channelId")

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
        ) {
            NotificationManagerCompat.from(context).notify(notificationId, builder.build())
        } else {
            Log.w("AlarmReceiver", "⚠️ 알림 권한 없음 (Android 13 이상)")
        }
    }
}
