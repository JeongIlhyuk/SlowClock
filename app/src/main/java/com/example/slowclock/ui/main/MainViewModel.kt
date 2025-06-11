// app/src/main/java/com/example/slowclock/ui/main/MainViewModel.kt
package com.example.slowclock.ui.main

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.slowclock.data.model.Schedule
import com.example.slowclock.data.remote.repository.ScheduleRepository
import com.example.slowclock.util.AppError
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
    val error: AppError? = null, // String → AppError 변경
    val selectedScheduleForDetail: Schedule? = null,
    val canRetry: Boolean = false, // 재시도 가능 여부 추가
    val showDeleteConfirmDialog: Boolean = false,
    val scheduleToDelete: Schedule? = null
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
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                error = null,
                canRetry = false
            )

            try {
                when (val result = scheduleRepository.getTodaySchedules()) {
                    is ScheduleRepository.ScheduleResult.Success -> {
                        val schedules = result.data
                        val currentTime = System.currentTimeMillis()

                        val currentSchedule = calculateCurrentSchedule(schedules, currentTime)

                        _uiState.value = MainUiState(
                            todaySchedules = schedules,
                            currentSchedule = currentSchedule,
                            completedCount = schedules.count { it.isCompleted },
                            totalCount = schedules.size,
                            isLoading = false
                        )

                        Log.d("MainViewModel", "일정 로드 성공: ${schedules.size}개")
                    }

                    is ScheduleRepository.ScheduleResult.Error -> {
                        Log.e("MainViewModel", "일정 로드 실패: ${result.error.message}")
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = result.error,
                            canRetry = true // 재시도 가능
                        )
                    }
                }
            } catch (e: Exception) {
                Log.e("MainViewModel", "예상치 못한 에러", e)
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = AppError.GeneralError("일정을 불러오는 중 문제가 발생했습니다"),
                    canRetry = true
                )
            }
        }
    }

    fun toggleScheduleComplete(scheduleId: String) {
        viewModelScope.launch {
            val schedule = _uiState.value.todaySchedules.find { it.id == scheduleId }
            schedule?.let {
                // 낙관적 업데이트 (즉시 UI 변경)
                val updatedSchedules = _uiState.value.todaySchedules.map { s ->
                    if (s.id == scheduleId) s.copy(isCompleted = !s.isCompleted) else s
                }

                val currentTime = System.currentTimeMillis()
                val currentSchedule = calculateCurrentSchedule(updatedSchedules, currentTime)

                _uiState.value = _uiState.value.copy(
                    todaySchedules = updatedSchedules,
                    currentSchedule = currentSchedule,
                    completedCount = updatedSchedules.count { it.isCompleted }
                )

                // 서버 업데이트
                when (val result =
                    scheduleRepository.markScheduleAsCompleted(scheduleId, !it.isCompleted)) {
                    is ScheduleRepository.ScheduleResult.Success -> {
                        Log.d("MainViewModel", "완료 상태 변경 성공")
                    }

                    is ScheduleRepository.ScheduleResult.Error -> {
                        Log.e("MainViewModel", "완료 상태 변경 실패: ${result.error.message}")
                        // 실패 시 원래 상태로 복구
                        loadTodaySchedules()

                        _uiState.value = _uiState.value.copy(
                            error = result.error,
                            canRetry = false
                        )
                    }
                }
            }
        }
    }

    private fun calculateCurrentSchedule(schedules: List<Schedule>, currentTime: Long): Schedule? {
        return schedules
            .filter { !it.isCompleted }
            .let { incompleteSchedules ->
                // loadTodaySchedules()와 동일한 로직 복사
                val ongoingSchedules = incompleteSchedules.filter { schedule ->
                    val startTime = schedule.startTime.toDate().time
                    val endTime = schedule.endTime?.toDate()?.time ?: (startTime + 60 * 60 * 1000)
                    currentTime >= startTime && currentTime <= endTime
                }

                if (ongoingSchedules.isNotEmpty()) {
                    ongoingSchedules.minByOrNull { schedule ->
                        schedule.endTime?.toDate()?.time
                            ?: (schedule.startTime.toDate().time + 60 * 60 * 1000)
                    }
                } else {
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
    }

    fun showScheduleDetail(scheduleId: String) {
        val schedule = _uiState.value.todaySchedules.find { it.id == scheduleId }
        _uiState.value = _uiState.value.copy(selectedScheduleForDetail = schedule)
    }

    fun hideScheduleDetail() {
        _uiState.value = _uiState.value.copy(selectedScheduleForDetail = null)
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null, canRetry = false)
    }

    fun retryLastAction() {
        clearError()
        loadTodaySchedules()
    }

    fun showDeleteConfirmDialog(scheduleId: String) {
        val schedule = _uiState.value.todaySchedules.find { it.id == scheduleId }
        _uiState.value = _uiState.value.copy(
            showDeleteConfirmDialog = true,
            scheduleToDelete = schedule
        )
    }

    fun hideDeleteConfirmDialog() {
        _uiState.value = _uiState.value.copy(
            showDeleteConfirmDialog = false,
            scheduleToDelete = null
        )
    }

    fun deleteSchedule(scheduleId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                error = null,
                showDeleteConfirmDialog = false,
                scheduleToDelete = null
            )

            when (val result = scheduleRepository.deleteSchedule(scheduleId)) {
                is ScheduleRepository.ScheduleResult.Success -> {
                    Log.d("MainViewModel", "일정 삭제 성공")
                    // 목록에서 제거
                    val updatedSchedules =
                        _uiState.value.todaySchedules.filter { it.id != scheduleId }
                    _uiState.value = _uiState.value.copy(
                        todaySchedules = updatedSchedules,
                        totalCount = updatedSchedules.size,
                        completedCount = updatedSchedules.count { it.isCompleted },
                        isLoading = false
                    )
                }

                is ScheduleRepository.ScheduleResult.Error -> {
                    Log.e("MainViewModel", "일정 삭제 실패: ${result.error.message}")
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = result.error,
                        canRetry = true
                    )
                }
            }
        }
    }
}