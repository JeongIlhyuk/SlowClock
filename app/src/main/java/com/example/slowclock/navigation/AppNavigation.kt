package com.example.slowclock.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.slowclock.ui.addschedule.AddScheduleScreen
import com.example.slowclock.ui.main.MainScreen
import com.example.slowclock.ui.profile.ProfileScreen

// app/src/main/java/com/example/slowclock/navigation/AppNavigation.kt

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "main"
    ) {
        composable("main") {
            val result = navController.currentBackStackEntry
                ?.savedStateHandle
                ?.get<Boolean>("schedule_added")

            MainScreen(
                shouldRefresh = result == true,
                onAddSchedule = {
                    navController.navigate("add_schedule")
                },
                onEditSchedule = { scheduleId -> // 새로 추가
                    navController.navigate("edit_schedule/$scheduleId")
                },
                onNavigateToProfile = {
                    navController.navigate("profile")
                },
                onRefreshHandled = {
                    navController.currentBackStackEntry
                        ?.savedStateHandle
                        ?.remove<Boolean>("schedule_added")
                }
            )
        }

        composable("add_schedule") {
            AddScheduleScreen(
                onNavigateBack = { success ->
                    if (success) {
                        navController.previousBackStackEntry
                            ?.savedStateHandle
                            ?.set("schedule_added", true)
                    }
                    navController.popBackStack()
                }
            )
        }

        // 편집 화면 추가
        composable("edit_schedule/{scheduleId}") { backStackEntry ->
            val scheduleId = backStackEntry.arguments?.getString("scheduleId") ?: ""
            AddScheduleScreen(
                scheduleId = scheduleId, // 편집 모드
                onNavigateBack = { success ->
                    if (success) {
                        navController.previousBackStackEntry
                            ?.savedStateHandle
                            ?.set("schedule_added", true)
                    }
                    navController.popBackStack()
                }
            )
        }

        composable("profile") {
            ProfileScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}