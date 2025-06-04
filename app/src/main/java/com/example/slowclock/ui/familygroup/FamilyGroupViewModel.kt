package com.example.slowclock.ui.familygroup

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.slowclock.data.remote.repository.FamilyGroupRepository
import kotlinx.coroutines.launch

class FamilyGroupViewModel(app: Application) : AndroidViewModel(app) {
    private val repo = FamilyGroupRepository()

    // 1. 그룹 생성 (이름 받아서 생성)
    fun createFamilyGroup(name: String, onResult: (String?) -> Unit) {
        viewModelScope.launch {
            val groupId = repo.createFamilyGroup(name)
            onResult(groupId)
        }
    }

    // 2. 그룹 참가(멤버 추가)
    fun joinFamilyGroup(groupId: String, memberId: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val success = repo.addMemberToGroup(groupId, memberId)
            onResult(success)
        }
    }

    // 3. 그룹 전체에 FCM 알림 발송
    fun sendAlertToFamilyGroup(context: Context, groupId: String, title: String, message: String) {
        viewModelScope.launch {
            repo.sendAlertToGroup(context, groupId, title, message)
        }
    }
}
