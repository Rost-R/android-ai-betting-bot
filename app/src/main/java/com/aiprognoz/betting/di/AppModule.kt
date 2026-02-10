package com.aiprognoz.betting.di

import android.content.Context
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

    // TODO: Добавить провайдеры для:
    // - Retrofit (The Odds API, OpenAI, Gemini)
    // - Room Database
    // - Firebase Auth
    // - Payment Managers (RuStore, YooKassa)
}
