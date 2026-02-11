package com.aiprognoz.betting.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.aiprognoz.betting.data.local.entity.PredictionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PredictionDao {

    @Query("SELECT * FROM predictions ORDER BY createdAt DESC")
    fun getAllPredictions(): Flow<List<PredictionEntity>>

    @Query("SELECT * FROM predictions WHERE id = :predictionId")
    suspend fun getPredictionById(predictionId: String): PredictionEntity?

    @Query("SELECT * FROM predictions WHERE matchId = :matchId ORDER BY createdAt DESC")
    fun getPredictionsForMatch(matchId: String): Flow<List<PredictionEntity>>

    @Query("SELECT * FROM predictions WHERE isVip = 1 ORDER BY createdAt DESC")
    fun getVipPredictions(): Flow<List<PredictionEntity>>

    @Query("SELECT * FROM predictions ORDER BY createdAt DESC LIMIT :limit")
    fun getRecentPredictions(limit: Int = 20): Flow<List<PredictionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPrediction(prediction: PredictionEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPredictions(predictions: List<PredictionEntity>)

    @Query("DELETE FROM predictions WHERE id = :predictionId")
    suspend fun deletePrediction(predictionId: String)

    @Query("DELETE FROM predictions WHERE createdAt < :threshold")
    suspend fun deleteOldPredictions(threshold: Long)

    @Query("DELETE FROM predictions")
    suspend fun clearAll()

    @Query("SELECT COUNT(*) FROM predictions")
    suspend fun getPredictionCount(): Int
}
