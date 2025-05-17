package com.example.slowclock.util

import android.content.Context
import android.util.Log
import com.example.slowclock.domain.usecase.GenerateScheduleRecommendationUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object VertexAITestUtil {
    fun testRecommendation(context: Context) {
        val useCase = GenerateScheduleRecommendationUseCase(context)

        CoroutineScope(Dispatchers.Main).launch {
            try {
                val recommendations = useCase("고령자", "월요일", "오전")
                Log.d("VertexAI_SLOWCLOCK", "추천 결과: $recommendations")
            } catch (e: Exception) {
                Log.e("VertexAI_SLOWCLOCK", "테스트 실패", e)
            }
        }
    }
}