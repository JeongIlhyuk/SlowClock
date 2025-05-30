// app/src/main/java/com/example/slowclock/ui/main/components/TodayScheduleSection.kt
package com.example.slowclock.ui.main.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.slowclock.data.model.Schedule

@Composable
fun TodayScheduleSection(
    schedules: List<Schedule>,
    onToggleComplete: (String) -> Unit,
    onShowDetail: (String) -> Unit,
) {
    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 12.dp) // 8dp → 12dp
        ) {
            Icon(
                Icons.Outlined.CalendarToday,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary, // 하드코딩 색상 제거
                modifier = Modifier.size(24.dp) // 20dp → 24dp
            )
            Spacer(modifier = Modifier.width(8.dp)) // 4dp → 8dp
            Text(
                text = "오늘의 일정",
                style = MaterialTheme.typography.titleLarge, // fontSize 대신 style 사용
                color = MaterialTheme.colorScheme.onSurface // 하드코딩 색상 제거
            )
        }

        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp) // 8dp → 12dp
        ) {
            schedules.forEach { schedule ->
                ScheduleCard(
                    schedule = schedule,
                    onToggleComplete = { onToggleComplete(schedule.id) },
                    onShowDetail = { onShowDetail(schedule.id) },
                )
            }
        }
    }
}