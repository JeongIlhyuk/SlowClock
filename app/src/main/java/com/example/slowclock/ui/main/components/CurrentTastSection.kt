package com.example.slowclock.ui.main.components

import androidx.compose.foundation.background
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

@Composable
fun CurrentTaskSection(schedule: Schedule) {
    val timeFormat = SimpleDateFormat("a h:mm", Locale.KOREAN)

    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 8.dp)
        ) {
            Icon(
                Icons.Default.Check,
                contentDescription = null,
                tint = Color(0xFF2196F3),
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "지금 할 일",
                fontSize = 16.sp,
                color = Color(0xFF2196F3)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = timeFormat.format(schedule.startTime.toDate()),
                fontSize = 14.sp,
                color = Color(0xFF2196F3)
            )
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFFFFF3CD)
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.Check,
                    contentDescription = null,
                    tint = Color(0xFFFFA000),
                    modifier = Modifier
                        .size(24.dp)
                        .background(Color.White, CircleShape)
                        .padding(4.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = schedule.title,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium
                    )
                    if (schedule.description.isNotEmpty()) {
                        Text(
                            text = schedule.description,
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                    }
                }
            }
        }
    }
}