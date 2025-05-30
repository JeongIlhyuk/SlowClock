// app/src/main/java/com/example/slowclock/ui/main/components/ErrorCard.kt
package com.example.slowclock.ui.main.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ErrorCard(error: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer // 하드코딩 색상 제거
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(28.dp), // 24dp → 28dp
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "⚠️",
                fontSize = 56.sp // 48sp → 56sp
            )
            Spacer(modifier = Modifier.height(16.dp)) // 12dp → 16dp
            Text(
                text = "문제가 발생했습니다",
                style = MaterialTheme.typography.titleLarge, // fontSize 대신 style 사용
                color = MaterialTheme.colorScheme.onErrorContainer // 하드코딩 색상 제거
            )
            Spacer(modifier = Modifier.height(12.dp)) // 8dp → 12dp
            Text(
                text = error,
                style = MaterialTheme.typography.bodyLarge, // fontSize 대신 style 사용
                color = MaterialTheme.colorScheme.onErrorContainer // 하드코딩 색상 제거
            )
        }
    }
}