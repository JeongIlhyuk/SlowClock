package com.example.slowclock.ui.familygroup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.slowclock.data.remote.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class FamilyGroupViewModel : ViewModel() {
    private val userRepository = UserRepository()

    private val _statusMessage = MutableStateFlow("")
    val statusMessage: StateFlow<String> = _statusMessage

    fun createGroup() {
        viewModelScope.launch {
            val result = userRepository.createOrJoinGroup()
            _statusMessage.value = if (result) "새 그룹 생성 완료!" else "그룹 생성 실패"
        }
    }

    fun joinGroup(inputGroupId: String) {
        viewModelScope.launch {
            val result = userRepository.createOrJoinGroup(inputGroupId)
            _statusMessage.value = if (result) "그룹 참가 완료!" else "그룹 참가 실패"
        }
    }
}
