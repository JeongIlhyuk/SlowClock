// auth/AuthManager.kt
package com.example.slowclock.util.auth

import android.app.Activity
import android.content.Intent
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth

class AuthManager(private val activity: ComponentActivity) {
    private val firebaseAuth = FirebaseAuth.getInstance()
    private lateinit var signInLauncher: ActivityResultLauncher<Intent>

    fun initialize(onSuccess: () -> Unit, onError: (String) -> Unit = {}) {
        signInLauncher = activity.registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            val response = IdpResponse.fromResultIntent(result.data)

            if (result.resultCode == Activity.RESULT_OK) {
                val user = firebaseAuth.currentUser
                Log.d("AUTH", "로그인 성공: ${user?.displayName} (${user?.email})")
                onSuccess()
            } else {
                val error = response?.error?.message ?: "로그인이 취소되었습니다"
                Log.e("AUTH", "로그인 실패: $error")
                onError(error)
            }
        }
    }

    fun getCurrentUser() = firebaseAuth.currentUser

    fun signInWithGoogle() {
        try {
            val providers = arrayListOf(
                AuthUI.IdpConfig.GoogleBuilder()
                    .setScopes(listOf("https://www.googleapis.com/auth/calendar"))
                    .build()
            )

            val signInIntent = AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .setTosAndPrivacyPolicyUrls(
                    "https://example.com/terms.html",
                    "https://example.com/privacy.html"
                )
                .build()

            Log.d("AUTH", "구글 로그인 시작")
            signInLauncher.launch(signInIntent)
        } catch (e: Exception) {
            Log.e("AUTH", "구글 로그인 시작 실패", e)
        }
    }

//    fun signOut(context: Context, onComplete: () -> Unit = {}) {
//        AuthUI.getInstance()
//            .signOut(context)
//            .addOnCompleteListener {
//                Log.d("AUTH", "로그아웃 완료")
//                onComplete()
//            }
//            .addOnFailureListener { e ->
//                Log.e("AUTH", "로그아웃 실패", e)
//                onComplete() // 실패해도 콜백 호출
//            }
//    }
}