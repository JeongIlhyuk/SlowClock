package com.example.slowclock.data.repository

import com.example.slowclock.data.FirestoreDB
import com.example.slowclock.data.model.ScheduleRecommendation
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.firestore.ktx.toObjects
import kotlinx.coroutines.tasks.await

/**
 * ScheduleRecommendation 컬렉션에 대한 저장소 클래스
 */
class RecommendationRepository {
    private val recommendationsCollection = FirestoreDB.scheduleRecommendations

    // 추천 일정 목록 가져오기 (인기도 순)
    suspend fun getRecommendations(limit: Int = 10): List<ScheduleRecommendation> {
        return try {
            recommendationsCollection
                .orderBy("popularityScore", Query.Direction.DESCENDING)
                .limit(limit.toLong())
                .get()
                .await()
                .toObjects()
        } catch (e: Exception) {
            emptyList()
        }
    }

    // 특정 사용자 유형에 맞는 추천 일정 가져오기
    suspend fun getRecommendationsByUserType(
        userType: String,
        limit: Int = 10
    ): List<ScheduleRecommendation> {
        return try {
            recommendationsCollection
                .whereEqualTo("targetUserType", userType)
                .orderBy("popularityScore", Query.Direction.DESCENDING)
                .limit(limit.toLong())
                .get()
                .await()
                .toObjects()
        } catch (e: Exception) {
            emptyList()
        }
    }

    // ID로 추천 일정 가져오기
    suspend fun getRecommendationById(recommendationId: String): ScheduleRecommendation? {
        return try {
            val document = recommendationsCollection.document(recommendationId).get().await()
            document.toObject<ScheduleRecommendation>()
        } catch (e: Exception) {
            null
        }
    }
}