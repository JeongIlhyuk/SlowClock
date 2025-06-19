// app/src/main/java/com/example/slowclock/navigation/AppNavigation.kt
package com.example.slowclock.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.slowclock.ui.recommendation.RecommendationScreen
import com.example.slowclock.ui.timeline.TimelineScreen
import com.example.slowclock.ui.addschedule.AddScheduleScreen
import com.example.slowclock.ui.common.components.BottomNavigationBar
import com.example.slowclock.ui.done.DoneScreen
import com.example.slowclock.ui.main.MainScreen
import com.example.slowclock.ui.main.MainViewModel
import com.example.slowclock.ui.profile.ProfileScreen
import com.example.slowclock.ui.settings.SettingsScreen

@Composable
fun AppNavigation() {
    val mainViewModel: MainViewModel = viewModel()
    val navController = rememberNavController()
    val currentRoute by navController.currentBackStackEntryFlow.collectAsState(
        initial = navController.currentBackStackEntry
    )

    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                currentRoute = currentRoute?.destination?.route ?: "main",
                onNavigate = { navController.navigate(it) }
            )
        }
    ) { innerPadding ->

        NavHost(
            navController = navController,
            startDestination = "main",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("main") {
                val result = navController.currentBackStackEntry
                    ?.savedStateHandle
                    ?.get<Boolean>("schedule_added")

                // 🔥 MainViewModel을 직접 전달
                MainScreen(
                    viewModel = mainViewModel, // 명시적으로 전달
                    shouldRefresh = result == true,
                    onAddSchedule = {
                        navController.navigate("add_schedule")
                    },
                    onEditSchedule = { scheduleId ->
                        navController.navigate("edit_schedule/$scheduleId")
                    },
                    onNavigateToProfile = {
                        navController.navigate("profile")
                    },
                    onNavigateToSettings = {
                        navController.navigate("settings_share_code")
                    },
                    onRefreshHandled = {
                        navController.currentBackStackEntry
                            ?.savedStateHandle
                            ?.remove<Boolean>("schedule_added")
                    }
                )
            }

            composable("done") {
                DoneScreen(
                    mainViewModel = mainViewModel
                )
            }

            composable("timeline") { TimelineScreen(mainViewModel) }
            composable("settings") { SettingsScreen() }

            composable(
                route = "add_schedule?title={title}",
                arguments = listOf(
                    navArgument("title") {
                        nullable = true; defaultValue = ""
                    }
                )
                ) { backStackEntry ->
                val titleArg = backStackEntry.arguments?.getString("title")
                AddScheduleScreen(
                    initialTitle = titleArg,
                    onNavigateBack = { success ->
                        if (success) {
                            navController.previousBackStackEntry
                                ?.savedStateHandle
                                ?.set("schedule_added", true)

                        }else{
                            navController.navigate("main") {
                                popUpTo("main") { inclusive = false }
                                launchSingleTop = true
                            }
                        }
                    },
                    onNavigateToRecommendation = {
                        navController.navigate("recommendation"){
                            popUpTo("main")
                        }
                    }
                )
            }

            composable("edit_schedule/{scheduleId}") { backStackEntry ->
                val scheduleId = backStackEntry.arguments?.getString("scheduleId") ?: ""
                AddScheduleScreen(
                    scheduleId = scheduleId,
                    onNavigateBack = { success ->
                        if (success) {
                            navController.previousBackStackEntry
                                ?.savedStateHandle
                                ?.set("schedule_added", true)
                        }
                        else{
                            navController.navigate("main") {
                                popUpTo("main") { inclusive = false }
                                launchSingleTop = true
                            }
                        }
                    },
                    onNavigateToRecommendation = {
                        navController.navigate("recommendation")
                    }
                )
            }

            composable("profile") {
                ProfileScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable("recommendation") {
                RecommendationScreen(
                    navController = navController
                )
            }

            composable("settings_share_code") {
                com.example.slowclock.ui.settings.SettingsScreenShareCode(
                    onReturn = { navController.popBackStack() }
                )
            }
        }
    }
}