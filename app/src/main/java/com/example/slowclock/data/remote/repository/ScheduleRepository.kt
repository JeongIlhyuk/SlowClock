// app/src/main/java/com/example/slowclock/data/repository/ScheduleRepository.kt
package com.example.slowclock.data.remote.repository

import android.util.Log
import com.example.slowclock.data.FirestoreDB
import com.example.slowclock.data.model.Schedule
import com.example.slowclock.util.AppError
import com.example.slowclock.util.toAppError
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.tasks.await
import java.util.Calendar

/**
 * Schedule 컬렉션에 대한 저장소 클래스 (정리된 버전)
 */
class ScheduleRepository {
    private val auth = FirebaseAuth.getInstance()
    private val schedulesCollection = FirestoreDB.schedules

    // 결과 타입 정의
    sealed class ScheduleResult<out T> {
        data class Success<T>(val data: T) : ScheduleResult<T>()
        data class Error(val error: AppError) : ScheduleResult<Nothing>()
    }

    // 현재 사용자의 오늘 일정 가져오기
    suspend fun getTodaySchedules(): ScheduleResult<List<Schedule>> {
        val uid = auth.currentUser?.uid
        if (uid == null) {
            Log.e("ScheduleRepo", "사용자 로그인 안됨")
            return ScheduleResult.Error(AppError.AuthError)
        }

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
            Log.d("ScheduleRepo", "일정 로드 시작")

            val documents = schedulesCollection
                .whereEqualTo("userId", uid)
                .get()
                .await()

            // 수동으로 Schedule 객체 변환 (deprecated API 회피)
            val allSchedules = documents.mapNotNull { document ->
                try {
                    val schedule = Schedule(
                        id = document.id,
                        userId = document.getString("userId") ?: "",
                        familyGroupId = document.getString("familyGroupId") ?: "",
                        title = document.getString("title") ?: "",
                        description = document.getString("description") ?: "",
                        startTime = document.getTimestamp("startTime") ?: Timestamp.now(),
                        endTime = document.getTimestamp("endTime"),
                        isCompleted = document.getBoolean("isCompleted") ?: false, // 🔥 직접 매핑
                        isRecurring = document.getBoolean("isRecurring") ?: false,
                        recurringType = document.getString("recurringType"),
                        createdAt = document.getTimestamp("createdAt") ?: Timestamp.now(),
                        updatedAt = document.getTimestamp("updatedAt") ?: Timestamp.now()
                    )

                    schedule
                } catch (e: Exception) {
                    Log.w("ScheduleRepo", "일정 파싱 실패: ${document.id}", e)
                    null
                }
            }

            // 코드에서 날짜 필터링
            val todaySchedules = allSchedules.filter { schedule ->
                val scheduleTime = schedule.startTime.toDate()
                scheduleTime.after(startOfDay) && scheduleTime.before(endOfDay)
            }.sortedBy { it.startTime }

            Log.d("ScheduleRepo", "일정 로드 성공: ${todaySchedules.size}개")
            ScheduleResult.Success(todaySchedules)

        } catch (e: FirebaseFirestoreException) {
            Log.e("ScheduleRepo", "Firestore 에러: ${e.code}", e)
            val error = when (e.code) {
                FirebaseFirestoreException.Code.UNAVAILABLE -> AppError.NetworkError
                FirebaseFirestoreException.Code.DEADLINE_EXCEEDED -> AppError.TimeoutError
                FirebaseFirestoreException.Code.PERMISSION_DENIED -> AppError.PermissionError
                FirebaseFirestoreException.Code.UNAUTHENTICATED -> AppError.AuthError
                else -> AppError.GeneralError("일정을 불러오는 중 오류가 발생했습니다: ${e.localizedMessage}")
            }
            ScheduleResult.Error(error)
        } catch (e: Exception) {
            Log.e("ScheduleRepo", "예상치 못한 에러", e)
            ScheduleResult.Error(e.toAppError())
        }
    }

    // 일정 추가
    suspend fun addSchedule(schedule: Schedule): ScheduleResult<String> {
        val uid = auth.currentUser?.uid
        if (uid == null) {
            Log.e("ScheduleRepo", "사용자 로그인 안됨")
            return ScheduleResult.Error(AppError.AuthError)
        }

        // 데이터 검증
        if (schedule.title.isBlank()) {
            return ScheduleResult.Error(AppError.InvalidDataError)
        }

        val newSchedule = schedule.copy(
            userId = uid,
            createdAt = Timestamp.now(),
            updatedAt = Timestamp.now()
        )

        return try {
            val docRef = schedulesCollection.document()
            val scheduleWithId = newSchedule.copy(id = docRef.id)
            Log.d("ScheduleRepo", "일정 저장 시도: ${scheduleWithId.title}")

            docRef.set(scheduleWithId).await()
            Log.d("ScheduleRepo", "일정 저장 성공: ${docRef.id}")
            ScheduleResult.Success(docRef.id)

        } catch (e: FirebaseFirestoreException) {
            Log.e("ScheduleRepo", "Firestore 저장 에러: ${e.code}", e)
            val error = when (e.code) {
                FirebaseFirestoreException.Code.UNAVAILABLE -> AppError.NetworkError
                FirebaseFirestoreException.Code.DEADLINE_EXCEEDED -> AppError.TimeoutError
                FirebaseFirestoreException.Code.PERMISSION_DENIED -> AppError.PermissionError
                FirebaseFirestoreException.Code.RESOURCE_EXHAUSTED -> AppError.StorageFullError
                else -> AppError.SaveError
            }
            ScheduleResult.Error(error)
        } catch (e: Exception) {
            Log.e("ScheduleRepo", "일정 저장 실패", e)
            ScheduleResult.Error(AppError.SaveError)
        }
    }

    // 일정 완료 상태 변경
    suspend fun markScheduleAsCompleted(
        scheduleId: String,
        isCompleted: Boolean = true
    ): ScheduleResult<Unit> {
        if (scheduleId.isBlank()) {
            return ScheduleResult.Error(AppError.InvalidDataError)
        }

        return try {
            schedulesCollection.document(scheduleId)
                .update(
                    mapOf(
                        "isCompleted" to isCompleted,
                        "updatedAt" to Timestamp.now()
                    )
                ).await()

            Log.d("ScheduleRepo", "완료 상태 변경 성공: $scheduleId -> $isCompleted")
            ScheduleResult.Success(Unit)

        } catch (e: FirebaseFirestoreException) {
            Log.e("ScheduleRepo", "완료 상태 변경 실패: ${e.code}", e)
            val error = when (e.code) {
                FirebaseFirestoreException.Code.NOT_FOUND -> AppError.NotFoundError
                FirebaseFirestoreException.Code.PERMISSION_DENIED -> AppError.PermissionError
                FirebaseFirestoreException.Code.UNAVAILABLE -> AppError.NetworkError
                else -> AppError.GeneralError("상태 변경에 실패했습니다")
            }
            ScheduleResult.Error(error)
        } catch (e: Exception) {
            Log.e("ScheduleRepo", "완료 상태 변경 중 예상치 못한 에러", e)
            ScheduleResult.Error(e.toAppError())
        }
    }

    suspend fun updateSchedule(schedule: Schedule): ScheduleResult<Unit> {
        val uid = auth.currentUser?.uid
        if (uid == null) {
            return ScheduleResult.Error(AppError.AuthError)
        }

        if (schedule.id.isBlank()) {
            return ScheduleResult.Error(AppError.InvalidDataError)
        }

        if (schedule.title.isBlank()) {
            return ScheduleResult.Error(AppError.InvalidDataError)
        }

        val updatedSchedule = schedule.copy(
            userId = uid, // 현재 사용자 ID로 강제 설정
            updatedAt = Timestamp.now()
        )

        return try {
            schedulesCollection.document(schedule.id)
                .set(updatedSchedule)
                .await()

            Log.d("ScheduleRepo", "일정 수정 성공: ${schedule.id}")
            ScheduleResult.Success(Unit)

        } catch (e: FirebaseFirestoreException) {
            Log.e("ScheduleRepo", "일정 수정 실패: ${e.code}", e)
            val error = when (e.code) {
                FirebaseFirestoreException.Code.NOT_FOUND -> AppError.NotFoundError
                FirebaseFirestoreException.Code.PERMISSION_DENIED -> AppError.PermissionError
                FirebaseFirestoreException.Code.UNAVAILABLE -> AppError.NetworkError
                else -> AppError.SaveError
            }
            ScheduleResult.Error(error)
        } catch (e: Exception) {
            Log.e("ScheduleRepo", "일정 수정 중 예상치 못한 에러", e)
            ScheduleResult.Error(AppError.SaveError)
        }
    }

    // 일정 삭제
    suspend fun deleteSchedule(scheduleId: String): ScheduleResult<Unit> {
        if (scheduleId.isBlank()) {
            return ScheduleResult.Error(AppError.InvalidDataError)
        }

        return try {
            schedulesCollection.document(scheduleId)
                .delete()
                .await()

            Log.d("ScheduleRepo", "일정 삭제 성공: $scheduleId")
            ScheduleResult.Success(Unit)

        } catch (e: FirebaseFirestoreException) {
            Log.e("ScheduleRepo", "일정 삭제 실패: ${e.code}", e)
            val error = when (e.code) {
                FirebaseFirestoreException.Code.NOT_FOUND -> AppError.NotFoundError
                FirebaseFirestoreException.Code.PERMISSION_DENIED -> AppError.PermissionError
                FirebaseFirestoreException.Code.UNAVAILABLE -> AppError.NetworkError
                else -> AppError.GeneralError("삭제에 실패했습니다")
            }
            ScheduleResult.Error(error)
        } catch (e: Exception) {
            Log.e("ScheduleRepo", "일정 삭제 중 예상치 못한 에러", e)
            ScheduleResult.Error(e.toAppError())
        }
    }

    // ID로 일정 가져오기 (편집용)
    suspend fun getScheduleById(scheduleId: String): ScheduleResult<Schedule> {
        if (scheduleId.isBlank()) {
            return ScheduleResult.Error(AppError.InvalidDataError)
        }

        return try {
            val document = schedulesCollection.document(scheduleId).get().await()

            if (!document.exists()) {
                return ScheduleResult.Error(AppError.NotFoundError)
            }

            val schedule = document.toObject(Schedule::class.java)
            if (schedule != null) {
                ScheduleResult.Success(schedule)
            } else {
                ScheduleResult.Error(AppError.InvalidDataError)
            }

        } catch (e: FirebaseFirestoreException) {
            Log.e("ScheduleRepo", "일정 조회 실패: ${e.code}", e)
            val error = when (e.code) {
                FirebaseFirestoreException.Code.UNAVAILABLE -> AppError.NetworkError
                FirebaseFirestoreException.Code.PERMISSION_DENIED -> AppError.PermissionError
                else -> AppError.NotFoundError
            }
            ScheduleResult.Error(error)
        } catch (e: Exception) {
            Log.e("ScheduleRepo", "일정 조회 중 예상치 못한 에러", e)
            ScheduleResult.Error(e.toAppError())
        }
    }
}