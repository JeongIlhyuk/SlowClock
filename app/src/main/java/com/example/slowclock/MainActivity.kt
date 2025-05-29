// MainActivity.kt
package com.example.slowclock

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.lifecycleScope
import com.example.slowclock.data.DummyDataManager
import com.example.slowclock.navigation.AppNavigation
import com.example.slowclock.ui.theme.SlowClockTheme
import com.example.slowclock.util.auth.AuthManager
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private lateinit var authManager: AuthManager
    private val dummyDataManager = DummyDataManager()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("MAIN", "onCreate 시작")

        try {
            // AuthManager 초기화
            authManager = AuthManager(this)
            authManager.initialize(
                onSuccess = {
                    Log.d("AUTH", "로그인 성공 콜백")
                    addDummyData()
                },
                onError = { error ->
                    Log.e("AUTH", "로그인 실패: $error")
                    // 에러 처리하지만 앱은 계속 실행
                }
            )

            // 현재 로그인 상태 확인
            val currentUser = authManager.getCurrentUser()
            Log.d("AUTH", "현재 사용자: ${currentUser?.uid}")

            if (currentUser == null) {
                Log.d("AUTH", "로그인 필요 - 구글 로그인 시작")
                authManager.signInWithGoogle()
            } else {
                Log.d("AUTH", "이미 로그인됨: ${currentUser.displayName}")
                addDummyData()
            }

            enableEdgeToEdge()
            setContent {
                SlowClockTheme {
                    AppNavigation()
                }
            }
            Log.d("MAIN", "onCreate 완료")
        } catch (e: Exception) {
            Log.e("MAIN", "onCreate 실패", e)
        }
    }

    private fun addDummyData() {
        lifecycleScope.launch {
            try {
                dummyDataManager.addDummyDataIfNeeded()
                Log.d("MAIN", "더미 데이터 처리 완료")
            } catch (e: Exception) {
                Log.e("MAIN", "더미 데이터 처리 실패", e)
            }
        }
    }
}