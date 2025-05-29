// app/src/main/java/com/example/slowclock/ui/main/MainViewModel.kt
package com.example.slowclock.ui.main

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.slowclock.data.model.Schedule
import com.example.slowclock.data.repository.ScheduleRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class MainUiState(
    val todaySchedules: List<Schedule> = emptyList(),
    val currentSchedule: Schedule? = null,
    val completedCount: Int = 0,
    val totalCount: Int = 0,
    val isLoading: Boolean = false,
    val error: String? = null,
    val selectedScheduleForDetail: Schedule? = null
)

class MainViewModel : ViewModel() {
    private val scheduleRepository = ScheduleRepository()

    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    init {
        loadTodaySchedules()
    }

    fun loadTodaySchedules() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                val schedules = scheduleRepository.getTodaySchedules()
                val currentTime = System.currentTimeMillis()

                val currentSchedule = schedules
                    .filter { !it.isCompleted }
                    .let { incompleteSchedules ->
                        // 1단계: 현재 진행 중인 일정들
                        val ongoingSchedules = incompleteSchedules.filter { schedule ->
                            val startTime = schedule.startTime.toDate().time
                            val endTime =
                                schedule.endTime?.toDate()?.time ?: (startTime + 60 * 60 * 1000)
                            currentTime >= startTime && currentTime <= endTime
                        }

                        if (ongoingSchedules.isNotEmpty()) {
                            // 진행 중: 끝나는 시간 빠른 순
                            ongoingSchedules.minByOrNull { schedule ->
                                schedule.endTime?.toDate()?.time
                                    ?: (schedule.startTime.toDate().time + 60 * 60 * 1000)
                            }
                        } else {
                            // 진행 중 없음: 시작 시간 빠른 순 → 끝나는 시간 빠른 순
                            incompleteSchedules
                                .filter { it.startTime.toDate().time > currentTime }
                                .sortedWith(
                                    compareBy<Schedule> { it.startTime.toDate().time }
                                        .thenBy { schedule ->
                                            schedule.endTime?.toDate()?.time
                                                ?: (schedule.startTime.toDate().time + 60 * 60 * 1000)
                                        }
                                )
                                .firstOrNull()
                        }
                    }

                _uiState.value = MainUiState(
                    todaySchedules = schedules,
                    currentSchedule = currentSchedule,
                    completedCount = schedules.count { it.isCompleted },
                    totalCount = schedules.size,
                    isLoading = false
                )
            } catch (e: Exception) {
                Log.e("MainViewModel", "일정 로드 실패", e)
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "일정을 불러오는데 실패했습니다: ${e.message}"
                )
            }
        }
    }

    fun toggleScheduleComplete(scheduleId: String) {
        viewModelScope.launch {
            val schedule = _uiState.value.todaySchedules.find { it.id == scheduleId }
            schedule?.let {
                val success = scheduleRepository.markScheduleAsCompleted(
                    scheduleId,
                    !it.isCompleted
                )
                if (success) {
                    // 즉시 UI 업데이트 (낙관적 업데이트)
                    val updatedSchedules = _uiState.value.todaySchedules.map { s ->
                        if (s.id == scheduleId) s.copy(isCompleted = !s.isCompleted) else s
                    }

                    val currentTime = System.currentTimeMillis()
                    val currentSchedule = updatedSchedules.firstOrNull { schedule ->
                        !schedule.isCompleted &&
                                schedule.startTime.toDate().time <= currentTime &&
                                (schedule.endTime?.toDate()?.time ?: Long.MAX_VALUE) > currentTime
                    }

                    _uiState.value = _uiState.value.copy(
                        todaySchedules = updatedSchedules,
                        currentSchedule = currentSchedule,
                        completedCount = updatedSchedules.count { it.isCompleted }
                    )
                }
            }
        }
    }

    fun showScheduleDetail(scheduleId: String) {
        val schedule = _uiState.value.todaySchedules.find { it.id == scheduleId }
        _uiState.value = _uiState.value.copy(selectedScheduleForDetail = schedule)
    }

    fun hideScheduleDetail() {
        _uiState.value = _uiState.value.copy(selectedScheduleForDetail = null)
    }
}