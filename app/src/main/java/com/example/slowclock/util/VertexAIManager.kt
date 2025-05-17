package com.example.slowclock.util

import android.util.Log
import com.example.slowclock.data.api.GenerateContentRequest
import com.example.slowclock.data.api.Instance
import com.example.slowclock.data.api.Parameters
import com.example.slowclock.data.api.VertexAIService
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object VertexAIManager {
    private const val TAG = "VertexAI_SLOWCLOCK"
    private const val BASE_URL = "https://us-central1-aiplatform.googleapis.com/"
    private const val PROJECT_ID = "slow-clock-scheduler"
    private const val LOCATION = "us-central1"

    // 초기화 - 단순화
    fun initialize() {
        // Firebase는 이미 앱 시작 시 초기화되어 있음
    }

    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private val apiService by lazy {
        retrofit.create(VertexAIService::class.java)
    }

    // Firebase Auth 토큰 가져오기
    private suspend fun getAuthToken(): String = withContext(Dispatchers.IO) {
        try {
            val user = FirebaseAuth.getInstance().currentUser
                ?: throw IllegalStateException("사용자가 로그인되어 있지 않습니다")

            val tokenResult = user.getIdToken(false).await()
            return@withContext "Bearer ${tokenResult.token}"
        } catch (e: Exception) {
            Log.e(TAG, "인증 토큰 가져오기 실패: ${e.message}", e)
            throw e
        }
    }

    // 주요 메서드 - AI 일정 추천 생성
    suspend fun generateScheduleRecommendations(
        userType: String,
        count: Int = 5
    ): List<String> = withContext(Dispatchers.IO) {
        try {
            // Firebase 인증 토큰 가져오기
            val token = getAuthToken()

            val prompt = """
                사용자 유형: $userType
                
                위 정보를 바탕으로 이 사용자에게 적합한 일정 ${count}개를 추천해주세요.
                각 일정은 제목만 간결하게 한 줄로 작성하고, 줄바꿈으로 구분해주세요.
            """.trimIndent()

            val response = apiService.generateContent(
                projectId = PROJECT_ID,
                location = LOCATION,
                modelId = "text-bison",
                request = GenerateContentRequest(
                    instances = listOf(Instance(prompt = prompt)),
                    parameters = Parameters(
                        maxOutputTokens = 256,
                        temperature = 0.2f
                    )
                ),
                auth = token
            )

            // 응답에서 추천 항목 파싱
            return@withContext response.predictions.firstOrNull()?.content
                ?.split("\n")
                ?.filter { it.isNotBlank() }
                ?: emptyList()

        } catch (e: Exception) {
            Log.e(TAG, "일정 추천 생성 실패: ${e.message}", e)
            return@withContext emptyList()
        }
    }
}