package com.example.todoapppractice.ui.screen.person

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todoapppractice.data.datastore.SimplifyPreferences
import com.example.todoapppractice.domain.model.PersonSummary
import com.example.todoapppractice.domain.model.SettlementSuggestion
import com.example.todoapppractice.domain.usecase.GetPersonSummaryUseCase
import com.example.todoapppractice.domain.usecase.SettleBalanceUseCase
import com.example.todoapppractice.ui.state.UiEvent
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/**
 * Sealed UI state that prevents illegal combinations
 * (e.g. loading=true + data!=null, or loading=false + data=null).
 */
sealed interface PersonUiState {
    data object Loading : PersonUiState
    data class Success(val person: PersonSummary) : PersonUiState
    data object Empty : PersonUiState
    data class Error(val message: String) : PersonUiState
}

class PersonDetailViewModel(
    private val userId: Long,
    private val getPersonSummaryUseCase: GetPersonSummaryUseCase,
    private val settleBalanceUseCase: SettleBalanceUseCase,
    private val simplifyPreferences: SimplifyPreferences
) : ViewModel() {

    private val _uiState = MutableStateFlow<PersonUiState>(PersonUiState.Loading)
    val uiState: StateFlow<PersonUiState> = _uiState.asStateFlow()

    private val _uiEvents = MutableSharedFlow<UiEvent>()
    val uiEvents: SharedFlow<UiEvent> = _uiEvents.asSharedFlow()

    init {
        // Re-load summary whenever simplify preference changes
        viewModelScope.launch {
            simplifyPreferences.isSimplifyOn.collectLatest { isSimplified ->
                loadSummary(isSimplified)
            }
        }
    }

    private suspend fun loadSummary(isSimplified: Boolean) {
        _uiState.value = PersonUiState.Loading
        try {
            val summary = getPersonSummaryUseCase(userId, isSimplified)
            _uiState.value = if (summary != null) {
                PersonUiState.Success(summary)
            } else {
                PersonUiState.Empty
            }
        } catch (e: Exception) {
            _uiState.value = PersonUiState.Error(e.message ?: "Unknown error")
        }
    }

    fun onSettleClicked(suggestion: SettlementSuggestion) {
        viewModelScope.launch {
            try {
                settleBalanceUseCase(suggestion)
                _uiEvents.emit(UiEvent.ShowSnackbar("Settled!"))
                val isSimplified = simplifyPreferences.isSimplifyOn.first()
                loadSummary(isSimplified)
            } catch (e: Exception) {
                _uiEvents.emit(UiEvent.ShowSnackbar("Settlement failed: ${e.message}"))
            }
        }
    }
}
