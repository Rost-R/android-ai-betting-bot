package com.aiprognoz.betting.presentation.screens.matches

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aiprognoz.betting.domain.models.Match
import com.aiprognoz.betting.domain.models.Sport
import com.aiprognoz.betting.domain.repository.MatchRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class MatchesUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val selectedSport: Sport? = null
)

@HiltViewModel
class MatchesViewModel @Inject constructor(
    private val matchRepository: MatchRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MatchesUiState())
    val uiState: StateFlow<MatchesUiState> = _uiState.asStateFlow()

    val sports: StateFlow<List<Sport>> = matchRepository.getActiveSports()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val matches: StateFlow<List<Match>> = matchRepository.getUpcomingMatches()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        refreshData()
    }

    fun refreshData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            // Refresh sports
            matchRepository.refreshSports().onFailure { e ->
                _uiState.value = _uiState.value.copy(error = e.message)
            }

            // Refresh matches for popular sports
            val popularSports = listOf(
                "soccer_epl",
                "soccer_spain_la_liga",
                "soccer_germany_bundesliga",
                "basketball_nba",
                "icehockey_nhl"
            )
            matchRepository.refreshAllMatches(popularSports)

            _uiState.value = _uiState.value.copy(isLoading = false)
        }
    }

    fun selectSport(sport: Sport?) {
        _uiState.value = _uiState.value.copy(selectedSport = sport)
        if (sport != null) {
            viewModelScope.launch {
                matchRepository.refreshMatches(sport.key)
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}
