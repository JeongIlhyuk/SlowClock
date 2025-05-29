package com.example.slowclock.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.slowclock.ui.addschedule.AddScheduleScreen
import com.example.slowclock.ui.main.MainScreen

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
    }
}