package com.example.slowclock.data

import android.util.Log
import com.example.slowclock.data.model.Schedule
import com.example.slowclock.data.repository.ScheduleRepository
import com.google.firebase.Timestamp
import java.util.Calendar

class DummyDataManager {
    private val scheduleRepository = ScheduleRepository()

    suspend fun addDummyDataIfNeeded() {
        try {
            val existing = scheduleRepository.getTodaySchedules()

            if (existing.isEmpty()) {
                Log.d("DUMMY", "일정 없음, 더미 데이터 추가")

                val schedules = listOf(
                    Schedule(title = "아침 운동", startTime = getTodayTime(9, 0)),
                    Schedule(
                        title = "점심 약속",
                        startTime = getTodayTime(12, 30),
                        isCompleted = true
                    ),
                    Schedule(title = "저녁 산책", startTime = getTodayTime(18, 0)),
                    Schedule(title = "지금 할 일", startTime = Timestamp.now())
                )

                schedules.forEach {
                    scheduleRepository.addSchedule(it)
                }
                Log.d("DUMMY", "더미 데이터 추가 완료")
            } else {
                Log.d("DUMMY", "이미 일정 있음: ${existing.size}개")
            }
        } catch (e: Exception) {
            Log.e("DUMMY", "더미 데이터 추가 실패", e)
        }
    }

    private fun getTodayTime(hour: Int, minute: Int): Timestamp {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, hour)
        calendar.set(Calendar.MINUTE, minute)
        calendar.set(Calendar.SECOND, 0)
        return Timestamp(calendar.time)
    }
}