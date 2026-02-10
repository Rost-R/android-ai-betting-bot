package com.aiprognoz.betting.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.aiprognoz.betting.presentation.screens.home.HomeScreen

/**
 * Основная навигация приложения
 */
@Composable
fun BettingApp() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "home"
    ) {
        composable("home") {
            HomeScreen(navController = navController)
        }

        // TODO: Добавить остальные экраны
        // composable("matches") { MatchesScreen(navController) }
        // composable("prediction/{matchId}") { PredictionScreen(navController) }
        // composable("express") { ExpressScreen(navController) }
        // composable("vip") { VipScreen(navController) }
        // composable("payment") { PaymentScreen(navController) }
        // composable("profile") { ProfileScreen(navController) }
    }
}
