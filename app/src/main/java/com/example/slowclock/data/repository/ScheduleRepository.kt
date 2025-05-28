package com.example.slowclock.data.repository

import android.util.Log
import com.example.slowclock.data.FirestoreDB
import com.example.slowclock.data.model.Schedule
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.firestore.ktx.toObjects
import kotlinx.coroutines.tasks.await
import java.util.Calendar

/**
 * Schedule 컬렉션에 대한 저장소 클래스
 */
class ScheduleRepository {
    private val auth = FirebaseAuth.getInstance()
    private val schedulesCollection = FirestoreDB.schedules

    // 현재 사용자의 오늘 일정 가져오기
// ScheduleRepository.kt
    // ScheduleRepository.kt 수정
    suspend fun getTodaySchedules(): List<Schedule> {
        val uid = auth.currentUser?.uid ?: return emptyList()

        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        val startOfDay = calendar.time

        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
        val endOfDay = calendar.time

        return try {
            // 단순 쿼리로 변경 (날짜 필터링 제거)
            val allSchedules = schedulesCollection
                .whereEqualTo("userId", uid)
                .get()
                .await()
                .toObjects<Schedule>()

            // 코드에서 날짜 필터링
            val todaySchedules = allSchedules.filter { schedule ->
                val scheduleTime = schedule.startTime.toDate()
                scheduleTime.after(startOfDay) && scheduleTime.before(endOfDay)
            }.sortedBy { it.startTime }

            Log.d("ScheduleRepo", "오늘 일정 개수: ${todaySchedules.size}")
            todaySchedules
        } catch (e: Exception) {
            Log.e("ScheduleRepo", "쿼리 실패", e)
            emptyList()
        }
    }

    // 일정 추가
    suspend fun addSchedule(schedule: Schedule): String? {
        val uid = auth.currentUser?.uid ?: return null

        val newSchedule = schedule.copy(
            userId = uid,
            createdAt = Timestamp.now(),
            updatedAt = Timestamp.now()
        )

        return try {
            val docRef = schedulesCollection.document()
            val scheduleWithId = newSchedule.copy(id = docRef.id)
            docRef.set(scheduleWithId).await()
            docRef.id
        } catch (e: Exception) {
            null
        }
    }

    // 일정 업데이트
    suspend fun updateSchedule(schedule: Schedule): Boolean {
        val uid = auth.currentUser?.uid ?: return false
        if (schedule.userId != uid) return false

        return try {
            val updatedSchedule = schedule.copy(updatedAt = Timestamp.now())
            schedulesCollection.document(schedule.id).set(updatedSchedule).await()
            true
        } catch (e: Exception) {
            false
        }
    }

    // 일정 삭제
    suspend fun deleteSchedule(scheduleId: String): Boolean {
        return try {
            schedulesCollection.document(scheduleId).delete().await()
            true
        } catch (e: Exception) {
            false
        }
    }

    // 일정 완료 상태 변경
    suspend fun markScheduleAsCompleted(scheduleId: String, isCompleted: Boolean = true): Boolean {
        return try {
            schedulesCollection.document(scheduleId)
                .update(
                    mapOf(
                        "isCompleted" to isCompleted,
                        "updatedAt" to Timestamp.now()
                    )
                ).await()
            true
        } catch (e: Exception) {
            false
        }
    }

    // ID로 일정 가져오기
    suspend fun getScheduleById(scheduleId: String): Schedule? {
        return try {
            val document = schedulesCollection.document(scheduleId).get().await()
            document.toObject<Schedule>()
        } catch (e: Exception) {
            null
        }
    }
}