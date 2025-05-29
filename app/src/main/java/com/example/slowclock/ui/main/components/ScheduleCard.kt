// app/src/main/java/com/example/slowclock/ui/main/components/ScheduleCard.kt
package com.example.slowclock.ui.main.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
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
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Notifications
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
import java.util.Locale

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ScheduleCard(
    schedule: Schedule,
    onToggleComplete: () -> Unit,
    onShowDetail: () -> Unit
) {
    val timeFormat = SimpleDateFormat("a h:mm", Locale.KOREAN)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = onToggleComplete,
                onLongClick = onShowDetail
            ),
        colors = CardDefaults.cardColors(
            containerColor = if (schedule.isCompleted) Color(0xFFE8F5E9) else Color(0xFFE3F2FD)
        ),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxWidth()
        ) {
            // 왼쪽 색상 바
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(60.dp)
                    .background(
                        if (schedule.isCompleted) Color(0xFF4CAF50) else Color(0xFF2196F3)
                    )
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
                    tint = if (schedule.isCompleted) Color(0xFF4CAF50) else Color(0xFF2196F3),
                    modifier = Modifier.size(24.dp)
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = schedule.title,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Normal,
                        color = Color.Black
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = timeFormat.format(schedule.startTime.toDate()),
                            fontSize = 14.sp,
                            color = Color.Gray
                        )

                        // 힌트 표시
                        if (hasExtraInfo(schedule)) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(
                                Icons.Default.MoreHoriz,
                                contentDescription = "상세정보 있음",
                                tint = Color.Gray,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }

                if (schedule.isCompleted) {
                    Text(
                        text = "완료",
                        fontSize = 14.sp,
                        color = Color(0xFF4CAF50),
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

private fun hasExtraInfo(schedule: Schedule): Boolean {
    return schedule.description.isNotBlank() ||
            schedule.endTime != null ||
            schedule.isRecurring
}