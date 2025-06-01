package com.example.slowclock

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.example.slowclock.data.model.Schedule
import com.google.firebase.Timestamp
import java.util.Calendar

@Composable
fun TimelineScreen(modifier: Modifier = Modifier) {
    val calendar1 = Calendar.getInstance()
    calendar1.set(Calendar.HOUR_OF_DAY, 8)
    calendar1.set(Calendar.MINUTE, 0)
    calendar1.set(Calendar.SECOND, 0)
    val calendar2 = Calendar.getInstance()
    calendar2.set(Calendar.HOUR_OF_DAY, 11)
    calendar2.set(Calendar.MINUTE, 0)
    calendar2.set(Calendar.SECOND, 0)
    val calendar3 = Calendar.getInstance()
    calendar3.set(Calendar.HOUR_OF_DAY, 12)
    calendar3.set(Calendar.MINUTE, 0)
    calendar3.set(Calendar.SECOND, 0)
    val calendar4 = Calendar.getInstance()
    calendar4.set(Calendar.HOUR_OF_DAY, 13)
    calendar4.set(Calendar.MINUTE, 0)
    calendar4.set(Calendar.SECOND, 0)
    val calendar5 = Calendar.getInstance()
    calendar5.set(Calendar.HOUR_OF_DAY, 14)
    calendar5.set(Calendar.MINUTE, 0)
    calendar5.set(Calendar.SECOND, 0)
    val calendar6 = Calendar.getInstance()
    calendar6.set(Calendar.HOUR_OF_DAY, 15)
    calendar6.set(Calendar.MINUTE, 0)
    calendar6.set(Calendar.SECOND, 0)
    val calendar7 = Calendar.getInstance()
    calendar7.set(Calendar.HOUR_OF_DAY, 16)
    calendar7.set(Calendar.MINUTE, 0)
    calendar7.set(Calendar.SECOND, 0)
    val calendar8 = Calendar.getInstance()
    calendar8.set(Calendar.HOUR_OF_DAY, 17)
    calendar8.set(Calendar.MINUTE, 0)
    calendar8.set(Calendar.SECOND, 0)
    val calendar9 = Calendar.getInstance()
    calendar9.set(Calendar.HOUR_OF_DAY, 18)
    calendar9.set(Calendar.MINUTE, 0)
    calendar9.set(Calendar.SECOND, 0)
    val Events = listOf(
        Schedule(title="아침 식사", startTime= Timestamp(calendar1.time), isCompleted = true),
        Schedule(title="병원 방문",startTime= Timestamp(calendar2.time), isCompleted = true),
        Schedule(title="점심 식사",startTime= Timestamp(calendar3.time), isCompleted = true),
        Schedule(title="약 먹기",startTime= Timestamp(calendar4.time), isSkipped = true),
        Schedule(title="걷기", startTime= Timestamp(calendar5.time), isCompleted = true),
        Schedule(title="치매 예방 운동",startTime= Timestamp(calendar6.time)),
        Schedule(title="저녁 식사",startTime= Timestamp(calendar7.time)),
        Schedule(title="약 먹기",startTime= Timestamp(calendar8.time)),
        Schedule(title="잘 준비",startTime= Timestamp(calendar9.time))
    )

    BoxWithConstraints(modifier= Modifier.fillMaxSize()) {

        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "오늘의 타임라인",
                fontSize = 20.sp,
                color = Color.Blue,
                fontWeight = Bold,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Timeline(date = "2025년 6월 1일",
                items=Events,
                height= this@BoxWithConstraints.maxHeight,
                modifier=Modifier.align(Alignment.CenterHorizontally)
            )
        }
    }

}

@Preview
@Composable
private fun TimelineScreenPreview(){
    TimelineScreen()
}

