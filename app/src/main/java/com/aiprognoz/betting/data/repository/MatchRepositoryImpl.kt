package com.aiprognoz.betting.data.repository

import com.aiprognoz.betting.BuildConfig
import com.aiprognoz.betting.data.local.dao.MatchDao
import com.aiprognoz.betting.data.local.dao.SportDao
import com.aiprognoz.betting.data.local.entity.MatchEntity
import com.aiprognoz.betting.data.remote.api.OddsApiService
import com.aiprognoz.betting.domain.models.Match
import com.aiprognoz.betting.domain.models.Sport
import com.aiprognoz.betting.domain.repository.MatchRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MatchRepositoryImpl @Inject constructor(
    private val oddsApiService: OddsApiService,
    private val matchDao: MatchDao,
    private val sportDao: SportDao
) : MatchRepository {

    private val apiKey: String
        get() = BuildConfig.ODDS_API_KEY

    // ==================== Sports ====================

    override fun getActiveSports(): Flow<List<Sport>> {
        return sportDao.getActiveSports().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun refreshSports(): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val response = oddsApiService.getSports(apiKey)
            if (response.isSuccessful) {
                val sports = response.body() ?: emptyList()
                val entities = sports.map { it.toEntity() }
                sportDao.insertSports(entities)
                Timber.d("Refreshed ${entities.size} sports")
                Result.success(Unit)
            } else {
                val error = "API error: ${response.code()} - ${response.message()}"
                Timber.e(error)
                Result.failure(Exception(error))
            }
        } catch (e: Exception) {
            Timber.e(e, "Failed to refresh sports")
            Result.failure(e)
        }
    }

    // ==================== Matches ====================

    override fun getAllMatches(): Flow<List<Match>> {
        return matchDao.getAllMatches().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getMatchesBySport(sportKey: String): Flow<List<Match>> {
        return matchDao.getMatchesBySport(sportKey).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getUpcomingMatches(limit: Int): Flow<List<Match>> {
        return matchDao.getUpcomingMatches(
            currentTime = System.currentTimeMillis(),
            limit = limit
        ).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getMatchById(matchId: String): Match? {
        return matchDao.getMatchById(matchId)?.toDomain()
    }

    override suspend fun refreshMatches(sportKey: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val response = oddsApiService.getOdds(sport = sportKey, apiKey = apiKey)
            if (response.isSuccessful) {
                val events = response.body() ?: emptyList()
                val entities = events.map { it.toEntity() }
                matchDao.insertMatches(entities)
                Timber.d("Refreshed ${entities.size} matches for $sportKey")
                Result.success(Unit)
            } else {
                val error = "API error: ${response.code()} - ${response.message()}"
                Timber.e(error)
                Result.failure(Exception(error))
            }
        } catch (e: Exception) {
            Timber.e(e, "Failed to refresh matches for $sportKey")
            Result.failure(e)
        }
    }

    override suspend fun refreshAllMatches(sportKeys: List<String>): Result<Unit> = withContext(Dispatchers.IO) {
        var hasError = false
        var lastError: Exception? = null

        for (sportKey in sportKeys) {
            val result = refreshMatches(sportKey)
            if (result.isFailure) {
                hasError = true
                lastError = result.exceptionOrNull() as? Exception
            }
        }

        if (hasError && lastError != null) {
            Result.failure(lastError)
        } else {
            Result.success(Unit)
        }
    }

    override suspend fun cleanupOldMatches() {
        val threshold = System.currentTimeMillis() - (24 * 60 * 60 * 1000) // 24 hours ago
        matchDao.deleteOldMatches(threshold)
        Timber.d("Cleaned up old matches")
    }
}
