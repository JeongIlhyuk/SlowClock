// app/src/main/java/com/example/slowclock/ui/addschedule/AddScheduleViewModel.kt
package com.example.slowclock.ui.addschedule

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.slowclock.data.model.Schedule
import com.example.slowclock.data.repository.ScheduleRepository
import com.example.slowclock.util.AppError
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Calendar

data class AddScheduleUiState(
    val title: String = "",
    val description: String = "",
    val selectedTime: Calendar = Calendar.getInstance(),
    val endTime: Calendar? = null,
    val isRecurring: Boolean = false,
    val recurringType: String = "daily",
    val showTimePicker: Boolean = false,
    val showEndTimePicker: Boolean = false,
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: AppError? = null,
    val canSave: Boolean = false,
    val canRetry: Boolean = false
)

class AddScheduleViewModel : ViewModel() {
    private val scheduleRepository = ScheduleRepository()

    private val _uiState = MutableStateFlow(AddScheduleUiState())
    val uiState: StateFlow<AddScheduleUiState> = _uiState.asStateFlow()

    fun updateTitle(title: String) {
        _uiState.value = _uiState.value.copy(
            title = title,
            canSave = title.trim().isNotBlank(),
            error = null
        )
    }

    fun updateDescription(description: String) {
        _uiState.value = _uiState.value.copy(description = description)
    }

    fun updateTime(time: Calendar) {
        val newTime = Calendar.getInstance().apply {
            timeInMillis = time.timeInMillis
        }
        _uiState.value = _uiState.value.copy(selectedTime = newTime)
    }

    fun updateEndTime(time: Calendar?) {
        val newTime = time?.let {
            Calendar.getInstance().apply { timeInMillis = it.timeInMillis }
        }
        _uiState.value = _uiState.value.copy(endTime = newTime)
    }

    fun updateRecurring(isRecurring: Boolean) {
        _uiState.value = _uiState.value.copy(isRecurring = isRecurring)
    }

    fun updateRecurringType(type: String) {
        _uiState.value = _uiState.value.copy(recurringType = type)
    }

    fun showTimePicker(show: Boolean) {
        _uiState.value = _uiState.value.copy(showTimePicker = show)
    }

    fun showEndTimePicker(show: Boolean) {
        _uiState.value = _uiState.value.copy(showEndTimePicker = show)
    }

    fun saveSchedule() {
        val currentTitle = _uiState.value.title.trim()

        // 클라이언트 측 검증 (copy 함수 대신 직접 AppError.GeneralError 사용)
        when {
            currentTitle.isBlank() -> {
                _uiState.value = _uiState.value.copy(
                    error = AppError.GeneralError("할 일을 입력해주세요") // 수정
                )
                return
            }

            currentTitle.length > 100 -> {
                _uiState.value = _uiState.value.copy(
                    error = AppError.GeneralError("제목이 너무 깁니다 (최대 100자)") // 수정
                )
                return
            }

            _uiState.value.endTime?.let { end ->
                end.timeInMillis <= _uiState.value.selectedTime.timeInMillis
            } == true -> {
                _uiState.value = _uiState.value.copy(
                    error = AppError.GeneralError("종료 시간은 시작 시간보다 늦어야 합니다") // 수정
                )
                return
            }
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                error = null,
                canRetry = false
            )

            try {
                Log.d("AddSchedule", "일정 저장 시작: $currentTitle")
                Log.d("AddSchedule", "현재 사용자: ${FirebaseAuth.getInstance().currentUser?.uid}")

                val schedule = Schedule(
                    title = currentTitle,
                    description = _uiState.value.description.trim(),
                    startTime = Timestamp(_uiState.value.selectedTime.time),
                    endTime = _uiState.value.endTime?.let { Timestamp(it.time) },
                    isRecurring = _uiState.value.isRecurring,
                    recurringType = if (_uiState.value.isRecurring) _uiState.value.recurringType else null
                )

                when (val result = scheduleRepository.addSchedule(schedule)) {
                    is ScheduleRepository.ScheduleResult.Success -> {
                        Log.d("AddSchedule", "저장 성공: ${result.data}")
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            isSuccess = true
                        )
                    }

                    is ScheduleRepository.ScheduleResult.Error -> {
                        Log.e("AddSchedule", "저장 실패: ${result.error.message}")
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = result.error,
                            canRetry = true
                        )
                    }
                }
            } catch (e: Exception) {
                Log.e("AddSchedule", "예상치 못한 저장 실패", e)
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = AppError.GeneralError("일정 저장 중 문제가 발생했습니다"), // 수정
                    canRetry = true
                )
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null, canRetry = false)
    }

    fun retryLastAction() {
        clearError()
        saveSchedule()
    }
}

// copy 확장 함수 완전히 제거 (더 이상 필요 없음)