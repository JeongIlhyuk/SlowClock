package com.example.slowclock

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
import com.example.slowclock.ui.theme.SlowClockTheme
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.api.services.calendar.CalendarScopes
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging

private const val RC_SIGN_IN = 9001

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Firestore 연동 테스트
        FirebaseFirestore.getInstance().collection("test_collection")
            .document("test_doc")
            .set(hashMapOf("test_field" to "test_value", "timestamp" to Timestamp.now()))
            .addOnSuccessListener {
                Log.d("Firestore", "데이터 쓰기 성공")

                // 데이터 읽기 테스트
                FirebaseFirestore.getInstance().collection("test_collection")
                    .document("test_doc")
                    .get()
                    .addOnSuccessListener { document ->
                        if (document.exists()) {
                            Log.d("Firestore", "데이터 읽기 성공: ${document.data}")
                        } else {
                            Log.d("Firestore", "문서가 없음")
                        }
                    }
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "데이터 쓰기 실패: ${e.message}")
            }

        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val token = task.result
                Log.d("FCM", "FCM 토큰: $token")
                // 이 토큰을 백엔드에 저장하면 나중에 알림 보낼 때 사용 가능
            } else {
                Log.e("FCM", "FCM 토큰 얻기 실패", task.exception)
            }
        }

        // 구글 로그인 처리
        val signInClient = GoogleSignIn.getClient(
            this, GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestScopes(com.google.android.gms.common.api.Scope(CalendarScopes.CALENDAR))
                .build()
        )

        // 로그인 버튼 클릭 리스너 등에서 호출
        val signInIntent = signInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN) // RC_SIGN_IN은 상수 (ex: 9001)

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