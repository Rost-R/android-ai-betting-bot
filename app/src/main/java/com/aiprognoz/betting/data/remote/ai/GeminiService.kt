package com.aiprognoz.betting.data.remote.ai

import com.aiprognoz.betting.BuildConfig
import com.aiprognoz.betting.domain.models.AiModel
import com.aiprognoz.betting.domain.models.Match
import com.aiprognoz.betting.domain.models.Prediction
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import com.google.ai.client.generativeai.type.generationConfig
import timber.log.Timber
import java.time.Instant
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GeminiService @Inject constructor() {

    private val generativeModel: GenerativeModel by lazy {
        GenerativeModel(
            modelName = "gemini-pro",
            apiKey = BuildConfig.GEMINI_API_KEY,
            generationConfig = generationConfig {
                temperature = 0.7f
                topK = 40
                topP = 0.95f
                maxOutputTokens = 2048
            }
        )
    }

    suspend fun generatePrediction(match: Match): Result<Prediction> {
        return try {
            val prompt = buildPredictionPrompt(match)

            val response = generativeModel.generateContent(
                content {
                    text(SYSTEM_PROMPT)
                    text(prompt)
                }
            )

            val responseText = response.text ?: ""
            val prediction = parsePredictionResponse(match, responseText)

            Timber.d("Gemini generated prediction for ${match.displayName}: ${prediction.prediction}")
            Result.success(prediction)
        } catch (e: Exception) {
            Timber.e(e, "Failed to generate Gemini prediction for ${match.displayName}")
            Result.failure(e)
        }
    }

    suspend fun generateExpressAnalysis(matches: List<Match>): Result<String> {
        return try {
            val prompt = buildExpressPrompt(matches)

            val response = generativeModel.generateContent(
                content {
                    text(EXPRESS_SYSTEM_PROMPT)
                    text(prompt)
                }
            )

            val analysis = response.text ?: "Анализ недоступен"
            Timber.d("Generated express analysis for ${matches.size} matches")
            Result.success(analysis)
        } catch (e: Exception) {
            Timber.e(e, "Failed to generate express analysis")
            Result.failure(e)
        }
    }

    private fun buildPredictionPrompt(match: Match): String {
        return buildString {
            appendLine("Проанализируй матч и дай прогноз:")
            appendLine()
            appendLine("Матч: ${match.homeTeam} vs ${match.awayTeam}")
            appendLine("Спорт: ${match.sportTitle}")
            appendLine("Дата: ${match.commenceTime}")
            if (match.hasOdds) {
                appendLine("Коэффициенты:")
                appendLine("- Победа ${match.homeTeam}: ${match.homeOdds}")
                match.drawOdds?.let { appendLine("- Ничья: $it") }
                appendLine("- Победа ${match.awayTeam}: ${match.awayOdds}")
            }
            appendLine()
            appendLine("Ответ дай в формате:")
            appendLine("ПРОГНОЗ: [home/draw/away]")
            appendLine("УВЕРЕННОСТЬ: [число от 1 до 100]%")
            appendLine("АНАЛИЗ: [детальный анализ на 2-3 абзаца]")
        }
    }

    private fun buildExpressPrompt(matches: List<Match>): String {
        return buildString {
            appendLine("Проанализируй следующие матчи для экспресса дня:")
            appendLine()
            matches.forEachIndexed { index, match ->
                appendLine("${index + 1}. ${match.homeTeam} vs ${match.awayTeam}")
                appendLine("   Спорт: ${match.sportTitle}")
                if (match.hasOdds) {
                    appendLine("   Коэффициенты: П1=${match.homeOdds} X=${match.drawOdds ?: "-"} П2=${match.awayOdds}")
                }
                appendLine()
            }
            appendLine("Дай общий анализ экспресса и рекомендации по каждому матчу.")
        }
    }

    private fun parsePredictionResponse(match: Match, response: String): Prediction {
        val predictionMatch = Regex("ПРОГНОЗ:\\s*(home|draw|away)", RegexOption.IGNORE_CASE)
            .find(response)
        val confidenceMatch = Regex("УВЕРЕННОСТЬ:\\s*(\\d+)%?", RegexOption.IGNORE_CASE)
            .find(response)
        val analysisMatch = Regex("АНАЛИЗ:\\s*(.+)", RegexOption.DOT_MATCHES_ALL)
            .find(response)

        val prediction = predictionMatch?.groupValues?.get(1)?.lowercase() ?: "home"
        val confidence = confidenceMatch?.groupValues?.get(1)?.toIntOrNull() ?: 60
        val analysis = analysisMatch?.groupValues?.get(1)?.trim() ?: response

        val odds = when (prediction) {
            "home" -> match.homeOdds ?: 1.5
            "draw" -> match.drawOdds ?: 3.0
            "away" -> match.awayOdds ?: 2.5
            else -> 1.5
        }

        return Prediction(
            id = UUID.randomUUID().toString(),
            matchId = match.id,
            homeTeam = match.homeTeam,
            awayTeam = match.awayTeam,
            prediction = prediction,
            confidence = confidence.coerceIn(1, 100),
            odds = odds,
            analysis = analysis,
            aiModel = AiModel.GEMINI,
            createdAt = Instant.now(),
            isVip = false
        )
    }

    companion object {
        private const val SYSTEM_PROMPT = """Ты - профессиональный спортивный аналитик с многолетним опытом.
Твоя задача - анализировать спортивные матчи и давать обоснованные прогнозы.

Учитывай:
- Текущую форму команд
- Историю личных встреч
- Домашнее/гостевое преимущество
- Травмы и дисквалификации
- Мотивацию команд

Давай честную оценку, не завышай уверенность без оснований.
Отвечай на русском языке."""

        private const val EXPRESS_SYSTEM_PROMPT = """Ты - эксперт по составлению экспресс-ставок.
Проанализируй предложенные матчи и дай рекомендации для экспресса дня.

Учитывай:
- Надежность каждого исхода
- Общий коэффициент экспресса
- Риски и потенциальную прибыль
- Корреляцию между матчами

Дай развернутый анализ на русском языке."""
    }
}
