package com.example.slowclock

import android.content.Context
import android.util.Log
import com.example.slowclock.data.api.Content
import com.example.slowclock.data.api.GenerateContentRequest
import com.example.slowclock.data.api.GenerationConfig
import com.example.slowclock.data.api.Part
import com.example.slowclock.data.api.VertexAIService
import com.example.slowclock.data.api.VertexAIServiceFactory
import com.google.auth.oauth2.ServiceAccountCredentials
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException

class VertexAIManager(private val context: Context) {
    private val TAG = "VertexAI_SLOWCLOCK"
    private val vertexAIService: VertexAIService = VertexAIServiceFactory.create()

    suspend fun generateScheduleRecommendation(
        projectId: String,
        location: String,
        prompt: String
    ): String? {
        val modelId = "gemini-2.5-flash-preview-04-17"
        return try {
            // Load service account credentials from raw resource
            val credentials = ServiceAccountCredentials.fromStream(
                context.resources.openRawResource(R.raw.service_account)
            ).createScoped(listOf("https://www.googleapis.com/auth/cloud-platform"))
            credentials.refresh()
            val token = "Bearer ${credentials.accessToken.tokenValue}"

            val response = withContext(Dispatchers.IO) {
                vertexAIService.generateContent(
                    projectId = projectId,
                    location = location,
                    modelId = modelId,
                    request = GenerateContentRequest(
                        contents = listOf(
                            Content(role = "user", parts = listOf(Part(text = prompt)))
                        ),
                        generationConfig = GenerationConfig(
                            maxOutputTokens = 256,
                            temperature = 0.2f
                        )
                    ),
                    auth = token
                )
            }
            response.candidates.firstOrNull()?.content?.parts
                ?.joinToString("\n") { it.text }
        } catch (e: HttpException) {
            Log.e(TAG, "일정 추천 생성 실패: HTTP ${e.code()}", e)
            null
        } catch (e: Exception) {
            Log.e(TAG, "일정 추천 생성 실패: ${e.message}", e)
            null
        }
    }
}