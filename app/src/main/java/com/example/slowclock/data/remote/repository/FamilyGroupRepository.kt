package com.example.slowclock.data.remote.repository

import com.example.slowclock.data.FirestoreDB
import com.example.slowclock.data.model.FamilyGroup
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.firestore.ktx.toObjects
import kotlinx.coroutines.tasks.await

/**
 * FamilyGroup 컬렉션에 대한 저장소 클래스
 */
class FamilyGroupRepository {
    private val auth = FirebaseAuth.getInstance()
    private val familyGroupsCollection = FirestoreDB.familyGroups

    // 사용자가 속한 가족 그룹 목록 가져오기
    suspend fun getUserFamilyGroups(): List<FamilyGroup> {
        val uid = auth.currentUser?.uid ?: return emptyList()

        return try {
            familyGroupsCollection
                .whereArrayContains("memberIds", uid)
                .get()
                .await()
                .toObjects()
        } catch (e: Exception) {
            emptyList()
        }
    }

    // 사용자가 소유한 가족 그룹 목록 가져오기
    suspend fun getOwnedFamilyGroups(): List<FamilyGroup> {
        val uid = auth.currentUser?.uid ?: return emptyList()

        return try {
            familyGroupsCollection
                .whereEqualTo("ownerUserId", uid)
                .get()
                .await()
                .toObjects()
        } catch (e: Exception) {
            emptyList()
        }
    }

    // 새 가족 그룹 생성
    suspend fun createFamilyGroup(name: String): String? {
        val uid = auth.currentUser?.uid ?: return null

        val familyGroup = FamilyGroup(
            name = name,
            ownerUserId = uid,
            memberIds = listOf(uid),
            createdAt = Timestamp.now(),
            updatedAt = Timestamp.now()
        )

        return try {
            val docRef = familyGroupsCollection.document()
            val groupWithId = familyGroup.copy(id = docRef.id)
            docRef.set(groupWithId).await()
            docRef.id
        } catch (e: Exception) {
            null
        }
    }

    // 가족 그룹에 멤버 추가
    suspend fun addMemberToGroup(groupId: String, memberId: String): Boolean {
        return try {
            familyGroupsCollection.document(groupId)
                .update(
                    mapOf(
                        "memberIds" to FieldValue.arrayUnion(memberId),
                        "updatedAt" to Timestamp.now()
                    )
                ).await()
            true
        } catch (e: Exception) {
            false
        }
    }

    // 가족 그룹에서 멤버 제거
    suspend fun removeMemberFromGroup(groupId: String, memberId: String): Boolean {
        return try {
            familyGroupsCollection.document(groupId)
                .update(
                    mapOf(
                        "memberIds" to FieldValue.arrayRemove(memberId),
                        "updatedAt" to Timestamp.now()
                    )
                ).await()
            true
        } catch (e: Exception) {
            false
        }
    }

    // 가족 그룹 정보 가져오기
    suspend fun getFamilyGroupById(groupId: String): FamilyGroup? {
        return try {
            val document = familyGroupsCollection.document(groupId).get().await()
            document.toObject<FamilyGroup>()
        } catch (e: Exception) {
            null
        }
    }

    // 가족 그룹 삭제
    suspend fun deleteFamilyGroup(groupId: String): Boolean {
        val uid = auth.currentUser?.uid ?: return false

        try {
            val group = getFamilyGroupById(groupId) ?: return false
            if (group.ownerUserId != uid) return false

            familyGroupsCollection.document(groupId).delete().await()
            return true
        } catch (e: Exception) {
            return false
        }
    }
}