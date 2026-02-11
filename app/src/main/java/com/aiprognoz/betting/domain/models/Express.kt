package com.aiprognoz.betting.domain.models

import java.time.Instant

/**
 * Доменная модель экспресса дня (VIP)
 */
data class Express(
    val id: String,
    val date: Instant,
    val predictions: List<ExpressPrediction>,
    val totalOdds: Double,
    val gptAnalysis: String,
    val geminiAnalysis: String,
    val createdAt: Instant
) {
    val matchCount: Int
        get() = predictions.size
}

data class ExpressPrediction(
    val matchId: String,
    val homeTeam: String,
    val awayTeam: String,
    val prediction: String,
    val odds: Double
)
