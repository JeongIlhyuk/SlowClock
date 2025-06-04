package com.example.slowclock
//
//import android.app.DatePickerDialog
//import android.widget.DatePicker
//import androidx.compose.foundation.clickable
//import androidx.compose.foundation.gestures.detectHorizontalDragGestures
//import androidx.compose.foundation.layout.BoxWithConstraints
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.layout.padding
//// import androidx.compose.foundation.layout.size // Not used in this snippet
//// import androidx.compose.material.icons.Icons // Not used in this snippet
//// import androidx.compose.material.icons.filled.DateRange // Not used in this snippet
//// import androidx.compose.material3.FloatingActionButton // Commented out in your code
//import androidx.compose.material3.Text
//// import androidx.compose.material3.Icon // Not used in this snippet
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.LaunchedEffect
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.remember
//import androidx.compose.runtime.setValue
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.input.pointer.pointerInput
//import androidx.compose.ui.platform.LocalContext
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import com.example.slowclock.data.model.Schedule
//// import com.google.firebase.Timestamp // Not used in this snippet
//// import com.google.type.Date // Potential naming conflict, consider aliasing or removing if not used
//import java.text.SimpleDateFormat
//import java.util.Calendar
//import java.util.Locale
//// Assuming you have a Timeline composable defined elsewhere, e.g.:
//// @Composable
//// fun Timeline(items: List<Schedule>, height: Dp, modifier: Modifier = Modifier) { /* ... */ }
//
//@Composable
//fun TimelineScreen(modifier: Modifier = Modifier) {
//
//    val context = LocalContext.current
//
//    val formatter = remember { SimpleDateFormat("yyyy년 MM월 dd일", Locale.KOREA) }
//    var hasSwiped by remember { mutableStateOf(false) }
//    val calendar = remember { Calendar.getInstance() } // Use val for remember if not reassigning the Calendar instance itself
//    val selectedDateString = remember { mutableStateOf(formatter.format(calendar.time)) } // Renamed for clarity
//
//    // State to hold the fetched schedule items
//    var scheduleItems by remember { mutableStateOf<List<Schedule>>(emptyList()) }
//
//    // This would ideally be in a ViewModel, but for simplicity here:
//    // LaunchedEffect to fetch data when selectedDateString.value changes
//    LaunchedEffect(selectedDateString.value) {
//        // --- THIS IS WHERE YOU WOULD FETCH YOUR DATA ---
//        // For example, if you have a suspend function in a ViewModel or repository:
//        // scheduleItems = viewModel.getSchedulesForDate(calendar.time) // Pass java.util.Date or formatted string
//
//        // Placeholder: Simulate fetching data
//        println("Fetching items for date: ${selectedDateString.value}")
//        // In a real app, parse selectedDateString.value back to a Date object
//        // or use calendar.time to query your database.
//        scheduleItems = loadSchedulesForDate(calendar.time) // Replace with your actual data fetching logic
//    }
//
//
//    val datePickerDialog =
//        DatePickerDialog(
//            context,
//            { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
//                calendar.set(year, month, dayOfMonth)
//                selectedDateString.value = formatter.format(calendar.time)
//            },
//            calendar.get(Calendar.YEAR),
//            calendar.get(Calendar.MONTH),
//            calendar.get(Calendar.DAY_OF_MONTH)
//        )
//
//    BoxWithConstraints(
//        modifier = modifier // Use the passed modifier
//            .fillMaxSize()
//            .padding(top = 50.dp)
//    ) {
//        Column(
//            modifier = Modifier
//                .fillMaxWidth()
//                .pointerInput(Unit) {
//                    detectHorizontalDragGestures(
//                        onDragStart = {
//                            hasSwiped = false
//                        }
//                    ) { _, dragAmount ->
//                        if (!hasSwiped) {
//                            val daysToAdd = when {
//                                dragAmount > 100 -> -1 // Swipe right (older date)
//                                dragAmount < -100 -> 1  // Swipe left (newer date)
//                                else -> 0
//                            }
//                            if (daysToAdd != 0) {
//                                calendar.add(Calendar.DAY_OF_MONTH, daysToAdd)
//                                selectedDateString.value = formatter.format(calendar.time)
//                                hasSwiped = true
//                            }
//                        }
//                    }
//                }
//        ) {
//            Text(
//                text = "일정 타임라인",
//                fontSize = 20.sp,
//                color = Color.Blue,
//                fontWeight = FontWeight.Bold,
//                modifier = Modifier
//                    .align(Alignment.CenterHorizontally)
//                    .clickable {
//                        datePickerDialog.show()
//                    }
//            )
//            Text(
//                text = selectedDateString.value,
//                fontSize = 15.sp,
//                color = Color.Gray,
//                modifier = Modifier
//                    .align(Alignment.CenterHorizontally)
//                    .clickable {
//                        datePickerDialog.show()
//                    }
//            )
//
//            // Ensure you have a Timeline composable defined that accepts these parameters
//            if (scheduleItems.isNotEmpty()) { // Optionally, only show Timeline if there are items
//                androidx.media3.common.Timeline(
//                    items = scheduleItems, // Now you're providing the fetched items
//                    height = this@BoxWithConstraints.maxHeight
//                    // modifier = Modifier.fillMaxWidth() // Add other modifiers as needed
//                )
//            } else {
//                // Optionally, show a placeholder if there are no items
//                Text(
//                    text = "선택된 날짜에 일정이 없습니다.",
//                    modifier = Modifier
//                        .align(Alignment.CenterHorizontally)
//                        .padding(16.dp)
//                )
//            }
//        }
//        /*FloatingActionButton(
//            // ... your FAB code
//        )*/
//    }
//}
//
//// Placeholder for your data fetching logic.
//// Replace this with your actual database/network call.
//// This function would likely be a suspend function if it involves I/O.
//fun loadSchedulesForDate(date: java.util.Date): List<Schedule> {
//    // Simulate fetching data.
//    // In a real app, you would query your database (e.g., Room, Firestore)
//    // based on the given 'date'.
//    val sdf = SimpleDateFormat("yyyyMMdd", Locale.KOREA)
//    val formattedDate = sdf.format(date)
//
//    // Example dummy data structure
//    if (formattedDate == sdf.format(Calendar.getInstance().time)) { // If it's today
//        return listOf(
//            Schedule(androidx.compose.animation.graphics.vector.Timestamp.now(), "오늘의 첫 번째 일정", "세부 내용1"),
//            Schedule(androidx.compose.animation.graphics.vector.Timestamp.now(), "오늘의 두 번째 일정", "세부 내용2")
//        )
//    } else {
//        return emptyList()
//    }
//}
//
//// Dummy Schedule data class (make sure your actual Schedule class matches)
//// data class Schedule(val time: Timestamp, val title: String, val description: String)
//
//// Dummy Timeline composable (replace with your actual Timeline composable)
//@Composable
//fun Timeline(items: List<Schedule>, height: androidx.compose.ui.unit.Dp, modifier: Modifier = Modifier) {
//    Column(modifier = modifier.padding(8.dp)) {
//        items.forEach { schedule ->
//            Text("일정: ${schedule.title} at ${SimpleDateFormat("HH:mm", Locale.getDefault()).format(schedule.timestamp.toDate())}")
//        }
//    }
//}