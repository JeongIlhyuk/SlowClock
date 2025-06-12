// MainActivity.kt
package com.example.slowclock

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.slowclock.auth.AuthManager
import com.example.slowclock.data.DummyDataManager
import com.example.slowclock.navigation.AppNavigation
import com.example.slowclock.notification.ForegroundService
import com.example.slowclock.notification.requestExactAlarmPermissionIfNeeded
import com.example.slowclock.ui.familygroup.FamilyGroupManageScreen
import com.example.slowclock.ui.familygroup.FamilyGroupViewModel
import com.example.slowclock.ui.theme.SlowClockTheme
import kotlinx.coroutines.launch
import android.media.AudioAttributes
import android.media.RingtoneManager
import com.google.firebase.messaging.FirebaseMessaging

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
                    // FCM 토큰을 Firestore에 저장
                    saveFcmTokenToFirestore()
                },
                onError = { error ->
                    Log.e("AUTH", "로그인 실패: $error")
                }
            )

// 로그인 상태 확인
            val currentUser = this.authManager.getCurrentUser()
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
        requestNotificationPermission() // 알림 권한 요청
        requestExactAlarmPermissionIfNeeded(this) // 알림 권한 요청
        createNotificationChannel() // ← 반드시 호출 필요

        // NotificationChannel 생성  ForegroundService에서 알림 사용을 위해
        val serviceIntent = Intent(this, ForegroundService::class.java)
        ContextCompat.startForegroundService(this, serviceIntent)
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
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "일정 알림"
            val descriptionText = "일정 시간에 울리는 알림"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
            val audioAttributes = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()

            val channel = NotificationChannel("schedule_channel", name, importance).apply {
                description = descriptionText
                enableLights(true)
                enableVibration(true)
                setSound(soundUri, audioAttributes) // 🔊 사운드 설정 추가
            }

            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissions(
                    arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                    1001
                )
            }
        }
    }
    private fun saveFcmTokenToFirestore() {
        val user = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser
        if (user != null) {
            FirebaseMessaging.getInstance().token.addOnSuccessListener { token ->
                val db = com.google.firebase.firestore.FirebaseFirestore.getInstance()
                db.collection("users").document(user.uid)
                    .update("fcmToken", token)
                    .addOnSuccessListener { Log.d("FCM", "로그인 후 토큰 Firestore 저장 성공") }
                    .addOnFailureListener { e -> Log.e("FCM", "로그인 후 토큰 Firestore 저장 실패", e) }
            }
        }
    }

}