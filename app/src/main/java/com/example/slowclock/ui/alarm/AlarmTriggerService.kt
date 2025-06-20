package com.example.slowclock.ui.alarm

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.slowclock.MainActivity
import com.example.slowclock.R
import com.example.slowclock.receiver.AlarmDismissReceiver

class AlarmTriggerService : Service() {

    companion object {
        private const val FOREGROUND_SERVICE_ID = 123
        private const val NOTIFICATION_CHANNEL_ID = "alarm_notification_channel"
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val title = intent?.getStringExtra("title") ?: "알람"
        val desc = intent?.getStringExtra("desc") ?: ""
        val isFullScreen = intent?.getBooleanExtra("isFullScreen", false) ?: false

        Log.d("AlarmTriggerService", "알람 서비스 시작: $title, 풀스크린: $isFullScreen")

        val notification = if (isFullScreen) {
            createFullScreenNotification(title, desc)
        } else {
            createHeadsUpNotification(title, desc)
        }

        // 포그라운드 서비스로 시작
        startForeground(FOREGROUND_SERVICE_ID, notification)

        // 잠시 후 서비스 종료 (알림은 유지됨)
        android.os.Handler(mainLooper).postDelayed({
            // 📌 헤드업 알림의 경우 서비스를 바로 종료하지 않고 조금 더 유지
            // 풀스크린의 경우에만 바로 종료
            if (isFullScreen) {
                stopSelf()
            } else {
                // 헤드업 알림은 5초 후 서비스 종료 (알림은 계속 유지됨)
                android.os.Handler(mainLooper).postDelayed({
                    stopSelf()
                }, 5000)
            }
        }, 1000)

        return START_NOT_STICKY
    }

    private fun createHeadsUpNotification(title: String, desc: String): Notification {
        val tapIntent = Intent(this, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
        }
        val tapPendingIntent = PendingIntent.getActivity(
            this, 0, tapIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // 알림 닫기 액션 추가
        val dismissIntent = Intent(this, AlarmDismissReceiver::class.java)
        val dismissPendingIntent = PendingIntent.getBroadcast(
            this, 0, dismissIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.baseline_access_alarm_24)
            .setContentTitle("⏰ $title")
            .setContentText(desc)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setContentIntent(tapPendingIntent)
            .setAutoCancel(false) // 📌 알림이 자동으로 사라지지 않도록 설정
            .setOngoing(true) // 📌 사용자가 직접 해제할 때까지 유지
            .addAction(R.drawable.baseline_close_24, "닫기", dismissPendingIntent)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .build()
    }

    private fun createFullScreenNotification(title: String, desc: String): Notification {
        val fullScreenIntent = Intent(this, AlarmFullScreenActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
            putExtra("title", title)
            putExtra("desc", desc)
        }
        val fullScreenPendingIntent = PendingIntent.getActivity(
            this, 1, fullScreenIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val mainActivityIntent = Intent(this, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
        }
        val mainActivityPendingIntent = PendingIntent.getActivity(
            this, 2, mainActivityIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.baseline_access_alarm_24)
            .setContentTitle("⏰ $title")
            .setContentText(desc)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setContentIntent(mainActivityPendingIntent)
            .setFullScreenIntent(fullScreenPendingIntent, true)
            .setAutoCancel(true)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .build()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                "알람 알림",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "알람 및 미리 알림"
                lockscreenVisibility = Notification.VISIBILITY_PUBLIC
                enableVibration(true)
                enableLights(true)
                setSound(
                    android.media.RingtoneManager.getDefaultUri(android.media.RingtoneManager.TYPE_ALARM),
                    null
                )
            }
            val nm = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            nm.createNotificationChannel(channel)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("AlarmTriggerService", "알람 서비스 종료")
    }

    override fun onBind(intent: Intent?): IBinder? = null
}