package com.aiprognoz.betting.presentation.screens.prediction

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.aiprognoz.betting.domain.models.AiModel
import com.aiprognoz.betting.domain.models.Prediction
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PredictionScreen(
    navController: NavController,
    viewModel: PredictionViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Прогноз") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Назад")
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when {
                uiState.isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                uiState.match == null -> {
                    Text(
                        text = "Матч не найден",
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                else -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Match info card
                        val match = uiState.match!!
                        Card(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = match.sportTitle,
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = match.homeTeam,
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center
                                )
                                Text(
                                    text = "vs",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = match.awayTeam,
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center
                                )

                                if (match.hasOdds) {
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceEvenly
                                    ) {
                                        OddsDisplay("П1", match.homeOdds)
                                        match.drawOdds?.let { OddsDisplay("X", it) }
                                        OddsDisplay("П2", match.awayOdds)
                                    }
                                }
                            }
                        }

                        // Balance info
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Ваш баланс:")
                                Text(
                                    text = if (uiState.user?.isVipActive == true) {
                                        "VIP (безлимит)"
                                    } else {
                                        "${uiState.user?.balance ?: 0} прогнозов"
                                    },
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        // Prediction result or generate buttons
                        if (uiState.prediction != null) {
                            PredictionResultCard(prediction = uiState.prediction!!)
                        } else {
                            // Generate buttons
                            if (uiState.isGenerating) {
                                Card(
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(32.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        CircularProgressIndicator()
                                        Spacer(modifier = Modifier.height(16.dp))
                                        Text("AI анализирует матч...")
                                    }
                                }
                            } else {
                                Button(
                                    onClick = { viewModel.generatePrediction(AiModel.GPT4O) },
                                    modifier = Modifier.fillMaxWidth(),
                                    enabled = uiState.hasBalance
                                ) {
                                    Text("🤖 Получить прогноз GPT-4o")
                                }

                                OutlinedButton(
                                    onClick = { viewModel.generatePrediction(AiModel.GEMINI) },
                                    modifier = Modifier.fillMaxWidth(),
                                    enabled = uiState.hasBalance
                                ) {
                                    Text("✨ Получить прогноз Gemini")
                                }

                                if (!uiState.hasBalance) {
                                    Text(
                                        text = "Пополните баланс для получения прогноза",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.error,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.fillMaxWidth()
                                    )

                                    Button(
                                        onClick = { navController.navigate("payment") },
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text("💰 Пополнить баланс")
                                    }
                                }
                            }
                        }

                        // Error message
                        uiState.error?.let { error ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.errorContainer
                                )
                            ) {
                                Text(
                                    text = error,
                                    modifier = Modifier.padding(16.dp),
                                    color = MaterialTheme.colorScheme.onErrorContainer
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun OddsDisplay(label: String, odds: Double?) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium
        )
        Text(
            text = odds?.let { String.format("%.2f", it) } ?: "-",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun PredictionResultCard(prediction: Prediction) {
    val dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")
        .withZone(ZoneId.systemDefault())

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Прогноз от ${prediction.aiModel.name}",
                    style = MaterialTheme.typography.labelMedium
                )
                Text(
                    text = dateFormatter.format(prediction.createdAt),
                    style = MaterialTheme.typography.labelSmall
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Prediction outcome
            val outcomeText = when (prediction.prediction) {
                "home" -> "Победа ${prediction.homeTeam}"
                "draw" -> "Ничья"
                "away" -> "Победа ${prediction.awayTeam}"
                else -> prediction.prediction
            }

            Text(
                text = outcomeText,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Column {
                    Text("Коэффициент", style = MaterialTheme.typography.labelSmall)
                    Text(
                        text = String.format("%.2f", prediction.odds),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                Column {
                    Text("Уверенность", style = MaterialTheme.typography.labelSmall)
                    Text(
                        text = "${prediction.confidence}%",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Divider()
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Анализ:",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = prediction.analysis,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}
