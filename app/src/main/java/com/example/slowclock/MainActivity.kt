package com.example.slowclock

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.example.slowclock.data.repository.VertexAIRepository
import com.example.slowclock.domain.usecase.GenerateScheduleRecommendationUseCase
import com.example.slowclock.ui.theme.SlowClockTheme
import com.example.slowclock.util.FCMManager
import com.example.slowclock.util.FirestoreTestUtil
import com.example.slowclock.util.GoogleAuthManager
import com.example.slowclock.util.GoogleCalendarManager
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private lateinit var calendarManager: GoogleCalendarManager
    private lateinit var authManager: GoogleAuthManager
    private lateinit var vertexAIManager: VertexAIRepository

    @Deprecated("This method has been deprecated in favor of using the Activity Result API\n      which brings increased type safety via an {@link ActivityResultContract} and the prebuilt\n      contracts for common intents available in\n      {@link androidx.activity.result.contract.ActivityResultContracts}, provides hooks for\n      testing, and allow receiving results in separate, testable classes independent from your\n      activity. Use\n      {@link #registerForActivityResult(ActivityResultContract, ActivityResultCallback)}\n      with the appropriate {@link ActivityResultContract} and handling the result in the\n      {@link ActivityResultCallback#onActivityResult(Object) callback}.")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val TAG = "Auth_SLOWCLOCK"
        super.onActivityResult(requestCode, resultCode, data)

        val account = authManager.handleSignInResult(requestCode, resultCode, data)
        if (account != null) {
            Log.d(TAG, "로그인 성공: ${account.displayName}, ${account.email}")
            lifecycleScope.launch {
                calendarManager.fetchAllEvents()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

                // 매니저 초기화
        calendarManager = GoogleCalendarManager(this)
        authManager = GoogleAuthManager(this)

        vertexAIManager = VertexAIRepository(this)

        // 테스트 코드 실행
        FirestoreTestUtil.testFirestore()
        FCMManager.getToken()
        authManager.signIn()

        val useCase = GenerateScheduleRecommendationUseCase(this)
            lifecycleScope.launch {
                try {
                    val recommendations = useCase("고령자", "월요일", "오전")
                    Log.d("VertexAI_SLOWCLOCK", "추천 결과: $recommendations")
                } catch (e: Exception) {
                    Log.e("VertexAI_SLOWCLOCK", "테스트 실패", e)
                }
            }

        enableEdgeToEdge()
        setContent {
            SlowClockTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Button(
                        onClick = {
                            val intent = Intent(this@MainActivity, AITestActivity::class.java)
                            startActivity(intent)
                        },
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text("AI 테스트 열기")
                    }
                }
            }
        }
    }
}