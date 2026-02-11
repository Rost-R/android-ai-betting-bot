package com.aiprognoz.betting.data.repository

import com.aiprognoz.betting.data.local.dao.PredictionDao
import com.aiprognoz.betting.data.local.entity.PredictionEntity
import com.aiprognoz.betting.data.remote.ai.GeminiService
import com.aiprognoz.betting.data.remote.ai.OpenAiService
import com.aiprognoz.betting.domain.models.AiModel
import com.aiprognoz.betting.domain.models.Match
import com.aiprognoz.betting.domain.models.Prediction
import com.aiprognoz.betting.domain.repository.PredictionRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PredictionRepositoryImpl @Inject constructor(
    private val predictionDao: PredictionDao,
    private val openAiService: OpenAiService,
    private val geminiService: GeminiService
) : PredictionRepository {

    override fun getAllPredictions(): Flow<List<Prediction>> {
        return predictionDao.getAllPredictions().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getRecentPredictions(limit: Int): Flow<List<Prediction>> {
        return predictionDao.getRecentPredictions(limit).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getPredictionsForMatch(matchId: String): Flow<List<Prediction>> {
        return predictionDao.getPredictionsForMatch(matchId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getPredictionById(predictionId: String): Prediction? {
        return predictionDao.getPredictionById(predictionId)?.toDomain()
    }

    override suspend fun generatePrediction(match: Match, aiModel: AiModel): Result<Prediction> =
        withContext(Dispatchers.IO) {
            val result = when (aiModel) {
                AiModel.GPT4O -> openAiService.generatePrediction(match)
                AiModel.GEMINI -> geminiService.generatePrediction(match)
            }

            result.onSuccess { prediction ->
                savePrediction(prediction)
                Timber.d("Saved prediction ${prediction.id} for match ${match.id}")
            }

            result
        }

    override suspend fun savePrediction(prediction: Prediction) {
        predictionDao.insertPrediction(PredictionEntity.fromDomain(prediction))
    }

    override suspend fun cleanupOldPredictions() {
        // Удаляем прогнозы старше 30 дней
        val threshold = System.currentTimeMillis() - (30L * 24 * 60 * 60 * 1000)
        predictionDao.deleteOldPredictions(threshold)
        Timber.d("Cleaned up old predictions")
    }
}
