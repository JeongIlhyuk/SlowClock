package com.example.slowclock.util

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.slowclock.reciever.AlarmReceiver
import com.example.slowclock.data.model.Schedule

object ScheduleAlarmHelper {
    fun scheduleAlarm(context: Context, schedule: Schedule) {
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra("title", schedule.title)
            putExtra("desc", schedule.description)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            schedule.id.hashCode(), // 고유값이면 hashCode로 충분!
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        // startTime은 Timestamp 타입이므로 toDate().time으로 변환
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            schedule.startTime?.toDate()?.time ?: System.currentTimeMillis(),
            pendingIntent
        )
        Log.d("AlarmHelper", "알람 예약 완료")
    }
}
