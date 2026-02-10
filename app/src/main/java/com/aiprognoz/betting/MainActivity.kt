package com.aiprognoz.betting

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.aiprognoz.betting.presentation.navigation.BettingApp
import com.aiprognoz.betting.presentation.theme.AIBettingBotTheme
import dagger.hilt.android.AndroidEntryPoint

/**
 * Main Activity для приложения
 * Использует Jetpack Compose для UI
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Edge-to-edge display
        enableEdgeToEdge()

        setContent {
            AIBettingBotTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    BettingApp()
                }
            }
        }
    }
}
