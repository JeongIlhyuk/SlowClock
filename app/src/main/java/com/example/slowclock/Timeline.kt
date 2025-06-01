package com.example.slowclock

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.tooling.preview.Preview

import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.slowclock.data.model.Schedule
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Composable
fun Timeline(
    date: String,
    modifier: Modifier = Modifier,
    items: List<Schedule>
) {
    val timelineHeight = 600.dp
    val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault()) // 시간 포맷

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        // Header
        Text(
            text = "오늘의 타임라인",
            fontSize = 20.sp,
            color = Color.Blue,
            fontWeight = Bold,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        Text(
            text = date,
            fontSize = 15.sp,
            color = Color.Gray,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        // 일정 박스
        Box(
            modifier = Modifier
                .height(timelineHeight)
                .fillMaxWidth()
        ) {
            // 중앙 선
            Divider(
                color = Color(0xFF3A5CCC),
                modifier = Modifier
                    .fillMaxHeight()
                    .width(2.dp)
                    .align(Alignment.Center)
            )

            // 일정 정렬
            val allItems = items.sortedBy { it.startTime.seconds }
            val spacing = if (allItems.size > 1) timelineHeight / (allItems.size) else 0.dp

            allItems.forEachIndexed { index, _ ->
                Box(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = spacing * index + 50.dp)
                        .width(12.dp)
                        .height(12.dp)
                        .background(Color(0xFF3A5CCC), shape = CircleShape)
                )
            }
            var twist = true
            allItems.forEachIndexed { index, item ->
                Box(
                    modifier = Modifier
                        .align(if (twist) Alignment.TopStart else Alignment.TopEnd)
                        .padding(
                            top = spacing * index + 30.dp,
                            start = 80.dp,
                            end = 80.dp
                        )
                ) {
                    Column(
                        modifier = Modifier
                            .background(Color(0xFFEFF3FF))
                            .padding(8.dp)
                    ) {
                        Text(text = item.title)
                        Text(
                            text = timeFormat.format(item.startTime.toDate()), // 실제 시간 표시
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }
                }
               if(twist) twist = false else twist = true
            }
        }
    }
}

@Preview
@Composable
private fun TimelinePreview() {
    val calendar1 = Calendar.getInstance()
        calendar1.set(Calendar.HOUR_OF_DAY, 8)
        calendar1.set(Calendar.MINUTE, 0)
        calendar1.set(Calendar.SECOND, 0)
    val calendar2 = Calendar.getInstance()
        calendar2.set(Calendar.HOUR_OF_DAY, 10)
        calendar2.set(Calendar.MINUTE, 0)
        calendar2.set(Calendar.SECOND, 0)
    val calendar3 = Calendar.getInstance()
        calendar3.set(Calendar.HOUR_OF_DAY, 12)
        calendar3.set(Calendar.MINUTE, 0)
        calendar3.set(Calendar.SECOND, 0)
    val calendar4 = Calendar.getInstance()
        calendar4.set(Calendar.HOUR_OF_DAY, 14)
        calendar4.set(Calendar.MINUTE, 0)
        calendar4.set(Calendar.SECOND, 0)
    val Events = listOf(
        Schedule(title="아침 식사", startTime=Timestamp(calendar1.time)),
        Schedule(title="병원 방문",startTime=Timestamp(calendar3.time)),
        Schedule(title="약 먹기",startTime=Timestamp(calendar2.time)),
        Schedule(title="저녁 식사",startTime=Timestamp(calendar4.time)),

    )
    Timeline(
        date = "2025년 6월 1일",
        items= Events
    )
}