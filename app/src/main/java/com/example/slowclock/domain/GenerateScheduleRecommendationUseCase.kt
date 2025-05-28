package com.example.slowclock.domain

import android.content.Context
import com.example.slowclock.data.repository.VertexAIRepository

class GenerateScheduleRecommendationUseCase(
    context: Context
) {
    private val repository = VertexAIRepository(context)

    suspend operator fun invoke(userType: String, day: String, timeOfDay: String): String? {
        val prompt = """
            다음 사용자를 위한 일정 추천을 3개 제안해주세요:
            - 사용자 유형: $userType
            - 요일: $day
            - 시간대: $timeOfDay
        """.trimIndent()

        return repository.generateScheduleRecommendation(
            prompt = prompt
        )
    }
}