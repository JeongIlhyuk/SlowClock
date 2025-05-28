package com.example.slowclock.ui.addschedule

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.slowclock.data.model.Schedule
import com.example.slowclock.data.repository.ScheduleRepository
import com.google.firebase.Timestamp
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Calendar

data class AddScheduleUiState(
    val title: String = "",
    val selectedTime: Calendar = Calendar.getInstance(),
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null,
    val canSave: Boolean = false
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

    fun updateTime(time: Calendar) {
        _uiState.value = _uiState.value.copy(selectedTime = time)
    }

    fun saveSchedule() {
        val currentTitle = _uiState.value.title.trim()
        if (currentTitle.isBlank()) {
            _uiState.value = _uiState.value.copy(error = "할 일을 입력해주세요")
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            try {
                val schedule = Schedule(
                    title = currentTitle,
                    startTime = Timestamp(_uiState.value.selectedTime.time)
                )

                val result = scheduleRepository.addSchedule(schedule)

                if (result != null) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isSuccess = true
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "일정 저장에 실패했습니다"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "일정 저장 중 오류가 발생했습니다"
                )
            }
        }
    }
}