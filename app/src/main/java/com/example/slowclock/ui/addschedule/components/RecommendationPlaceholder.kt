// app/src/main/java/com/example/slowclock/ui/addschedule/components/RecommendationPlaceholder.kt
package com.example.slowclock.ui.addschedule.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun RecommendationPlaceholder() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer
        ),
        border = BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.outline
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp)
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "🔮 일정 추천 기능",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onTertiaryContainer
                )
                Text(
                    text = "곧 추가됩니다!",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onTertiaryContainer
                )
            }
        }
    }
}