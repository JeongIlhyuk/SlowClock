package com.example.slowclock.data.api

import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

// API 인터페이스 정의
interface VertexAIService {
    @POST("v1/projects/{projectId}/locations/{location}/publishers/google/models/{modelId}:predict")
    suspend fun generateContent(
        @Path("projectId") projectId: String,
        @Path("location") location: String,
        @Path("modelId") modelId: String,
        @Body request: GenerateContentRequest,
        @Header("Authorization") auth: String // Firebase Auth 토큰 사용
    ): GenerateContentResponse
}

// 요청/응답 모델 클래스들
data class GenerateContentRequest(val instances: List<Instance>, val parameters: Parameters)
data class Instance(val prompt: String)
data class Parameters(val maxOutputTokens: Int, val temperature: Float)
data class GenerateContentResponse(val predictions: List<Prediction>)
data class Prediction(val content: String)