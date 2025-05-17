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
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Firestore 연동 테스트
        FirebaseFirestore.getInstance().collection("test")
            .document("test")
            .get()
            .addOnSuccessListener {
                Log.d("Firebase", "연결 성공")
            }
            .addOnFailureListener {
                Log.e("Firebase", "연결 실패: ${it.message}")
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