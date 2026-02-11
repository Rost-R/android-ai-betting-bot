package com.aiprognoz.betting.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.aiprognoz.betting.data.local.entity.MatchEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MatchDao {

    @Query("SELECT * FROM matches ORDER BY commenceTime ASC")
    fun getAllMatches(): Flow<List<MatchEntity>>

    @Query("SELECT * FROM matches WHERE sportKey = :sportKey ORDER BY commenceTime ASC")
    fun getMatchesBySport(sportKey: String): Flow<List<MatchEntity>>

    @Query("SELECT * FROM matches WHERE id = :matchId")
    suspend fun getMatchById(matchId: String): MatchEntity?

    @Query("SELECT * FROM matches WHERE commenceTime > :currentTime ORDER BY commenceTime ASC LIMIT :limit")
    fun getUpcomingMatches(currentTime: Long, limit: Int = 50): Flow<List<MatchEntity>>

    @Query("SELECT * FROM matches WHERE isLive = 1 ORDER BY commenceTime ASC")
    fun getLiveMatches(): Flow<List<MatchEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMatches(matches: List<MatchEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMatch(match: MatchEntity)

    @Query("DELETE FROM matches WHERE commenceTime < :threshold")
    suspend fun deleteOldMatches(threshold: Long)

    @Query("DELETE FROM matches")
    suspend fun clearAll()

    @Query("SELECT COUNT(*) FROM matches")
    suspend fun getMatchCount(): Int
}
