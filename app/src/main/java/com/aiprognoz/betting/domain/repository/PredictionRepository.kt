package com.aiprognoz.betting.domain.repository

import com.aiprognoz.betting.domain.models.AiModel
import com.aiprognoz.betting.domain.models.Match
import com.aiprognoz.betting.domain.models.Prediction
import kotlinx.coroutines.flow.Flow

/**
 * Repository интерфейс для работы с прогнозами
 */
interface PredictionRepository {

    /**
     * Получить все прогнозы
     */
    fun getAllPredictions(): Flow<List<Prediction>>

    /**
     * Получить последние прогнозы
     */
    fun getRecentPredictions(limit: Int = 20): Flow<List<Prediction>>

    /**
     * Получить прогнозы для матча
     */
    fun getPredictionsForMatch(matchId: String): Flow<List<Prediction>>

    /**
     * Получить прогноз по ID
     */
    suspend fun getPredictionById(predictionId: String): Prediction?

    /**
     * Сгенерировать новый прогноз с помощью AI
     */
    suspend fun generatePrediction(match: Match, aiModel: AiModel = AiModel.GPT4O): Result<Prediction>

    /**
     * Сохранить прогноз
     */
    suspend fun savePrediction(prediction: Prediction)

    /**
     * Удалить старые прогнозы
     */
    suspend fun cleanupOldPredictions()
}
