package com.example.slowclock

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.lifecycleScope
import com.example.slowclock.data.model.Schedule
import com.example.slowclock.data.repository.ScheduleRepository
import com.example.slowclock.ui.main.MainScreen
import com.example.slowclock.ui.theme.SlowClockTheme
import com.example.slowclock.util.GoogleAuthManager
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.launch
import java.util.Calendar

class MainActivity : ComponentActivity() {
    private lateinit var authManager: GoogleAuthManager

    @Deprecated("This method has been deprecated")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        Log.d("AUTH", "onActivityResult 호출됨")
        super.onActivityResult(requestCode, resultCode, data)

        val account = authManager.handleSignInResult(requestCode, resultCode, data)
        if (account != null) {
            Log.d("AUTH", "로그인 성공: ${account.displayName}")

            val credential = GoogleAuthProvider.getCredential(account.idToken, null)
            FirebaseAuth.getInstance().signInWithCredential(credential)
                .addOnSuccessListener {
                    Log.d("AUTH", "Firebase 연결 성공")
                    addDummyData() // 더미 데이터 추가
                }
                .addOnFailureListener { e ->
                    Log.e("AUTH", "Firebase 연결 실패", e)
                }
        } else {
            Log.e("AUTH", "구글 로그인 실패")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("MAIN", "onCreate 시작")

        try {
            authManager = GoogleAuthManager(this)
            Log.d("MAIN", "AuthManager 초기화 완료")

            val currentUser = FirebaseAuth.getInstance().currentUser
            if (currentUser == null) {
                Log.d("AUTH", "로그인 필요 - 구글 로그인 시작")
                authManager.signIn()
            } else {
                Log.d("AUTH", "이미 로그인됨: ${currentUser.uid}")
                addDummyData() // 이미 로그인된 상태에서도 더미 데이터 체크
            }

            enableEdgeToEdge()
            setContent {
                SlowClockTheme {
                    MainScreen()
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
                val repo = ScheduleRepository()
                val existing = repo.getTodaySchedules()

                if (existing.isEmpty()) {
                    Log.d("DUMMY", "일정 없음, 더미 데이터 추가")

                    val schedules = listOf(
                        Schedule(title = "아침 운동", startTime = getTodayTime(9, 0)),
                        Schedule(
                            title = "점심 약속",
                            startTime = getTodayTime(12, 30),
                            isCompleted = true
                        ),
                        Schedule(title = "저녁 산책", startTime = getTodayTime(18, 0)),
                        Schedule(title = "지금 할 일", startTime = Timestamp.now())
                    )

                    schedules.forEach {
                        repo.addSchedule(it)
                    }
                    Log.d("DUMMY", "더미 데이터 추가 완료")
                } else {
                    Log.d("DUMMY", "이미 일정 있음: ${existing.size}개")
                }
            } catch (e: Exception) {
                Log.e("DUMMY", "더미 데이터 추가 실패", e)
            }
        }
    }

    private fun getTodayTime(hour: Int, minute: Int): Timestamp {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, hour)
        calendar.set(Calendar.MINUTE, minute)
        calendar.set(Calendar.SECOND, 0)
        return Timestamp(calendar.time)
    }
}