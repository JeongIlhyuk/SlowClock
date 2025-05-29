// ui/main/MainScreen.kt
package com.example.slowclock.ui.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
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
    onNavigateToProfile: () -> Unit = {},
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
                            fontSize = 28.sp, // 더 크게
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2196F3)
                        )
                        Text(
                            text = dateFormat.format(Date()),
                            fontSize = 16.sp, // 더 크게
                            color = Color(0xFF424242),
                            fontWeight = FontWeight.Medium
                        )
                    }
                },
                actions = {

                    // 프로필 버튼 (더 크게)
                    IconButton(
                        onClick = onNavigateToProfile,
                        modifier = Modifier.size(48.dp) // 더 크게
                    ) {
                        Icon(
                            Icons.Default.Person,
                            contentDescription = "내 정보",
                            tint = Color(0xFF2196F3),
                            modifier = Modifier.size(28.dp) // 아이콘도 크게
                        )
                    }
                    // 새로고침 버튼 (더 크게)
                    IconButton(
                        onClick = { viewModel.loadTodaySchedules() },
                        modifier = Modifier.size(48.dp)
                    ) {

                        if (uiState.isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = Color(0xFF2196F3),
                                strokeWidth = 3.dp
                            )
                        } else {
                            Icon(
                                Icons.Default.Refresh,
                                contentDescription = "새로고침",
                                tint = Color(0xFF2196F3),
                                modifier = Modifier.size(28.dp)
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
                containerColor = Color(0xFF2196F3),
                modifier = Modifier.size(64.dp) // 더 크게
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = "일정 추가",
                    tint = Color.White,
                    modifier = Modifier.size(32.dp) // 아이콘도 크게
                )
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF5F5F5)),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 24.dp), // 더 큰 패딩
            verticalArrangement = Arrangement.spacedBy(24.dp) // 더 큰 간격
        ) {

            // 📊 오늘 일정 요약 (새로 추가)
            item {
                TodaySummaryCard(
                    totalCount = uiState.totalCount,
                    completedCount = uiState.completedCount
                )
            }

            // 🟡 지금 할 일
            uiState.currentSchedule?.let { schedule ->
                item {
                    CurrentTaskSection(
                        schedule = schedule,
                        onShowDetail = { viewModel.showScheduleDetail(schedule.id) }
                    )
                }
            }

            // 📋 오늘의 일정
            item {
                TodayScheduleSection(
                    schedules = uiState.todaySchedules,
                    onToggleComplete = viewModel::toggleScheduleComplete,
                    onShowDetail = viewModel::showScheduleDetail
                )
            }

            // 빈 상태 처리 (더 친근하게)
            if (uiState.todaySchedules.isEmpty() && !uiState.isLoading) {
                item {
                    EmptyStateCard()
                }
            }

            // 에러 메시지 (더 명확하게)
            if (uiState.error != null) {
                item {
                    ErrorCard(error = uiState.error!!)
                }
            }
        }
    }
}

// 📊 오늘 일정 요약 카드 (새로 추가)
@Composable
private fun TodaySummaryCard(
    totalCount: Int,
    completedCount: Int
) {
    val progress = if (totalCount > 0) completedCount.toFloat() / totalCount else 0f

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = Color(0xFF4CAF50),
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "오늘의 진행상황",
                    fontSize = 20.sp, // 큰 글씨
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 진행률 표시
            Text(
                text = "${completedCount}개 완료 / 총 ${totalCount}개",
                fontSize = 18.sp, // 큰 글씨
                fontWeight = FontWeight.Medium,
                color = Color(0xFF424242)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // 진행률 바
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp), // 두꺼운 진행률 바
                color = Color(0xFF4CAF50),
                trackColor = Color(0xFFE0E0E0)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // 퍼센트 표시
            Text(
                text = "${(progress * 100).toInt()}% 완료",
                fontSize = 16.sp,
                color = Color(0xFF4CAF50),
                fontWeight = FontWeight.Medium
            )
        }
    }
}

// 😊 빈 상태 카드 (개선)
@Composable
private fun EmptyStateCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF3E5F5)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "📅",
                fontSize = 64.sp // 더 큰 이모지
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "오늘 등록된 일정이 없습니다",
                fontSize = 20.sp, // 큰 글씨
                color = Color(0xFF424242),
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "아래 + 버튼을 눌러 일정을 추가해보세요",
                fontSize = 16.sp,
                color = Color(0xFF757575)
            )
        }
    }
}

// ⚠️ 에러 카드 (개선)
@Composable
private fun ErrorCard(error: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "⚠️",
                fontSize = 48.sp
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "문제가 발생했습니다",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFD32F2F)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = error,
                fontSize = 16.sp,
                color = Color(0xFFD32F2F)
            )
        }
    }
}