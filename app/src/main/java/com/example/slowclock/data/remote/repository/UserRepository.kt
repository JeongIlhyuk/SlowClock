package com.example.slowclock.data.remote.repository

import com.example.slowclock.data.FirestoreDB
import com.example.slowclock.data.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.tasks.await

/**
 * User 컬렉션에 대한 저장소 클래스
 */
class UserRepository {
    private val auth = FirebaseAuth.getInstance()
    private val usersCollection = FirestoreDB.users

    // 현재 로그인한 사용자 정보 가져오기
    suspend fun getCurrentUser(): User? {
        val uid = auth.currentUser?.uid ?: return null
        return try {
            val document = usersCollection.document(uid).get().await()
            document.toObject<User>()
        } catch (e: Exception) {
            null
        }
    }

    // 사용자 정보 저장/업데이트
    suspend fun saveUser(user: User): Boolean {
        return try {
            usersCollection.document(user.id).set(user).await()
            true
        } catch (e: Exception) {
            false
        }
    }

    // ID로 사용자 정보 가져오기
    suspend fun getUserById(userId: String): User? {
        return try {
            val document = usersCollection.document(userId).get().await()
            document.toObject<User>()
        } catch (e: Exception) {
            null
        }
    }
}