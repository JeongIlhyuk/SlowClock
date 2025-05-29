package com.example.slowclock.ui.addschedule.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "시간 설정",
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // 시작 시간
            Text(
                text = "시작 시간",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD)),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onShowTimePicker(true) }
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.AccessTime,
                        contentDescription = null,
                        tint = Color(0xFF2196F3)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = timeFormat.format(selectedTime.time),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Text(
                        text = "변경",
                        fontSize = 14.sp,
                        color = Color(0xFF2196F3)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 종료 시간 (선택사항)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "종료 시간 (선택사항)",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.weight(1f))
                if (endTime != null) {
                    IconButton(onClick = { onEndTimeSelected(null) }) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = "종료 시간 제거",
                            tint = Color.Gray
                        )
                    }
                }
            }

            if (endTime != null) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF3E5F5)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onShowEndTimePicker(true) }
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.AccessTime,
                            contentDescription = null,
                            tint = Color(0xFF9C27B0)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = timeFormat.format(endTime.time),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        Text(
                            text = "변경",
                            fontSize = 14.sp,
                            color = Color(0xFF9C27B0)
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
                    Text("종료 시간 설정하기")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

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