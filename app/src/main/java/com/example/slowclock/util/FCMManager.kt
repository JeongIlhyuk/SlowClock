package com.example.slowclock.util

import android.util.Log
import com.google.firebase.messaging.FirebaseMessaging

object FCMManager {
    private const val TAG = "FCM_SLOWCLOCK"

    fun getToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val token = task.result
                Log.d(TAG, "FCM 토큰: $token")
                // 이 토큰을 백엔드에 저장하면 나중에 알림 보낼 때 사용 가능
            } else {
                Log.e(TAG, "FCM 토큰 얻기 실패", task.exception)
            }
        }
    }
}