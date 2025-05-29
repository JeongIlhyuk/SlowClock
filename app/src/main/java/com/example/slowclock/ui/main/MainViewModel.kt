package com.example.slowclock.ui.main

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
    val error: String? = null
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

                val currentSchedule = schedules.firstOrNull { schedule ->
                    !schedule.isCompleted &&
                            schedule.startTime.toDate().time <= currentTime &&
                            (schedule.endTime?.toDate()?.time ?: Long.MAX_VALUE) > currentTime
                }

                _uiState.value = MainUiState(
                    todaySchedules = schedules,
                    currentSchedule = currentSchedule,
                    completedCount = schedules.count { it.isCompleted },
                    totalCount = schedules.size,
                    isLoading = false
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "일정을 불러오는데 실패했습니다"
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
}