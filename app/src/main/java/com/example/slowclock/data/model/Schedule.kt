package com.example.slowclock.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId

// Schedule 모델
data class Schedule(
    @DocumentId val id: String = "",
    val userId: String = "",
    val title: String = "",
    val description: String = "",
    val startTime: Timestamp = Timestamp.now(),
    val endTime: Timestamp? = null,
    val isCompleted: Boolean = false,
    val isRecurring: Boolean = false,
    val recurringType: String? = null, // "daily", "weekly", "monthly"
    val isSkipped: Boolean = false,
    val createdAt: Timestamp = Timestamp.now(),
    val updatedAt: Timestamp = Timestamp.now()
)