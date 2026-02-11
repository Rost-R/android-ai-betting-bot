package com.aiprognoz.betting.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.aiprognoz.betting.domain.models.AiModel
import com.aiprognoz.betting.domain.models.Prediction
import java.time.Instant

@Entity(tableName = "predictions")
data class PredictionEntity(
    @PrimaryKey
    val id: String,
    val matchId: String,
    val homeTeam: String,
    val awayTeam: String,
    val prediction: String,
    val confidence: Int,
    val odds: Double,
    val analysis: String,
    val aiModel: String,
    val createdAt: Long,
    val isVip: Boolean
) {
    fun toDomain(): Prediction = Prediction(
        id = id,
        matchId = matchId,
        homeTeam = homeTeam,
        awayTeam = awayTeam,
        prediction = prediction,
        confidence = confidence,
        odds = odds,
        analysis = analysis,
        aiModel = AiModel.valueOf(aiModel),
        createdAt = Instant.ofEpochMilli(createdAt),
        isVip = isVip
    )

    companion object {
        fun fromDomain(prediction: Prediction): PredictionEntity = PredictionEntity(
            id = prediction.id,
            matchId = prediction.matchId,
            homeTeam = prediction.homeTeam,
            awayTeam = prediction.awayTeam,
            prediction = prediction.prediction,
            confidence = prediction.confidence,
            odds = prediction.odds,
            analysis = prediction.analysis,
            aiModel = prediction.aiModel.name,
            createdAt = prediction.createdAt.toEpochMilli(),
            isVip = prediction.isVip
        )
    }
}
