package com.example.slowclock.ui.addschedule.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerDefaults
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
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
    val quickTimes = remember { getQuickTimeOptions() }

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
            Text(
                text = "빠른 선택",
                fontSize = 14.sp,
                color = Color(0xFF424242),
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                quickTimes.take(3).forEach { (label, calendar) ->
                    OutlinedButton(
                        onClick = { onTimeSelected(calendar) },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            label,
                            fontSize = 14.sp,
                            color = Color(0xFF2196F3),
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                quickTimes.drop(3).forEach { (label, calendar) ->
                    OutlinedButton(
                        onClick = { onTimeSelected(calendar) },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            label,
                            fontSize = 14.sp,
                            color = Color(0xFF2196F3),
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }

    // 시작 시간 선택 다이얼로그
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

    // 종료 시간 선택 다이얼로그
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TimePickerDialog(
    title: String,
    initialTime: Calendar,
    onTimeSelected: (Calendar) -> Unit,
    onDismiss: () -> Unit
) {
    val timePickerState = rememberTimePickerState(
        initialHour = initialTime.get(Calendar.HOUR_OF_DAY),
        initialMinute = initialTime.get(Calendar.MINUTE)
    )

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = title,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                TimePicker(
                    state = timePickerState,
                    colors = TimePickerDefaults.colors(
                        clockDialColor = Color(0xFFE3F2FD),
                        selectorColor = Color(0xFF2196F3)
                    )
                )

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    TextButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("취소", color = Color.Gray)
                    }

                    TextButton(
                        onClick = {
                            val selectedCalendar = Calendar.getInstance().apply {
                                set(Calendar.HOUR_OF_DAY, timePickerState.hour)
                                set(Calendar.MINUTE, timePickerState.minute)
                                set(Calendar.SECOND, 0)
                            }
                            onTimeSelected(selectedCalendar)
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("확인", color = Color(0xFF2196F3), fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

private fun getQuickTimeOptions(): List<Pair<String, Calendar>> {
    return listOf(
        "지금" to Calendar.getInstance(),
        "30분 후" to Calendar.getInstance().apply {
            add(Calendar.MINUTE, 30)
        },
        "1시간 후" to Calendar.getInstance().apply {
            add(Calendar.HOUR, 1)
        },
        "오후 2시" to Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 14)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
        },
        "오후 6시" to Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 18)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
        },
        "오후 8시" to Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 20)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
        }
    )
}