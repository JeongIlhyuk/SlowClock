package com.example.slowclock.ui.main.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.slowclock.data.model.Schedule
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun CurrentTaskSection(schedule: Schedule) {
    val timeFormat = SimpleDateFormat("a h:mm", Locale.KOREAN)
    val currentTime = System.currentTimeMillis()

    val displayTime = if (isOngoing(schedule, currentTime)) {
        "~${
            timeFormat.format(
                schedule.endTime?.toDate() ?: Date(schedule.startTime.toDate().time + 60 * 60 * 1000)
            )
        }"
    } else {
        timeFormat.format(schedule.startTime.toDate())
    }

    Column {
        // 섹션 헤더
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 8.dp)
        ) {
            Icon(
                Icons.Outlined.AccessTime,
                contentDescription = null,
                tint = Color(0xFF2196F3),
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "지금 할 일",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black
            )

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = displayTime,
                fontSize = 14.sp,
                color = Color.Gray
            )
        }

        // 노란색 카드 (왼쪽 바 제거)
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFFFFF9C4)
            ),
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Box(
                modifier = Modifier.fillMaxWidth()
            ) {
                // 왼쪽 노란 바
                Box(
                    modifier = Modifier
                        .width(4.dp)
                        .height(60.dp)
                        .background(Color(0xFFFFD54F))
                        .align(Alignment.CenterStart)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .padding(start = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Notifications,
                        contentDescription = null,
                        tint = Color(0xFFF57C00),
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = schedule.title,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Normal,
                        color = Color.Black
                    )
                }
            }
        }
    }
}

private fun isOngoing(schedule: Schedule, currentTime: Long): Boolean {
    val startTime = schedule.startTime.toDate().time
    val endTime = schedule.endTime?.toDate()?.time ?: (startTime + 60 * 60 * 1000)
    return currentTime >= startTime && currentTime <= endTime
}