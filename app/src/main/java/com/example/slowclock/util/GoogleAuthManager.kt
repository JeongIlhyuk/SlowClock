package com.example.slowclock.util

import android.app.Activity
import android.content.Intent
import android.util.Log
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.api.services.calendar.CalendarScopes

class GoogleAuthManager(private val activity: Activity) {
    private val TAG = "Auth_SLOWCLOCK"
    private val RC_SIGN_IN = 9001

    fun setupGoogleSignIn(): GoogleSignInClient {
        // 구글 로그인 클라이언트 설정
        return GoogleSignIn.getClient(
            activity,
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestScopes(com.google.android.gms.common.api.Scope(CalendarScopes.CALENDAR))
                .build()
        )
    }

    fun signIn() {
        val signInClient = setupGoogleSignIn()
        val signInIntent = signInClient.signInIntent
        activity.startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    fun handleSignInResult(requestCode: Int, resultCode: Int, data: Intent?): GoogleSignInAccount? {
        // 로그인 결과 처리 코드
        if (requestCode == RC_SIGN_IN && resultCode == Activity.RESULT_OK && data != null) {
            try {
                val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                return task.getResult(ApiException::class.java)
            } catch (e: ApiException) {
                Log.e(TAG, "로그인 실패: ${e.statusCode}", e)
            }
        }
        return null
    }
}