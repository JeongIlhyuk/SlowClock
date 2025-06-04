package com.example.slowclock.reciever

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
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

        Log.d("AlarmReceiver", "알람 수신됨 - 제목: $title, 내용: $desc")

        val builder = NotificationCompat.Builder(context, "schedule_channel")
            .setSmallIcon(R.drawable.baseline_access_alarm_24)
            .setContentTitle(title)
            .setContentText(desc)
            .setPriority(NotificationCompat.PRIORITY_HIGH)

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            with(NotificationManagerCompat.from(context)) {
                notify(System.currentTimeMillis().toInt(), builder.build())
            }
        } else {
            Log.w("AlarmReceiver", "알림 권한 없음")
        }
    }
}
