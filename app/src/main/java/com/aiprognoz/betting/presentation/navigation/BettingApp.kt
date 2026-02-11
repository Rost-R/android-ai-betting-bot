package com.aiprognoz.betting.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.aiprognoz.betting.presentation.screens.auth.AuthScreen
import com.aiprognoz.betting.presentation.screens.home.HomeScreen
import com.aiprognoz.betting.presentation.screens.matches.MatchesScreen
import com.aiprognoz.betting.presentation.screens.payment.PaymentScreen
import com.aiprognoz.betting.presentation.screens.prediction.PredictionScreen
import com.aiprognoz.betting.presentation.screens.profile.ProfileScreen

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
        // Auth
        composable("auth") {
            AuthScreen(navController = navController)
        }

        // Home
        composable("home") {
            HomeScreen(navController = navController)
        }

        // Matches list
        composable("matches") {
            MatchesScreen(navController = navController)
        }

        // Prediction for specific match
        composable(
            route = "prediction/{matchId}",
            arguments = listOf(
                navArgument("matchId") { type = NavType.StringType }
            )
        ) {
            PredictionScreen(navController = navController)
        }

        // Express of the day (placeholder)
        composable("express") {
            PlaceholderScreen(
                title = "Экспресс дня",
                description = "VIP функция - ежедневные экспрессы с анализом от GPT-4o и Gemini",
                navController = navController
            )
        }

        // VIP subscription (placeholder)
        composable("vip") {
            PlaceholderScreen(
                title = "VIP Подписка",
                description = "299₽/мес - безлимитные прогнозы и ежедневные экспрессы",
                navController = navController
            )
        }

        // Payment
        composable("payment") {
            PaymentScreen(navController = navController)
        }

        // Profile
        composable("profile") {
            ProfileScreen(navController = navController)
        }
    }
}
