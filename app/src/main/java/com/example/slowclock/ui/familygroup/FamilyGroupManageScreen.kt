package com.example.slowclock.ui.familygroup

import androidx.lifecycle.viewmodel.compose.viewModel
import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun FamilyGroupManageScreen(viewModel: FamilyGroupViewModel = viewModel(), context: Context) {
    var groupName by remember { mutableStateOf("") }
    var joinGroupId by remember { mutableStateOf("") }
    var myGroupId by remember { mutableStateOf("") }

    Column {
        // 그룹 생성
        OutlinedTextField(
            value = groupName,
            onValueChange = { groupName = it },
            label = { Text("새 그룹 이름") }
        )
        Button(onClick = {
            viewModel.createFamilyGroup(groupName) { groupId ->
                myGroupId = groupId ?: ""
            }
        }) {
            Text("가족 그룹 생성")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 그룹 참가
        OutlinedTextField(
            value = joinGroupId,
            onValueChange = { joinGroupId = it },
            label = { Text("참가할 그룹ID") }
        )
        Button(onClick = {
            // 현재 유저의 uid 필요 (FirebaseAuth.currentUser.uid)
            val uid = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.uid ?: ""
            viewModel.joinFamilyGroup(joinGroupId, uid) { success ->
                // 결과 처리
            }
        }) {
            Text("가족 그룹 참가")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 그룹 전체 알림 보내기
        Button(
            onClick = {
                // myGroupId에 현재 내가 속한 그룹ID 입력
                viewModel.sendAlertToFamilyGroup(
                    context,
                    myGroupId,
                    "가족 일정 알림",
                    "오늘 일정을 확인해 주세요!"
                )
            },
            enabled = myGroupId.isNotEmpty()
        ) {
            Text("가족 모두에게 알림 보내기")
        }
    }
}
