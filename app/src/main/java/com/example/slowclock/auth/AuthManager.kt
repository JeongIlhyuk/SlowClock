package com.example.slowclock.auth

import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.api.services.calendar.CalendarScopes
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

class AuthManager(private val activity: ComponentActivity) {
    private val firebaseAuth = FirebaseAuth.getInstance()
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var signInLauncher: ActivityResultLauncher<android.content.Intent>

    fun initialize(onSuccess: () -> Unit) {
        // Google Sign-In 클라이언트 설정
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(activity.getString(android.R.string.ok)) // 실제로는 웹 클라이언트 ID 필요
            .requestEmail()
            .requestScopes(com.google.android.gms.common.api.Scope(CalendarScopes.CALENDAR))
            .build()

        googleSignInClient = GoogleSignIn.getClient(activity, gso)

        // 최신 Activity Result API 사용
        signInLauncher = activity.registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            try {
                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                val account = task.getResult(ApiException::class.java)

                Log.d("AUTH", "로그인 성공: ${account.displayName}")

                val credential = GoogleAuthProvider.getCredential(account.idToken, null)
                firebaseAuth.signInWithCredential(credential)
                    .addOnSuccessListener {
                        Log.d("AUTH", "Firebase 연결 성공")
                        onSuccess()
                    }
                    .addOnFailureListener { e ->
                        Log.e("AUTH", "Firebase 연결 실패", e)
                    }

            } catch (e: ApiException) {
                Log.e("AUTH", "구글 로그인 실패: ${e.statusCode}", e)
            }
        }
    }

    fun getCurrentUser() = firebaseAuth.currentUser

    fun signInWithGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        signInLauncher.launch(signInIntent)
    }
}