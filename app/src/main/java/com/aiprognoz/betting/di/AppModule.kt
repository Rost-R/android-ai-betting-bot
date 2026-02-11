package com.aiprognoz.betting.di

import android.content.Context
import androidx.room.Room
import com.aiprognoz.betting.data.local.BettingDatabase
import com.aiprognoz.betting.data.local.dao.MatchDao
import com.aiprognoz.betting.data.local.dao.PredictionDao
import com.aiprognoz.betting.data.local.dao.SportDao
import com.aiprognoz.betting.data.local.dao.UserDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt DI Module для основных зависимостей приложения
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideApplicationContext(@ApplicationContext context: Context): Context {
        return context
    }

    // ==================== Database ====================

    @Provides
    @Singleton
    fun provideBettingDatabase(@ApplicationContext context: Context): BettingDatabase {
        return Room.databaseBuilder(
            context,
            BettingDatabase::class.java,
            BettingDatabase.DATABASE_NAME
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun provideMatchDao(database: BettingDatabase): MatchDao {
        return database.matchDao()
    }

    @Provides
    @Singleton
    fun providePredictionDao(database: BettingDatabase): PredictionDao {
        return database.predictionDao()
    }

    @Provides
    @Singleton
    fun provideUserDao(database: BettingDatabase): UserDao {
        return database.userDao()
    }

    @Provides
    @Singleton
    fun provideSportDao(database: BettingDatabase): SportDao {
        return database.sportDao()
    }
}
