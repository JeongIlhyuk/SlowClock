// app/src/main/java/com/example/slowclock/ui/main/components/TodaySummaryCard.kt
package com.example.slowclock.ui.main.components

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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun TodaySummaryCard(
    totalCount: Int,
    completedCount: Int
) {
    val progress = if (totalCount > 0) completedCount.toFloat() / totalCount else 0f

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface // 하드코딩 색상 제거
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.secondary, // 하드코딩 색상 제거
                    modifier = Modifier.size(32.dp) // 28dp → 32dp
                )
                Spacer(modifier = Modifier.width(16.dp)) // 12dp → 16dp
                Text(
                    text = "오늘의 진행상황",
                    style = MaterialTheme.typography.headlineSmall, // fontSize 대신 style 사용
                    color = MaterialTheme.colorScheme.onSurface // 하드코딩 색상 제거
                )
            }

            Spacer(modifier = Modifier.height(20.dp)) // 16dp → 20dp

            // 진행률 표시
            Text(
                text = "${completedCount}개 완료 / 총 ${totalCount}개",
                style = MaterialTheme.typography.bodyLarge, // fontSize 대신 style 사용
                color = MaterialTheme.colorScheme.onSurfaceVariant // 하드코딩 색상 제거
            )

            Spacer(modifier = Modifier.height(16.dp)) // 12dp → 16dp

            // 진행률 바 (더 두껍게)
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp), // 8dp → 10dp
                color = MaterialTheme.colorScheme.secondary, // 하드코딩 색상 제거
                trackColor = MaterialTheme.colorScheme.surfaceVariant // 하드코딩 색상 제거
            )

            Spacer(modifier = Modifier.height(12.dp)) // 8dp → 12dp

            // 퍼센트 표시
            Text(
                text = "${(progress * 100).toInt()}% 완료",
                style = MaterialTheme.typography.bodyMedium, // fontSize 대신 style 사용
                color = MaterialTheme.colorScheme.secondary // 하드코딩 색상 제거
            )
        }
    }
}