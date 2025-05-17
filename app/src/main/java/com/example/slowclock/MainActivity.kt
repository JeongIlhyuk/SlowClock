package com.example.slowclock

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.lifecycleScope
import com.example.slowclock.ui.theme.SlowClockTheme
import com.example.slowclock.util.FCMManager
import com.example.slowclock.util.FirestoreTestUtil
import com.example.slowclock.util.GoogleAuthManager
import com.example.slowclock.util.GoogleCalendarManager
import kotlinx.coroutines.launch

private const val RC_SIGN_IN = 9001

class MainActivity : ComponentActivity() {
    private lateinit var calendarManager: GoogleCalendarManager
    private lateinit var authManager: GoogleAuthManager

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

        // 테스트 코드 실행
        FirestoreTestUtil.testFirestore()
        FCMManager.getToken()

        // 구글 로그인
        authManager.signIn()

        enableEdgeToEdge()
        setContent {
            SlowClockTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    SlowClockTheme {
        Greeting("Android")
    }
}