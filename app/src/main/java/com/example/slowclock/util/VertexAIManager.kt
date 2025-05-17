package com.example.slowclock.util

import android.content.Context
import android.util.Log
import com.example.slowclock.R
import com.example.slowclock.data.api.GenerateContentRequest
import com.example.slowclock.data.api.Instance
import com.example.slowclock.data.api.Parameters
import com.example.slowclock.data.api.VertexAIService
import com.google.auth.oauth2.GoogleCredentials
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.InputStream

object VertexAIManager {
    private const val TAG = "VertexAI_SLOWCLOCK"
    private const val BASE_URL = "https://us-central1-aiplatform.googleapis.com/"
    private const val PROJECT_ID = "slow-clock-scheduler"
    private const val LOCATION = "us-central1"

    private lateinit var applicationContext: Context

    // 초기화 함수 수정
    fun initialize(context: Context) {
        this.applicationContext = context.applicationContext
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

    // 서비스 계정 기반 인증으로 변경
    private suspend fun getAuthToken(): String = withContext(Dispatchers.IO) {
        try {
            // raw 폴더에서 서비스 계정 키 파일 읽기
            val inputStream: InputStream =
                applicationContext.resources.openRawResource(R.raw.service_account)

            // 인증 정보 생성
            val credentials = GoogleCredentials.fromStream(inputStream)
                .createScoped("https://www.googleapis.com/auth/cloud-platform")

            // 토큰 가져오기
            credentials.refreshIfExpired()
            val token = credentials.accessToken.tokenValue

            Log.d(TAG, "서비스 계정 토큰 발급 완료")
            return@withContext "Bearer $token"
        } catch (e: Exception) {
            Log.e(TAG, "서비스 계정 인증 실패: ${e.message}", e)
            throw e
        }
    }

    // 나머지 코드는 그대로 유지...
    suspend fun generateScheduleRecommendations(
        userType: String,
        count: Int = 5
    ): List<String> = withContext(Dispatchers.IO) {
        try {
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