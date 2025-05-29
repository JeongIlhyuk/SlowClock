package com.example.slowclock.ui.addschedule

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.slowclock.ui.addschedule.components.RecommendationPlaceholder
import com.example.slowclock.ui.addschedule.components.TimePickerSection

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddScheduleScreen(
    onNavigateBack: (Boolean) -> Unit,
    viewModel: AddScheduleViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            onNavigateBack(true)
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "일정 추가",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { onNavigateBack(false) }) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "뒤로가기",
                            tint = Color(0xFF2196F3)
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.White
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.saveSchedule() },
                containerColor = if (uiState.canSave) Color(0xFF4CAF50) else Color.Gray
            ) {
                Icon(
                    Icons.Default.Check,
                    contentDescription = "저장",
                    tint = Color.White
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // 일정 제목 입력
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "할 일",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                    OutlinedTextField(
                        value = uiState.title,
                        onValueChange = viewModel::updateTitle,
                        placeholder = {
                            Text(
                                "무엇을 할까요?",
                                fontSize = 16.sp,
                                color = Color(0xFF757575)
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        textStyle = androidx.compose.ui.text.TextStyle(
                            fontSize = 18.sp,
                            color = Color(0xFF212121),
                            fontWeight = FontWeight.Normal
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // 설명 입력 필드 추가
                    Text(
                        text = "상세 내용 (선택사항)",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    OutlinedTextField(
                        value = uiState.description,
                        onValueChange = viewModel::updateDescription,
                        placeholder = {
                            Text(
                                "자세한 내용을 입력하세요",
                                fontSize = 14.sp,
                                color = Color(0xFF757575)
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 3,
                        textStyle = androidx.compose.ui.text.TextStyle(
                            fontSize = 16.sp,
                            color = Color(0xFF212121)
                        )
                    )
                }
            }

            // 시간 선택 (개선된 버전)
            TimePickerSection(
                selectedTime = uiState.selectedTime,
                endTime = uiState.endTime,
                showTimePicker = uiState.showTimePicker,
                showEndTimePicker = uiState.showEndTimePicker,
                onTimeSelected = viewModel::updateTime,
                onEndTimeSelected = viewModel::updateEndTime,
                onShowTimePicker = viewModel::showTimePicker,
                onShowEndTimePicker = viewModel::showEndTimePicker
            )

            // 반복 일정 설정
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "반복 설정",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Checkbox(
                            checked = uiState.isRecurring,
                            onCheckedChange = viewModel::updateRecurring
                        )
                        Text(
                            text = "반복 일정으로 설정",
                            fontSize = 16.sp,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }

                    if (uiState.isRecurring) {
                        Spacer(modifier = Modifier.height(12.dp))

                        var expanded by remember { mutableStateOf(false) }
                        val recurringOptions = listOf(
                            "daily" to "매일",
                            "weekly" to "매주",
                            "monthly" to "매월"
                        )

                        ExposedDropdownMenuBox(
                            expanded = expanded,
                            onExpandedChange = { expanded = !expanded }
                        ) {
                            OutlinedTextField(
                                value = recurringOptions.find { it.first == uiState.recurringType }?.second
                                    ?: "매일",
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("반복 주기") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                                modifier = Modifier
                                    .menuAnchor()
                                    .fillMaxWidth()
                            )

                            ExposedDropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false }
                            ) {
                                recurringOptions.forEach { (value, label) ->
                                    DropdownMenuItem(
                                        text = { Text(label) },
                                        onClick = {
                                            viewModel.updateRecurringType(value)
                                            expanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // ==================== 추천 기능 영역 (추후 구현 예정) ====================
            RecommendationPlaceholder()
            // ================================================================

            // 에러 메시지
            if (uiState.error != null) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE))
                ) {
                    Text(
                        text = uiState.error!!,
                        color = Color(0xFFD32F2F),
                        fontSize = 16.sp,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }

            // 로딩 상태
            if (uiState.isLoading) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD))
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = Color(0xFF2196F3)
                        )
                        Text(
                            text = "일정을 저장하는 중...",
                            fontSize = 16.sp
                        )
                    }
                }
            }
        }
    }
}