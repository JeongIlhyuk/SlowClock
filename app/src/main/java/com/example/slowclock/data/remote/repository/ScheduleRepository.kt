// app/src/main/java/com/example/slowclock/data/remote/repository/ScheduleRepository.kt
package com.example.slowclock.data.remote.repository

import android.content.Context
import android.util.Log
import com.example.slowclock.data.FirestoreDB
import com.example.slowclock.data.model.Schedule
import com.example.slowclock.notification.GuardianNotifier
import com.example.slowclock.util.AppError
import com.example.slowclock.util.toAppError
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
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

    // 🔥 수동 파싱 함수 - toObject() 대신 이걸 써야 해
    private fun parseScheduleFromDocument(doc: com.google.firebase.firestore.DocumentSnapshot): Schedule? {
        return try {
            val data = doc.data ?: return null

            // 각 필드를 개별적으로 파싱해서 확실하게 가져옴
            val id = doc.id
            val userId = data["userId"] as? String ?: ""
            val familyGroupId = data["familyGroupId"] as? String ?: ""
            val sharedCode = data["sharedCode"] as? String ?: ""
            val title = data["title"] as? String ?: ""
            val description = data["description"] as? String ?: ""
            val startTime = data["startTime"] as? Timestamp ?: Timestamp.now()
            val endTime = data["endTime"] as? Timestamp
            val recurringType = data["recurringType"] as? String
            val createdAt = data["createdAt"] as? Timestamp ?: Timestamp.now()
            val updatedAt = data["updatedAt"] as? Timestamp ?: Timestamp.now()

            // Boolean 필드들을 명시적으로 변환
            val completed = when (val completed = data["completed"]) {
                is Boolean -> completed
                is String -> completed.toBoolean()
                else -> {
                    Log.w(
                        "ScheduleRepo",
                        "completed 필드 파싱 실패: $completed (타입: ${completed?.javaClass})"
                    )
                    false
                }
            }

            val recurring = when (val recurring = data["recurring"]) {
                is Boolean -> recurring
                is String -> recurring.toBoolean()
                else -> false
            }

            val skipped = when (val skipped = data["skipped"]) {
                is Boolean -> skipped
                is String -> skipped.toBoolean()
                else -> false
            }

            Log.d("ScheduleRepo", "파싱된 일정: $title, 완료상태: $completed (원본: ${data["completed"]})")

            Schedule(
                id = id,
                userId = userId,
                familyGroupId = familyGroupId,
                sharedCode = sharedCode,
                title = title,
                description = description,
                startTime = startTime,
                endTime = endTime,
                completed = completed,
                recurring = recurring,
                skipped = skipped,
                recurringType = recurringType,
                createdAt = createdAt,
                updatedAt = updatedAt
            )
        } catch (e: Exception) {
            Log.e("ScheduleRepo", "일정 파싱 실패: ${doc.id}", e)
            null
        }
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

            Log.d("ScheduleRepo", "Firestore에서 가져온 문서 수: ${documents.size()}")

            // 🔥 자동 변환 대신 수동 파싱 사용
            val allSchedules = documents.mapNotNull { document ->
                Log.d("ScheduleRepo", "문서 원본 데이터: ${document.data}")
                parseScheduleFromDocument(document)
            }

            // 코드에서 날짜 필터링
            val todaySchedules = allSchedules.filter { schedule ->
                val scheduleTime = schedule.startTime.toDate()
                scheduleTime.after(startOfDay) && scheduleTime.before(endOfDay)
            }.sortedBy { it.startTime }

            Log.d("ScheduleRepo", "=== 최종 결과 ===")
            Log.d("ScheduleRepo", "전체 일정: ${allSchedules.size}개")
            Log.d("ScheduleRepo", "오늘 일정: ${todaySchedules.size}개")
            Log.d("ScheduleRepo", "완료된 일정: ${todaySchedules.count { it.completed }}개")

            todaySchedules.forEach { schedule ->
                Log.d("ScheduleRepo", "- ${schedule.title}: 완료=${schedule.completed}")
            }

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

        // Fetch user's shareCode
        val userDoc = FirestoreDB.users.document(uid).get().await()
        val userShareCode = userDoc.getString("shareCode") ?: ""
        Log.d("ScheduleRepo", "Fetched userShareCode: '$userShareCode'")
        if (userShareCode.isBlank()) {
            Log.w("ScheduleRepo", "User's shareCode is blank! Schedule will be saved without a sharedCode.")
        }

        val newSchedule = schedule.copy(
            userId = uid,
            sharedCode = userShareCode,
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
        completed: Boolean = true
    ): ScheduleResult<Unit> {
        if (scheduleId.isBlank()) {
            return ScheduleResult.Error(AppError.InvalidDataError)
        }

        return try {
            Log.d("ScheduleRepo", "완료 상태 변경 시도: $scheduleId -> $completed")

            schedulesCollection.document(scheduleId)
                .update(
                    mapOf(
                        "completed" to completed,
                        "updatedAt" to Timestamp.now()
                    )
                ).await()

            Log.d("ScheduleRepo", "완료 상태 변경 성공: $scheduleId -> $completed")
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

        // Always preserve sharedCode and all fields from the original schedule
        val updatedSchedule = schedule.copy(
            userId = uid, // 현재 사용자 ID로 강제 설정
            updatedAt = Timestamp.now(),
            sharedCode = schedule.sharedCode // ensure sharedCode is preserved
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

            val schedule = parseScheduleFromDocument(document)
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

    // 공유 코드로 일정(리마인더) 목록 가져오기
    suspend fun getSchedulesBySharedCode(sharedCode: String): ScheduleResult<List<Schedule>> {
        Log.d("ScheduleRepo", "공유코드로 일정 조회 시작: $sharedCode")
        return try {
            val documents = schedulesCollection
                .whereEqualTo("sharedCode", sharedCode)
                .get()
                .await()

            Log.d("ScheduleRepo", "공유코드로 조회된 문서 수: ${documents.size()}")

            val schedules = documents.mapNotNull { document ->
                parseScheduleFromDocument(document)
            }

            if (schedules.isEmpty()) {
                Log.i("ScheduleRepo", "공유코드로 불러온 일정이 없습니다.")
            } else {
                Log.i("ScheduleRepo", "공유코드로 불러온 일정: ${schedules.size}")
                schedules.forEach { schedule ->
                    Log.d("ScheduleRepo", "공유 일정: ${schedule.title}, 완료: ${schedule.completed}")
                }
            }
            ScheduleResult.Success(schedules)
        } catch (e: FirebaseFirestoreException) {
            Log.e("ScheduleRepo", "공유코드 일정 조회 실패: ${e.code}", e)
            ScheduleResult.Error(AppError.GeneralError("공유코드 일정 조회 실패: ${e.localizedMessage}"))
        } catch (e: Exception) {
            Log.e("ScheduleRepo", "공유코드 일정 조회 중 에러", e)
            ScheduleResult.Error(AppError.GeneralError("공유코드 일정 조회 중 에러: ${e.localizedMessage}"))
        }
    }

    // 공유 코드로 일정(리마인더) 목록 실시간 가져오기
    fun observeSchedulesBySharedCode(sharedCode: String): Flow<List<Schedule>> = callbackFlow {
        val listener = schedulesCollection
            .whereEqualTo("sharedCode", sharedCode)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val schedules = snapshot?.documents?.mapNotNull { doc ->
                    parseScheduleFromDocument(doc)
                } ?: emptyList()
                trySend(schedules)
            }
        awaitClose { listener.remove() }
    }

    // 공유코드로 같은 그룹의 모든 사용자에게 FCM 알림 발송
    suspend fun sendNotificationToShareCodeMembers(
        context: Context,
        shareCode: String,
        title: String,
        message: String
    ) {
        val currentUid = auth.currentUser?.uid ?: return
        val users = FirestoreDB.users
            .whereEqualTo("shareCode", shareCode)
            .get().await()
            .documents
            .mapNotNull { it }
        for (userDoc in users) {
            val uid = userDoc.getString("id") ?: continue
            if (uid == currentUid) continue // Skip self
            val fcmToken = userDoc.getString("fcmToken") ?: continue
            GuardianNotifier.sendReminderToUser(context, fcmToken, title, message)
        }

        // Optimized: send to all tokens at once (if GuardianNotifier supports it)
        val tokens = users
            .filter { it.id != currentUid }
            .mapNotNull { it.getString("fcmToken") }
            .filter { it.isNotBlank() }
        if (tokens.isNotEmpty()) {
            GuardianNotifier.sendReminderToUsers(context, tokens, title, message)
        }
    }

    // 🔔 공유코드로 FCM 토큰 리스트 가져오기 (자기 자신 제외)
    suspend fun getFcmTokensByShareCode(shareCode: String): List<String> {
        val currentUid = auth.currentUser?.uid ?: return emptyList()
        return try {
            val users = FirestoreDB.users
                .whereEqualTo("shareCode", shareCode)
                .get().await()
            users.documents
                .filter { it.id != currentUid }
                .mapNotNull { it.getString("fcmToken") }
                .filter { it.isNotBlank() }
        } catch (e: Exception) {
            Log.e("ScheduleRepo", "FCM 토큰 조회 실패", e)
            emptyList()
        }
    }

    // 🔔 여러 사용자에게 FCM 알림 전송
    suspend fun notifyShareCodeMembers(
        context: Context,
        shareCode: String,
        title: String,
        message: String
    ) {
        val tokens = getFcmTokensByShareCode(shareCode)
        if (tokens.isNotEmpty()) {
            GuardianNotifier.sendReminderToUsers(context, tokens, title, message)
        }
    }
}