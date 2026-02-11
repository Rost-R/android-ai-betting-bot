package com.aiprognoz.betting.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.aiprognoz.betting.data.local.dao.MatchDao
import com.aiprognoz.betting.data.local.dao.PredictionDao
import com.aiprognoz.betting.data.local.dao.SportDao
import com.aiprognoz.betting.data.local.dao.UserDao
import com.aiprognoz.betting.data.local.entity.MatchEntity
import com.aiprognoz.betting.data.local.entity.PredictionEntity
import com.aiprognoz.betting.data.local.entity.SportEntity
import com.aiprognoz.betting.data.local.entity.UserEntity

@Database(
    entities = [
        MatchEntity::class,
        PredictionEntity::class,
        UserEntity::class,
        SportEntity::class
    ],
    version = 1,
    exportSchema = true
)
abstract class BettingDatabase : RoomDatabase() {

    abstract fun matchDao(): MatchDao
    abstract fun predictionDao(): PredictionDao
    abstract fun userDao(): UserDao
    abstract fun sportDao(): SportDao

    companion object {
        const val DATABASE_NAME = "betting_database"
    }
}
