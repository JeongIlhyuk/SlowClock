package com.example.slowclock

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.lifecycleScope
import com.example.slowclock.auth.AuthManager
import com.example.slowclock.data.DummyDataManager
import com.example.slowclock.navigation.AppNavigation
import com.example.slowclock.ui.theme.SlowClockTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private lateinit var authManager: AuthManager
    private val dummyDataManager = DummyDataManager()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("MAIN", "onCreate 시작")

        try {
            authManager = AuthManager(this)

            // AuthManager 초기화 (콜백과 함께)
            authManager.initialize {
                addDummyData()
            }

            val currentUser = authManager.getCurrentUser()
            Log.d("AUTH", "현재 사용자 상태: ${currentUser?.uid}")

            if (currentUser == null) {
                Log.d("AUTH", "로그인 필요 - 구글 로그인 시작")
                authManager.signInWithGoogle()
            } else {
                Log.d("AUTH", "이미 로그인됨: ${currentUser.uid}")
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
            dummyDataManager.addDummyDataIfNeeded()
        }
    }
}