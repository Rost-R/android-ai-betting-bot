package com.aiprognoz.betting.domain.models

import java.time.Instant

/**
 * Доменная модель прогноза
 */
data class Prediction(
    val id: String,
    val matchId: String,
    val homeTeam: String,
    val awayTeam: String,
    val prediction: String,           // "home", "draw", "away"
    val confidence: Int,              // 1-100%
    val odds: Double,
    val analysis: String,             // Детальный анализ от AI
    val aiModel: AiModel,
    val createdAt: Instant,
    val isVip: Boolean = false
)

enum class AiModel {
    GPT4O,
    GEMINI
}
