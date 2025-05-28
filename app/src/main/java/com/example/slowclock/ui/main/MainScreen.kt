package com.example.slowclock.ui.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
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
import com.example.slowclock.ui.main.components.TodayScheduleSection
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: MainViewModel = viewModel(),
    onAddSchedule: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val dateFormat = SimpleDateFormat("오늘 M월 d일 EEEE", Locale.KOREAN)

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "느린시계",
                            fontSize = 28.sp,  // 크기 증가
                            fontWeight = FontWeight.Bold,
                            color = Color.Black  // 명확한 검은색
                        )
                        Text(
                            text = dateFormat.format(Date()),
                            fontSize = 16.sp,  // 크기 증가
                            color = Color.Black.copy(alpha = 0.8f)  // 진한 회색
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF8F8F8)),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 지금 할 일
            uiState.currentSchedule?.let { schedule ->
                item {
                    CurrentTaskSection(schedule = schedule)
                }
            }

            // 오늘의 일정
            item {
                TodayScheduleSection(
                    schedules = uiState.todaySchedules,
                    onToggleComplete = viewModel::toggleScheduleComplete
                )
            }
        }
    }
}