// app/src/main/java/com/example/slowclock/ui/main/components/CurrentTaskSection.kt
package com.example.slowclock.ui.main.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.slowclock.data.model.Schedule
import com.example.slowclock.util.isOngoing
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun CurrentTaskSection(
    schedule: Schedule,
    onShowDetail: () -> Unit
) {
    val timeFormat = SimpleDateFormat("a h:mm", Locale.KOREAN)
    val currentTime = System.currentTimeMillis()

    val displayTime = if (isOngoing(schedule, currentTime)) {
        "~${timeFormat.format(schedule.endTime?.toDate() ?: Date(schedule.startTime.toDate().time + 60 * 60 * 1000))}"
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
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "지금 할 일",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        // 카드
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onShowDetail() },
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.tertiaryContainer
            ),
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Box(modifier = Modifier.fillMaxWidth()) {
                // 왼쪽 강조 바
                Box(
                    modifier = Modifier
                        .width(6.dp)
                        .height(80.dp)
                        .background(MaterialTheme.colorScheme.tertiary)
                        .align(Alignment.CenterStart)
                )

                // 오른쪽 상단 시간
                Text(
                    text = displayTime,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onTertiaryContainer,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(20.dp)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                        .padding(start = 12.dp, end = 80.dp), // 시간 텍스트 공간 확보
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Notifications,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.tertiary,
                        modifier = Modifier.size(28.dp)
                    )

                    Spacer(modifier = Modifier.width(16.dp))

                    Column {
                        Text(
                            text = schedule.title,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onTertiaryContainer
                        )

                        // 상세 설명 (있을 때만)
                        if (schedule.description.isNotBlank()) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = schedule.description,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.8f)
                            )
                        }
                    }
                }
            }
        }
    }
}