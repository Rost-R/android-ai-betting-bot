package com.aiprognoz.betting.data.remote.ai

import com.aiprognoz.betting.BuildConfig
import com.aiprognoz.betting.domain.models.AiModel
import com.aiprognoz.betting.domain.models.Match
import com.aiprognoz.betting.domain.models.Prediction
import com.aallam.openai.api.chat.ChatCompletion
import com.aallam.openai.api.chat.ChatCompletionRequest
import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.chat.ChatRole
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.OpenAI
import timber.log.Timber
import java.time.Instant
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OpenAiService @Inject constructor() {

    private val openAI: OpenAI by lazy {
        OpenAI(BuildConfig.OPENAI_API_KEY)
    }

    suspend fun generatePrediction(match: Match): Result<Prediction> {
        return try {
            val prompt = buildPredictionPrompt(match)

            val chatCompletionRequest = ChatCompletionRequest(
                model = ModelId("gpt-4o"),
                messages = listOf(
                    ChatMessage(
                        role = ChatRole.System,
                        content = SYSTEM_PROMPT
                    ),
                    ChatMessage(
                        role = ChatRole.User,
                        content = prompt
                    )
                ),
                temperature = 0.7
            )

            val completion: ChatCompletion = openAI.chatCompletion(chatCompletionRequest)
            val response = completion.choices.firstOrNull()?.message?.content ?: ""

            val prediction = parsePredictionResponse(match, response)
            Timber.d("Generated prediction for ${match.displayName}: ${prediction.prediction}")
            Result.success(prediction)
        } catch (e: Exception) {
            Timber.e(e, "Failed to generate prediction for ${match.displayName}")
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
            aiModel = AiModel.GPT4O,
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
- Травмы и дисквалификации ключевых игроков
- Мотивацию команд
- Статистику последних матчей

Давай честную оценку вероятности, не завышай уверенность без оснований.
Прогноз должен быть на русском языке."""
    }
}
