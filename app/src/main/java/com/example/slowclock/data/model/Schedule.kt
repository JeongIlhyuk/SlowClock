package com.example.slowclock.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.PropertyName

// Schedule 모델
data class Schedule(
    @DocumentId val id: String = "",
    val userId: String = "",
    val familyGroupId: String = "", //  가족 일정 공유를 위한 필드 추가
    val sharedCode: String = "", // 공유 코드로 일정 공유
    val title: String = "",
    val description: String = "",
    val startTime: Timestamp = Timestamp.now(),
    val endTime: Timestamp? = null,
    @get:PropertyName("completed") @set:PropertyName("completed")
    var isCompleted: Boolean = false,
    val isRecurring: Boolean = false,
    val isSkipped: Boolean = false,
    val recurringType: String? = null, // "daily", "weekly", "monthly"
    val createdAt: Timestamp = Timestamp.now(),
    val updatedAt: Timestamp = Timestamp.now()
)