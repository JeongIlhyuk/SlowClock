// app/src/main/java/com/example/slowclock/ui/main/MainScreen.kt
package com.example.slowclock.ui.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.slowclock.ui.common.components.ErrorCard
import com.example.slowclock.ui.common.dialog.DeleteConfirmDialog
import com.example.slowclock.ui.main.components.CurrentTaskSection
import com.example.slowclock.ui.main.components.EmptyStateCard
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
    onEditSchedule: (String) -> Unit = {},
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
            onEdit = {
                viewModel.hideScheduleDetail()
                onEditSchedule(schedule.id) // 편집 화면으로 이동
            },
            onDelete = {
                viewModel.hideScheduleDetail()
                viewModel.showDeleteConfirmDialog(schedule.id)
            }
        )
    }


    // 삭제 확인 다이얼로그 (이것도 필요함)
    if (uiState.showDeleteConfirmDialog && uiState.scheduleToDelete != null) {
        DeleteConfirmDialog(
            schedule = uiState.scheduleToDelete!!,
            onConfirm = {
                viewModel.deleteSchedule(uiState.scheduleToDelete!!.id)
            },
            onDismiss = { viewModel.hideDeleteConfirmDialog() }
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
                            style = MaterialTheme.typography.headlineLarge, // fontSize 대신 style 사용
                            color = MaterialTheme.colorScheme.primary // 하드코딩 색상 제거
                        )
                        Text(
                            text = dateFormat.format(Date()),
                            style = MaterialTheme.typography.bodyLarge, // fontSize 대신 style 사용
                            color = MaterialTheme.colorScheme.onSurfaceVariant // 하드코딩 색상 제거
                        )
                    }
                },
                actions = {
                    // 새로고침 버튼 (더 크게)
                    IconButton(
                        onClick = { viewModel.loadTodaySchedules() },
                        modifier = Modifier.size(56.dp) // 48dp → 56dp
                    ) {
                        if (uiState.isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(28.dp), // 24dp → 28dp
                                color = MaterialTheme.colorScheme.primary, // 하드코딩 색상 제거
                                strokeWidth = 4.dp // 3dp → 4dp
                            )
                        } else {
                            Icon(
                                Icons.Default.Refresh,
                                contentDescription = "새로고침",
                                tint = MaterialTheme.colorScheme.primary, // 하드코딩 색상 제거
                                modifier = Modifier.size(32.dp) // 28dp → 32dp
                            )
                        }
                    }

                    // 프로필 버튼 (더 크게)
                    IconButton(
                        onClick = onNavigateToProfile,
                        modifier = Modifier.size(56.dp) // 48dp → 56dp
                    ) {
                        Icon(
                            Icons.Default.Person,
                            contentDescription = "내 정보",
                            tint = MaterialTheme.colorScheme.primary, // 하드코딩 색상 제거
                            modifier = Modifier.size(32.dp) // 28dp → 32dp
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface // 하드코딩 색상 제거
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddSchedule,
                containerColor = MaterialTheme.colorScheme.primary, // 하드코딩 색상 제거
                modifier = Modifier.size(72.dp) // 64dp → 72dp
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = "일정 추가",
                    tint = MaterialTheme.colorScheme.onPrimary, // 하드코딩 색상 제거
                    modifier = Modifier.size(36.dp) // 32dp → 36dp
                )
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background), // 하드코딩 색상 제거
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 24.dp), // 더 큰 패딩
            verticalArrangement = Arrangement.spacedBy(24.dp) // 더 큰 간격
        ) {

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
                    onShowDetail = viewModel::showScheduleDetail,
                )
            }

            // 빈 상태 처리
            if (uiState.todaySchedules.isEmpty() && !uiState.isLoading) {
                item {
                    EmptyStateCard()
                }
            }

            // 에러 메시지
            if (uiState.error != null) {
                item {
                    ErrorCard(
                        error = uiState.error!!,
                        canRetry = uiState.canRetry,
                        onRetry = { viewModel.retryLastAction() },
                        onDismiss = { viewModel.clearError() }
                    )
                }
            }
        }
    }
}