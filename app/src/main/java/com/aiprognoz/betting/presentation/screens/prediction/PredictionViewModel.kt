package com.aiprognoz.betting.presentation.screens.prediction

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aiprognoz.betting.domain.models.AiModel
import com.aiprognoz.betting.domain.models.Match
import com.aiprognoz.betting.domain.models.Prediction
import com.aiprognoz.betting.domain.models.User
import com.aiprognoz.betting.domain.repository.AuthRepository
import com.aiprognoz.betting.domain.repository.MatchRepository
import com.aiprognoz.betting.domain.repository.PredictionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PredictionUiState(
    val isLoading: Boolean = false,
    val isGenerating: Boolean = false,
    val error: String? = null,
    val match: Match? = null,
    val prediction: Prediction? = null,
    val user: User? = null,
    val hasBalance: Boolean = false
)

@HiltViewModel
class PredictionViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val matchRepository: MatchRepository,
    private val predictionRepository: PredictionRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val matchId: String = savedStateHandle.get<String>("matchId") ?: ""

    private val _uiState = MutableStateFlow(PredictionUiState())
    val uiState: StateFlow<PredictionUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            // Load match
            val match = matchRepository.getMatchById(matchId)

            // Load user
            val user = authRepository.currentUser.first()

            // Check for existing prediction
            val existingPredictions = predictionRepository.getPredictionsForMatch(matchId).first()
            val prediction = existingPredictions.firstOrNull()

            _uiState.value = _uiState.value.copy(
                isLoading = false,
                match = match,
                user = user,
                prediction = prediction,
                hasBalance = (user?.balance ?: 0) > 0 || user?.isVipActive == true
            )
        }
    }

    fun generatePrediction(aiModel: AiModel = AiModel.GPT4O) {
        val match = _uiState.value.match ?: return
        val user = _uiState.value.user

        // Check balance
        if (user?.balance == 0 && user.isVipActive != true) {
            _uiState.value = _uiState.value.copy(
                error = "Недостаточно прогнозов. Пополните баланс."
            )
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isGenerating = true, error = null)

            // Deduct balance if not VIP
            if (user?.isVipActive != true) {
                authRepository.deductBalance(1)
            }

            val result = predictionRepository.generatePrediction(match, aiModel)

            result.onSuccess { prediction ->
                _uiState.value = _uiState.value.copy(
                    isGenerating = false,
                    prediction = prediction
                )
                // Refresh user data
                val updatedUser = authRepository.currentUser.first()
                _uiState.value = _uiState.value.copy(
                    user = updatedUser,
                    hasBalance = (updatedUser?.balance ?: 0) > 0 || updatedUser?.isVipActive == true
                )
            }.onFailure { e ->
                // Refund balance on error
                if (user?.isVipActive != true) {
                    authRepository.updateBalance(1)
                }
                _uiState.value = _uiState.value.copy(
                    isGenerating = false,
                    error = e.message ?: "Ошибка генерации прогноза"
                )
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}
