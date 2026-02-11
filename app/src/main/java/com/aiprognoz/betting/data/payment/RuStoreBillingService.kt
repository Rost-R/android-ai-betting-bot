package com.aiprognoz.betting.data.payment

import android.app.Activity
import android.content.Context
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Продукты для покупки
 */
sealed class BillingProduct(
    val productId: String,
    val predictions: Int,
    val price: Int
) {
    object Predictions1 : BillingProduct("predictions_1", 1, 10)
    object Predictions5 : BillingProduct("predictions_5", 5, 45)
    object Predictions10 : BillingProduct("predictions_10", 10, 80)
    object Predictions25 : BillingProduct("predictions_25", 25, 175)
    object Predictions50 : BillingProduct("predictions_50", 50, 300)
    object Predictions100 : BillingProduct("predictions_100", 100, 500)
    object VipSubscription : BillingProduct("vip_monthly", 0, 299)

    companion object {
        val allProducts = listOf(
            Predictions1, Predictions5, Predictions10,
            Predictions25, Predictions50, Predictions100,
            VipSubscription
        )

        fun fromProductId(productId: String): BillingProduct? {
            return allProducts.find { it.productId == productId }
        }
    }
}

/**
 * Данные о покупке
 */
data class PurchaseInfo(
    val purchaseId: String,
    val productId: String
)

/**
 * Результат покупки
 */
sealed class PurchaseResult {
    data class Success(val product: BillingProduct, val purchase: PurchaseInfo) : PurchaseResult()
    data class Cancelled(val message: String) : PurchaseResult()
    data class Error(val message: String, val exception: Throwable? = null) : PurchaseResult()
}

/**
 * Сервис биллинга RuStore
 *
 * Временная заглушка для сборки. Реальная интеграция будет добавлена позже.
 */
@Singleton
class RuStoreBillingService @Inject constructor(
    private val context: Context
) {
    private var isInitialized = false

    /**
     * Инициализация клиента
     */
    fun initialize() {
        try {
            // TODO: Реальная инициализация RuStore SDK
            // billingClient = RuStoreBillingClientFactory.create(...)
            isInitialized = true
            Timber.d("RuStore Billing Service initialized (stub mode)")
        } catch (e: Exception) {
            Timber.e(e, "Failed to initialize RuStore Billing Service")
        }
    }

    /**
     * Проверить доступность RuStore
     */
    suspend fun checkAvailability(): Boolean {
        // В реальной версии проверяем наличие RuStore на устройстве
        // Пока возвращаем true для тестирования UI
        return true
    }

    /**
     * Купить продукт
     *
     * В демо-режиме симулируем успешную покупку
     */
    suspend fun purchaseProduct(
        activity: Activity,
        product: BillingProduct
    ): PurchaseResult {
        return try {
            // TODO: Реальный вызов RuStore SDK
            // val result = billingClient?.purchases?.purchaseProduct(productId = product.productId)

            // Демо-режим: симулируем успешную покупку
            Timber.d("Demo purchase: ${product.productId}")

            val purchaseInfo = PurchaseInfo(
                purchaseId = "demo_${System.currentTimeMillis()}",
                productId = product.productId
            )

            PurchaseResult.Success(product, purchaseInfo)
        } catch (e: Exception) {
            Timber.e(e, "Purchase error")
            PurchaseResult.Error("Ошибка: ${e.message}", e)
        }
    }

    /**
     * Получить непотребленные покупки
     */
    suspend fun getUnconsumedPurchases(): List<PurchaseInfo> {
        // TODO: Реальный вызов RuStore SDK
        return emptyList()
    }

    /**
     * Подтвердить покупку (consume)
     */
    suspend fun confirmPurchase(purchaseId: String): Boolean {
        return try {
            // TODO: Реальный вызов RuStore SDK
            Timber.d("Purchase confirmed (demo): $purchaseId")
            true
        } catch (e: Exception) {
            Timber.e(e, "Failed to confirm purchase")
            false
        }
    }

    /**
     * Получить активные подписки
     */
    suspend fun getActiveSubscriptions(): List<PurchaseInfo> {
        // TODO: Реальный вызов RuStore SDK
        return emptyList()
    }
}
