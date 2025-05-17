package com.example.slowclock.util

import android.util.Log
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore

object FirestoreTestUtil {
    private const val TAG = "DB_SLOWCLOCK"
    fun testFirestore() {
        // Firestore 테스트 코드
        FirebaseFirestore.getInstance().collection("test_collection")
            .document("test_doc")
            .set(hashMapOf("test_field" to "test_value", "timestamp" to Timestamp.now()))
            .addOnSuccessListener {
                Log.d(TAG, "데이터 쓰기 성공")
                readTestData()
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "데이터 쓰기 실패: ${e.message}")
            }
    }

    private fun readTestData() {
        FirebaseFirestore.getInstance().collection("test_collection")
            .document("test_doc")
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    Log.d(TAG, "데이터 읽기 성공: ${document.data}")
                } else {
                    Log.d(TAG, "문서가 없음")
                }
            }
    }
}