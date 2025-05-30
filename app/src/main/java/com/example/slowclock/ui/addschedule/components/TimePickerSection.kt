// app/src/main/java/com/example/slowclock/ui/addschedule/components/TimePickerSection.kt
package com.example.slowclock.ui.addschedule.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.slowclock.ui.common.dialog.TimePickerDialog
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Composable
fun TimePickerSection(
    selectedTime: Calendar,
    endTime: Calendar?,
    showTimePicker: Boolean,
    showEndTimePicker: Boolean,
    onTimeSelected: (Calendar) -> Unit,
    onEndTimeSelected: (Calendar?) -> Unit,
    onShowTimePicker: (Boolean) -> Unit,
    onShowEndTimePicker: (Boolean) -> Unit
) {
    val timeFormat = SimpleDateFormat("a h:mm", Locale.KOREAN)

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Text(
                text = "시간 설정",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(bottom = 20.dp)
            )

            // 시작 시간
            Text(
                text = "시작 시간",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onShowTimePicker(true) }
            ) {
                Row(
                    modifier = Modifier.padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.AccessTime,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = timeFormat.format(selectedTime.time),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Text(
                        text = "변경",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 종료 시간 (선택사항)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "종료 시간 (선택사항)",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.weight(1f))
                if (endTime != null) {
                    IconButton(onClick = { onEndTimeSelected(null) }) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = "종료 시간 제거",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }

            if (endTime != null) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.tertiaryContainer
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onShowEndTimePicker(true) }
                ) {
                    Row(
                        modifier = Modifier.padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.AccessTime,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.tertiary,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = timeFormat.format(endTime.time),
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onTertiaryContainer
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        Text(
                            text = "변경",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.tertiary
                        )
                    }
                }
            } else {
                OutlinedButton(
                    onClick = {
                        val defaultEndTime = Calendar.getInstance().apply {
                            timeInMillis = selectedTime.timeInMillis
                            add(Calendar.HOUR, 1)
                        }
                        onEndTimeSelected(defaultEndTime)
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        "종료 시간 설정하기",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 빠른 시간 선택
            QuickTimeSelector(onTimeSelected = onTimeSelected)
        }
    }

    // 다이얼로그들
    if (showTimePicker) {
        TimePickerDialog(
            title = "시작 시간 선택",
            initialTime = selectedTime,
            onTimeSelected = { time ->
                onTimeSelected(time)
                onShowTimePicker(false)
            },
            onDismiss = { onShowTimePicker(false) }
        )
    }

    if (showEndTimePicker) {
        TimePickerDialog(
            title = "종료 시간 선택",
            initialTime = endTime ?: selectedTime,
            onTimeSelected = { time ->
                onEndTimeSelected(time)
                onShowEndTimePicker(false)
            },
            onDismiss = { onShowEndTimePicker(false) }
        )
    }
}