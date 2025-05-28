package com.example.slowclock.ui.main.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.slowclock.data.model.Schedule
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleCard(
    schedule: Schedule,
    onToggleComplete: () -> Unit
) {
    val timeFormat = SimpleDateFormat("a h:mm", Locale.KOREAN)
    val backgroundColor = when {
        schedule.isCompleted -> Color(0xFFE8F5E9)
        else -> Color(0xFFE3F2FD)
    }
    val iconColor = when {
        schedule.isCompleted -> Color(0xFF4CAF50)
        else -> Color(0xFF2196F3)
    }

    Card(
        onClick = onToggleComplete,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(if (schedule.isCompleted) iconColor else Color.Transparent)
                    .border(
                        width = 2.dp,
                        color = iconColor,
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (schedule.isCompleted) {
                    Icon(
                        Icons.Default.Check,
                        contentDescription = "완료",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = schedule.title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = timeFormat.format(schedule.startTime.toDate()),
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }

            if (schedule.isCompleted) {
                Text(
                    text = "완료",
                    fontSize = 14.sp,
                    color = iconColor
                )
            }
        }
    }
}