// app/src/main/java/com/example/slowclock/ui/main/components/ScheduleCard.kt
package com.example.slowclock.ui.main.components

import android.util.Log
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.slowclock.data.model.Schedule
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun ScheduleCard(
    schedule: Schedule,
    onToggleComplete: () -> Unit,
    onShowDetail: () -> Unit,
) {
    Log.d("ScheduleCard", "렌더링: ${schedule.title} - isCompleted=${schedule.isCompleted}")
    val timeFormat = SimpleDateFormat("a h:mm", Locale.KOREAN)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onShowDetail() }
            .padding(vertical = 4.dp), // 터치 영역 확보
        colors = CardDefaults.cardColors(
            containerColor = if (schedule.isCompleted)
                MaterialTheme.colorScheme.secondaryContainer
            else
                MaterialTheme.colorScheme.primaryContainer // 하드코딩 색상 제거
        ),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxWidth()
        ) {
            // 왼쪽 색상 바 (더 두껍게)
            Box(
                modifier = Modifier
                    .width(6.dp) // 4dp → 6dp
                    .height(80.dp) // 70dp → 80dp
                    .background(
                        if (schedule.isCompleted)
                            MaterialTheme.colorScheme.secondary
                        else
                            MaterialTheme.colorScheme.primary // 하드코딩 색상 제거
                    )
                    .align(Alignment.CenterStart)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp) // 16dp → 20dp
                    .padding(start = 12.dp), // 8dp → 12dp
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.Notifications,
                    contentDescription = null,
                    tint = if (schedule.isCompleted)
                        MaterialTheme.colorScheme.secondary
                    else
                        MaterialTheme.colorScheme.primary, // 하드코딩 색상 제거
                    modifier = Modifier.size(28.dp) // 24dp → 28dp
                )

                Spacer(modifier = Modifier.width(16.dp)) // 12dp → 16dp

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = schedule.title,
                        style = MaterialTheme.typography.bodyLarge, // fontSize 대신 style 사용
                        color = MaterialTheme.colorScheme.onSurface // 하드코딩 색상 제거
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = timeFormat.format(schedule.startTime.toDate()),
                            style = MaterialTheme.typography.bodyMedium, // fontSize 대신 style 사용
                            color = MaterialTheme.colorScheme.onSurfaceVariant // 하드코딩 색상 제거
                        )
                    }
                }

                // 완료 체크박스 (더 크게, 터치 영역 확대)
                Box(
                    modifier = Modifier.size(48.dp), // 터치 영역 확대
                    contentAlignment = Alignment.Center
                ) {
                    Checkbox(
                        checked = schedule.isCompleted,
                        onCheckedChange = { onToggleComplete() },
                        modifier = Modifier.size(36.dp), // 32dp → 36dp
                        colors = CheckboxDefaults.colors(
                            checkedColor = MaterialTheme.colorScheme.secondary, // 하드코딩 색상 제거
                            uncheckedColor = MaterialTheme.colorScheme.primary, // 하드코딩 색상 제거
                            checkmarkColor = MaterialTheme.colorScheme.onSecondary // 하드코딩 색상 제거
                        )
                    )
                }
            }
        }
    }
}