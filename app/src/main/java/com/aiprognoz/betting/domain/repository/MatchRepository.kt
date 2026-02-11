package com.aiprognoz.betting.domain.repository

import com.aiprognoz.betting.domain.models.Match
import com.aiprognoz.betting.domain.models.Sport
import kotlinx.coroutines.flow.Flow

/**
 * Repository интерфейс для работы с матчами и спортами
 */
interface MatchRepository {

    // ==================== Sports ====================

    /**
     * Получить все активные виды спорта
     */
    fun getActiveSports(): Flow<List<Sport>>

    /**
     * Обновить список видов спорта из API
     */
    suspend fun refreshSports(): Result<Unit>

    // ==================== Matches ====================

    /**
     * Получить все матчи
     */
    fun getAllMatches(): Flow<List<Match>>

    /**
     * Получить матчи по виду спорта
     */
    fun getMatchesBySport(sportKey: String): Flow<List<Match>>

    /**
     * Получить предстоящие матчи
     */
    fun getUpcomingMatches(limit: Int = 50): Flow<List<Match>>

    /**
     * Получить матч по ID
     */
    suspend fun getMatchById(matchId: String): Match?

    /**
     * Обновить матчи для вида спорта из API
     */
    suspend fun refreshMatches(sportKey: String): Result<Unit>

    /**
     * Обновить матчи для нескольких видов спорта
     */
    suspend fun refreshAllMatches(sportKeys: List<String>): Result<Unit>

    /**
     * Удалить старые матчи
     */
    suspend fun cleanupOldMatches()
}
