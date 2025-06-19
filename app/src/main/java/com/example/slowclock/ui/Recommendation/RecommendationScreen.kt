package com.example.slowclock.ui.Recommendation

import android.net.Uri
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.slowclock.ui.Recommendation.components.ADHDRecommendation
import com.example.slowclock.ui.Recommendation.components.ElderlyRecommendation
import com.example.slowclock.ui.Recommendation.components.InfoRecommendation
import com.example.slowclock.ui.Recommendation.components.StudentRecommendation

@Composable
fun RecommendationScreen(navController: NavController, modifier: Modifier = Modifier) {
    val userInfo = remember { mutableStateOf("Elderly") }

    Column(modifier = Modifier.fillMaxSize()) {
        Row(modifier = Modifier.padding(8.dp)) {
            Button(onClick = { userInfo.value = "Elderly" }, modifier = Modifier.padding(4.dp)) {
                Text(text = "노인")
            }
            Button(onClick = { userInfo.value = "ADHD" }, modifier = Modifier.padding(4.dp)) {
                Text(text = "ADHD")
            }
            Button(onClick = { userInfo.value = "Student" }, modifier = Modifier.padding(4.dp)) {
                Text(text = "학생")
            }
        }

        when (userInfo.value) {
            "Elderly" -> ElderlyRecommendation(onSelectRecommendation = { selectedTitle ->
                navController.navigate("add_schedule?title=${Uri.encode(selectedTitle)}")
            })
            "ADHD" -> ADHDRecommendation(onSelectRecommendation = { selectedTitle ->
                navController.navigate("add_schedule?title=${Uri.encode(selectedTitle)}")
            })
            else -> StudentRecommendation(onSelectRecommendation = { selectedTitle ->
                navController.navigate("add_schedule?title=${Uri.encode(selectedTitle)}")
            })
        }
    }
}