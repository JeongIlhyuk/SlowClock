package com.example.slowclock.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId

// ScheduleRecommendation 모델
data class ScheduleRecommendation(
    @DocumentId val id: String = "",
    val title: String = "",
    val description: String = "",
    val defaultStartTime: Timestamp? = null,
    val defaultDuration: Long = 0, // 분 단위
    val targetUserType: String = "", // "elderly", "adhd", "general"
    val popularityScore: Int = 0,
    val createdAt: Timestamp = Timestamp.now()
)