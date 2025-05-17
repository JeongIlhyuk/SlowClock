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
import com.google.api.services.calendar.Calendar
import com.google.api.services.calendar.CalendarScopes
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Collections

class GoogleCalendarManager(private val context: Context) {
    private val TAG = "Calender_SLOWCLOCK"
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

    suspend fun fetchAllEvents(maxResults: Int = 100): List<String> {
        val account = GoogleSignIn.getLastSignedInAccount(context) ?: return emptyList()

        return withContext(Dispatchers.IO) {
            try {
                val service = getCalendarService(account)

                val events = service.events().list("primary")
                    .setMaxResults(maxResults)
                    .setSingleEvents(false)  // 반복 일정을 하나로 처리
                    .execute()

                // 중복 제거해서 로그
                val uniqueEvents = events.items.distinctBy { it.summary }
                Log.d(
                    TAG,
                    "전체 일정 ${events.items.size}개 (중복 제거 후 ${uniqueEvents.size}개)"
                )
                Log.d(TAG, "중복 제거된 일정 목록: ${uniqueEvents.map { it.summary }}")

                // 반환은 원본 그대로 하거나 중복 제거
                events.items.map { it.summary }
            } catch (e: Exception) {
                Log.e(TAG, "일정 가져오기 실패: ${e.message}")
                emptyList()
            }
        }
    }
}