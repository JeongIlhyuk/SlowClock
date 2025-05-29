// app/src/main/java/com/example/slowclock/ui/main/MainScreen.kt
package com.example.slowclock.ui.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.slowclock.ui.main.components.CurrentTaskSection
import com.example.slowclock.ui.main.components.ScheduleDetailDialog
import com.example.slowclock.ui.main.components.TodayScheduleSection
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: MainViewModel = viewModel(),
    shouldRefresh: Boolean = false,
    onAddSchedule: () -> Unit = {},
    onRefreshHandled: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val dateFormat = SimpleDateFormat("yyyy년 M월 d일 EEEE", Locale.KOREAN)

    // 일정 추가 후 자동 새로고침
    LaunchedEffect(shouldRefresh) {
        if (shouldRefresh) {
            viewModel.loadTodaySchedules()
            onRefreshHandled()
        }
    }

    // 세부정보 다이얼로그
    uiState.selectedScheduleForDetail?.let { schedule ->
        ScheduleDetailDialog(
            schedule = schedule,
            onDismiss = { viewModel.hideScheduleDetail() },
        )
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "느린시계",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2196F3)
                        )
                        Text(
                            text = "오늘 ${dateFormat.format(Date())}",
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                    }
                },
                actions = {
                    // 새로고침 버튼 추가
                    IconButton(
                        onClick = { viewModel.loadTodaySchedules() }
                    ) {
                        if (uiState.isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = Color(0xFF2196F3),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(
                                Icons.Default.Refresh,
                                contentDescription = "새로고침",
                                tint = Color(0xFF2196F3)
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.White
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddSchedule,
                containerColor = Color(0xFF2196F3)
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = "일정 추가",
                    tint = Color.White
                )
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF5F5F5)),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // 지금 할 일
            uiState.currentSchedule?.let { schedule ->
                item {
                    CurrentTaskSection(
                        schedule = schedule,
                        onShowDetail = { viewModel.showScheduleDetail(schedule.id) }
                    )
                }
            }

            // 오늘의 일정
            item {
                TodayScheduleSection(
                    schedules = uiState.todaySchedules,
                    onToggleComplete = viewModel::toggleScheduleComplete,
                    onShowDetail = viewModel::showScheduleDetail
                )
            }

            // 빈 상태 처리
            if (uiState.todaySchedules.isEmpty() && !uiState.isLoading) {
                item {
                    Box(
                        modifier = Modifier.fillParentMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "📅",
                                fontSize = 48.sp
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "오늘 등록된 일정이 없습니다",
                                fontSize = 16.sp,
                                color = Color.Gray,
                                fontWeight = FontWeight.Medium
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.Refresh,
                                    contentDescription = null,
                                    tint = Color(0xFF2196F3),
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "새로고침하거나",
                                    fontSize = 14.sp,
                                    color = Color.Gray
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Icon(
                                    Icons.Default.Add,
                                    contentDescription = null,
                                    tint = Color(0xFF2196F3),
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "일정을 추가해보세요",
                                    fontSize = 14.sp,
                                    color = Color.Gray
                                )
                            }
                        }
                    }
                }
            }

            // 에러 메시지
            if (uiState.error != null) {
                item {
                    Box(
                        modifier = Modifier.fillParentMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "⚠️",
                                fontSize = 32.sp
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = uiState.error!!,
                                fontSize = 16.sp,
                                color = Color(0xFFD32F2F),
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }
    }
}