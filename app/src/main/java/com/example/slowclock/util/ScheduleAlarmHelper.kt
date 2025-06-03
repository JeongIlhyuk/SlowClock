package com.example.slowclock.util

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.example.slowclock.data.model.Schedule
import com.example.slowclock.reciever.AlarmReceiver

object ScheduleAlarmHelper {
    fun scheduleAlarm(context: Context, schedule: Schedule) {
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra("title", schedule.title)
            putExtra("desc", schedule.description)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context, schedule.id.hashCode(), intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            schedule.startTime.toDate().time,
            pendingIntent
        )
    }
}
