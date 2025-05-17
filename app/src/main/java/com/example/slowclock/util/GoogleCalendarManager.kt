// GoogleCalendarManager.kt
package com.example.slowclock.util

import android.content.Context
import android.util.Log
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import com.google.api.client.util.DateTime
import com.google.api.services.calendar.Calendar
import com.google.api.services.calendar.CalendarScopes
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Collections

class GoogleCalendarManager(private val context: Context) {
    private val scope = Collections.singletonList(CalendarScopes.CALENDAR)

    // Google 로그인 옵션 설정
    private val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestEmail()
        .requestScopes(com.google.android.gms.common.api.Scope(CalendarScopes.CALENDAR))
        .build()

    // 구글 캘린더 서비스 생성
    private fun getCalendarService(account: GoogleSignInAccount): Calendar {
        val credential = GoogleAccountCredential.usingOAuth2(
            context, scope
        )
        credential.selectedAccount = account.account

        return Calendar.Builder(
            NetHttpTransport(),
            GsonFactory.getDefaultInstance(),
            credential
        )
            .setApplicationName("SlowClock")
            .build()
    }

    // 오늘 일정 가져오기 테스트
    suspend fun fetchTodayEvents(): List<String> {
        val account = GoogleSignIn.getLastSignedInAccount(context) ?: return emptyList()

        return withContext(Dispatchers.IO) {
            try {
                val service = getCalendarService(account)

                val now = DateTime(System.currentTimeMillis())
                val midnight = DateTime(java.util.Calendar.getInstance().apply {
                    set(java.util.Calendar.HOUR_OF_DAY, 23)
                    set(java.util.Calendar.MINUTE, 59)
                    set(java.util.Calendar.SECOND, 59)
                }.timeInMillis)

                val events = service.events().list("primary")
                    .setTimeMin(now)
                    .setTimeMax(midnight)
                    .setOrderBy("startTime")
                    .setSingleEvents(true)
                    .execute()

                Log.d("GoogleCalendar", "일정 ${events.items.size}개 가져옴")

                // 일정 제목만 반환
                events.items.map { it.summary }
            } catch (e: Exception) {
                Log.e("GoogleCalendar", "일정 가져오기 실패: ${e.message}")
                emptyList()
            }
        }
    }
}