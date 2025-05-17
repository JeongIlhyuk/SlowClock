package com.example.slowclock.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId

// User 모델
data class User(
    @DocumentId val id: String = "",
    val name: String = "",
    val email: String = "",
    val profileImage: String = "",
    val createdAt: Timestamp = Timestamp.now(),
    val updatedAt: Timestamp = Timestamp.now()
)