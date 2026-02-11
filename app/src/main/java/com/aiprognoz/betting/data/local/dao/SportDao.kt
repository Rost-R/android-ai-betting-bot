package com.aiprognoz.betting.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.aiprognoz.betting.data.local.entity.SportEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SportDao {

    @Query("SELECT * FROM sports WHERE active = 1 ORDER BY title ASC")
    fun getActiveSports(): Flow<List<SportEntity>>

    @Query("SELECT * FROM sports ORDER BY title ASC")
    fun getAllSports(): Flow<List<SportEntity>>

    @Query("SELECT * FROM sports WHERE `key` = :sportKey")
    suspend fun getSportByKey(sportKey: String): SportEntity?

    @Query("SELECT * FROM sports WHERE `group` = :group AND active = 1 ORDER BY title ASC")
    fun getSportsByGroup(group: String): Flow<List<SportEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSports(sports: List<SportEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSport(sport: SportEntity)

    @Query("DELETE FROM sports")
    suspend fun clearAll()

    @Query("SELECT COUNT(*) FROM sports WHERE active = 1")
    suspend fun getActiveSportsCount(): Int
}
