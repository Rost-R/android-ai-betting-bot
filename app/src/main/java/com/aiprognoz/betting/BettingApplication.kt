package com.aiprognoz.betting

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

/**
 * Application class for AI Betting Bot
 * Инициализирует Hilt, Firebase, и другие сервисы
 */
@HiltAndroidApp
class BettingApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        // Initialize Timber для логирования (только в debug)
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        Timber.d("BettingApplication initialized")
    }
}
