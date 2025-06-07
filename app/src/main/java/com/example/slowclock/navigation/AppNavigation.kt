package com.example.slowclock.navigation

import RecommendationScreen
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
import com.example.slowclock.TimelineScreen
import com.example.slowclock.ui.addschedule.AddScheduleScreen
import com.example.slowclock.ui.common.components.BottomNavigationBar
import com.example.slowclock.ui.done.DoneScreen
import com.example.slowclock.ui.main.MainScreen
import com.example.slowclock.ui.main.MainViewModel
import com.example.slowclock.ui.profile.ProfileScreen
import com.example.slowclock.ui.settings.SettingsScreen

// app/src/main/java/com/example/slowclock/navigation/AppNavigation.kt

@Composable
fun AppNavigation() {
    val mainViewModel: MainViewModel = viewModel()
    val uiState by mainViewModel.uiState.collectAsState()
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
            composable("done") { DoneScreen(
                completed = uiState.todaySchedules.filter { it.isCompleted },
                remaining = uiState.todaySchedules.filter { !it.isCompleted },
                onToggleComplete = { schedule ->
                    mainViewModel.toggleScheduleComplete(schedule.id)
                }
            ) }
            composable("timeline") { TimelineScreen() }
            composable("settings") { SettingsScreen() }

            composable("add_schedule") {
                AddScheduleScreen(
                    onNavigateBack = { success ->
                        if (success) {
                            navController.previousBackStackEntry
                                ?.savedStateHandle
                                ?.set("schedule_added", true)
                        }
                        navController.popBackStack()
                    },
                    onNavigateToRecommendation = {
                        navController.navigate("recommendation") // 추천 화면으로 이동
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
                    },
                    onNavigateToRecommendation = {
                        navController.navigate("recommendation") // 추천 화면으로 이동
                    }
                )
            }

            composable("profile") {
                ProfileScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable("recommendation") {
                RecommendationScreen() // 추천 스크린 컴포저블
            }
        }
    }
}