package com.aiprognoz.betting.presentation.screens.payment

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aiprognoz.betting.data.payment.BillingProduct
import com.aiprognoz.betting.data.payment.PaymentRepository
import com.aiprognoz.betting.domain.models.User
import com.aiprognoz.betting.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PaymentUiState(
    val isLoading: Boolean = false,
    val isPurchasing: Boolean = false,
    val error: String? = null,
    val successMessage: String? = null,
    val isRuStoreAvailable: Boolean = true
)

@HiltViewModel
class PaymentViewModel @Inject constructor(
    private val paymentRepository: PaymentRepository,
    authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PaymentUiState())
    val uiState: StateFlow<PaymentUiState> = _uiState.asStateFlow()

    val user: StateFlow<User?> = authRepository.currentUser
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val products: List<BillingProduct> = paymentRepository.getAvailableProducts()
        .filter { it !is BillingProduct.VipSubscription }

    init {
        checkRuStoreAvailability()
    }

    private fun checkRuStoreAvailability() {
        viewModelScope.launch {
            val available = paymentRepository.isRuStoreAvailable()
            _uiState.value = _uiState.value.copy(isRuStoreAvailable = available)
        }
    }

    fun purchaseProduct(activity: Activity, product: BillingProduct) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isPurchasing = true, error = null)

            paymentRepository.purchasePredictions(activity, product)
                .onSuccess { predictions ->
                    _uiState.value = _uiState.value.copy(
                        isPurchasing = false,
                        successMessage = "Добавлено $predictions прогнозов!"
                    )
                }
                .onFailure { e ->
                    _uiState.value = _uiState.value.copy(
                        isPurchasing = false,
                        error = e.message ?: "Ошибка покупки"
                    )
                }
        }
    }

    fun restorePurchases() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            paymentRepository.restorePurchases()
                .onSuccess {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        successMessage = "Покупки восстановлены"
                    )
                }
                .onFailure { e ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = e.message ?: "Ошибка восстановления"
                    )
                }
        }
    }

    fun clearMessages() {
        _uiState.value = _uiState.value.copy(error = null, successMessage = null)
    }
}
