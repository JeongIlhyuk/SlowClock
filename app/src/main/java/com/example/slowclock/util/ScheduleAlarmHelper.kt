package com.example.slowclock.util

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.example.slowclock.reciever.AlarmReceiver
import com.example.slowclock.data.model.Schedule
import java.util.*

object ScheduleAlarmHelper {

    fun scheduleAlarm(context: Context, schedule: Schedule) {
        cancelAlarm(context, schedule)

        val now = System.currentTimeMillis()
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        // 예약: startTime
        schedule.startTime?.toDate()?.time?.takeIf { it > now }?.let { triggerTime ->
            val intent = Intent(context, AlarmReceiver::class.java).apply {
                putExtra("title", schedule.title)
                putExtra("desc", schedule.description)
            }

            val requestCode = schedule.id.hashCode()
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                requestCode,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            Log.d("AlarmHelper", "⏰ 시작 알람 예약: ${Date(triggerTime)} (requestCode=$requestCode)")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent)
            } else {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent)
            }
        }

        // 예약: endTime
        schedule.endTime?.toDate()?.time?.takeIf { it > now }?.let { triggerTime ->
            val intent = Intent(context, AlarmReceiver::class.java).apply {
                putExtra("title", "${schedule.title}")
                putExtra("desc", "${schedule.description}")
            }

            val requestCode = schedule.id.hashCode() + 9999 // end 알람은 별도 requestCode
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                requestCode,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            Log.d("AlarmHelper", "⏰ 종료 알람 예약: ${Date(triggerTime)} (requestCode=$requestCode)")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent)
            } else {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent)
            }
        }
    }

    fun cancelAlarm(context: Context, schedule: Schedule) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        // 시작 알람 취소
        val startIntent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra("title", schedule.title)
            putExtra("desc", schedule.description)
        }

        val startPendingIntent = PendingIntent.getBroadcast(
            context,
            schedule.id.hashCode(),
            startIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.cancel(startPendingIntent)
        Log.d("AlarmHelper", "🛑 시작 알람 취소 (requestCode=${schedule.id.hashCode()})")

        // 종료 알람 취소
        val endIntent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra("title", "${schedule.title}")
            putExtra("desc", "${schedule.description}")
        }

        val endPendingIntent = PendingIntent.getBroadcast(
            context,
            schedule.id.hashCode() + 9999,
            endIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.cancel(endPendingIntent)
        Log.d("AlarmHelper", "🛑 종료 알람 취소 (requestCode=${schedule.id.hashCode() + 9999})")
    }
}
