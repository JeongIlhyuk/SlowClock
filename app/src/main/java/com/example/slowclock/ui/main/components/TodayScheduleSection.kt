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
    val (completed, remaining) = schedules.partition { it.isCompleted }

    Column {
        // 섹션 제목
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 12.dp)
        ) {
            Icon(
                Icons.Outlined.CalendarToday,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "오늘의 일정",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        // 완료한 일정
        if (completed.isNotEmpty()) {
            Text("✔ 완료한 일정", style = MaterialTheme.typography.titleMedium)
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                completed.forEach { schedule ->
                    ScheduleCard(
                        schedule = schedule,
                        onToggleComplete = { onToggleComplete(schedule.id) },
                        onShowDetail = { onShowDetail(schedule.id) },
                        isCompleted = schedule.isCompleted
                    )
                }
            }
        }

        // 남은 일정
        if (remaining.isNotEmpty()) {
            Text("🕒 남은 일정", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(top = 16.dp))
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                remaining.forEach { schedule ->
                    ScheduleCard(
                        schedule = schedule,
                        onToggleComplete = { onToggleComplete(schedule.id) },
                        onShowDetail = { onShowDetail(schedule.id) },
                        isCompleted = schedule.isCompleted
                    )
                }
            }
        }
    }
}

