package com.aiprognoz.betting.data.payment

import android.app.Activity
import com.aiprognoz.betting.domain.repository.AuthRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PaymentRepository @Inject constructor(
    private val ruStoreBillingService: RuStoreBillingService,
    private val authRepository: AuthRepository
) {
    /**
     * Инициализация платежной системы
     */
    fun initialize() {
        ruStoreBillingService.initialize()
    }

    /**
     * Купить пакет прогнозов
     */
    suspend fun purchasePredictions(
        activity: Activity,
        product: BillingProduct
    ): Result<Int> = withContext(Dispatchers.IO) {
        val result = ruStoreBillingService.purchaseProduct(activity, product)

        when (result) {
            is PurchaseResult.Success -> {
                // Подтверждаем покупку
                ruStoreBillingService.confirmPurchase(result.purchase.purchaseId)

                // Начисляем прогнозы
                if (product is BillingProduct.VipSubscription) {
                    // Активируем VIP на 30 дней
                    authRepository.activateVip(30)
                    Timber.d("VIP activated")
                    Result.success(0)
                } else {
                    // Начисляем прогнозы
                    authRepository.updateBalance(product.predictions)
                    Timber.d("Added ${product.predictions} predictions")
                    Result.success(product.predictions)
                }
            }
            is PurchaseResult.Cancelled -> {
                Result.failure(Exception(result.message))
            }
            is PurchaseResult.Error -> {
                Result.failure(result.exception ?: Exception(result.message))
            }
        }
    }

    /**
     * Восстановить покупки
     */
    suspend fun restorePurchases(): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            // Проверяем непотребленные покупки
            val unconsumed = ruStoreBillingService.getUnconsumedPurchases()
            for (purchase in unconsumed) {
                val product = BillingProduct.fromProductId(purchase.productId)
                if (product != null) {
                    // Подтверждаем и начисляем
                    ruStoreBillingService.confirmPurchase(purchase.purchaseId)
                    if (product is BillingProduct.VipSubscription) {
                        authRepository.activateVip(30)
                    } else {
                        authRepository.updateBalance(product.predictions)
                    }
                }
            }

            // Проверяем активные подписки
            val subscriptions = ruStoreBillingService.getActiveSubscriptions()
            for (sub in subscriptions) {
                if (sub.productId == BillingProduct.VipSubscription.productId) {
                    authRepository.activateVip(30)
                }
            }

            Timber.d("Restored ${unconsumed.size} purchases and ${subscriptions.size} subscriptions")
            Result.success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "Failed to restore purchases")
            Result.failure(e)
        }
    }

    /**
     * Получить продукты для отображения
     */
    fun getAvailableProducts(): List<BillingProduct> {
        return BillingProduct.allProducts
    }

    /**
     * Проверить доступность RuStore
     */
    suspend fun isRuStoreAvailable(): Boolean {
        return ruStoreBillingService.checkAvailability()
    }
}
