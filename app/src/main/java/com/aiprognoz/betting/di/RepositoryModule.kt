package com.aiprognoz.betting.di

import com.aiprognoz.betting.data.auth.AuthRepositoryImpl
import com.aiprognoz.betting.data.repository.MatchRepositoryImpl
import com.aiprognoz.betting.data.repository.PredictionRepositoryImpl
import com.aiprognoz.betting.domain.repository.AuthRepository
import com.aiprognoz.betting.domain.repository.MatchRepository
import com.aiprognoz.betting.domain.repository.PredictionRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindMatchRepository(impl: MatchRepositoryImpl): MatchRepository

    @Binds
    @Singleton
    abstract fun bindPredictionRepository(impl: PredictionRepositoryImpl): PredictionRepository

    @Binds
    @Singleton
    abstract fun bindAuthRepository(impl: AuthRepositoryImpl): AuthRepository
}
