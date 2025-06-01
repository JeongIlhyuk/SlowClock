package com.example.slowclock

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.unit.Dp

import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.slowclock.data.model.Schedule
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun Timeline(
    date: String,
    height: Dp,
    modifier: Modifier = Modifier,
    items: List<Schedule>
) {
    val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault()) // 시간 포맷
    // Timeline 날짜 Text
    Text(
        text = date,
        fontSize = 15.sp,
        color = Color.Gray,
        modifier = modifier
    )

    // 일정 박스
    Box(
        modifier = Modifier
            .height(height)
            .fillMaxWidth()
    ) {
        // Timeline
        Divider(
            color = Color(0xFF3A5CCC),
            modifier = Modifier
                .fillMaxHeight()
                .width(2.dp)
                .align(Alignment.Center)
        )

        // 시간 순으로 일정 정렬
        val allItems = items.sortedBy { it.startTime.seconds }
        val spacing = if (allItems.size > 1) height / (allItems.size+1) else 0.dp

        // Timeline에 일정과 동일한 지점에 Point 구현
        allItems.forEachIndexed { index, _ ->
            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = spacing * index + 50.dp)
                    .width(8.dp)
                    .height(8.dp)
                    .background(Color(0xFF3A5CCC), shape = CircleShape)
            )
        }
        // Timeline에 맞춰 일정 구현
        var twist = true
        allItems.forEachIndexed { index, item ->
            Box(
                modifier = Modifier
                    .align(if (twist) Alignment.TopStart else Alignment.TopEnd)
                    .padding(
                        top = spacing * index + 30.dp,
                        start = 40.dp,
                        end = 40.dp
                    )

            ) {
                // 일정 표시
                Column(
                    modifier = Modifier
                        .background(color = if(item.isCompleted) Color(0xFFADFF2F) else if(item.isSkipped) Color(0xFFFFB6C1) else Color(0xFFE0FFFF))
                        .padding(8.dp)
                ) {
                    Text(text = item.title,
                        fontSize = 16.sp,
                        fontWeight = Bold,
                        modifier=Modifier.align(Alignment.CenterHorizontally)
                    )
                    Text(
                        text = timeFormat.format(item.startTime.toDate()),
                        fontSize = 12.sp,
                        fontWeight = Bold,
                        color=Color.Black,
                        modifier=Modifier.align(Alignment.CenterHorizontally)
                    )
                }
            }
           if(twist) twist = false else twist = true
        }
}

}