package com.weathersnap.app.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.weathersnap.app.ui.camera.CameraScreen
import com.weathersnap.app.ui.create_report.CreateReportScreen
import com.weathersnap.app.ui.reports.SavedReportsScreen
import com.weathersnap.app.ui.weather.WeatherScreen

private const val TRANSITION_DURATION = 400

@Composable
fun WeatherSnapNavHost() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Weather.route,
        enterTransition = {
            slideIntoContainer(
                AnimatedContentTransitionScope.SlideDirection.Start,
                tween(TRANSITION_DURATION)
            ) + fadeIn(tween(TRANSITION_DURATION))
        },
        exitTransition = {
            slideOutOfContainer(
                AnimatedContentTransitionScope.SlideDirection.Start,
                tween(TRANSITION_DURATION)
            ) + fadeOut(tween(TRANSITION_DURATION))
        },
        popEnterTransition = {
            slideIntoContainer(
                AnimatedContentTransitionScope.SlideDirection.End,
                tween(TRANSITION_DURATION)
            ) + fadeIn(tween(TRANSITION_DURATION))
        },
        popExitTransition = {
            slideOutOfContainer(
                AnimatedContentTransitionScope.SlideDirection.End,
                tween(TRANSITION_DURATION)
            ) + fadeOut(tween(TRANSITION_DURATION))
        }
    ) {
        composable(Screen.Weather.route) {
            WeatherScreen(
                onNavigateToCreateReport = {
                    navController.navigate(Screen.CreateReport.route)
                },
                onNavigateToSavedReports = {
                    navController.navigate(Screen.SavedReports.route)
                }
            )
        }

        composable(Screen.CreateReport.route) {
            CreateReportScreen(
                onNavigateToCamera = {
                    navController.navigate(Screen.Camera.route)
                },
                onNavigateToReports = {
                    navController.navigate(Screen.SavedReports.route) {
                        popUpTo(Screen.Weather.route)
                    }
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.Camera.route) { backStackEntry ->
            val createReportEntry = remember(backStackEntry) {
                navController.getBackStackEntry(Screen.CreateReport.route)
            }
            val createReportViewModel: com.weathersnap.app.ui.create_report.CreateReportViewModel = androidx.hilt.navigation.compose.hiltViewModel(createReportEntry)
            CameraScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onPhotoCaptured = { path, originalSize, compressedSize ->
                    createReportViewModel.onCameraResultReceived(path, originalSize, compressedSize)
                }
            )
        }

        composable(Screen.SavedReports.route) {
            SavedReportsScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
