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

class PersonDetailViewModel(
    private val userId: Long,
    private val getPersonSummaryUseCase: GetPersonSummaryUseCase,
    private val settleBalanceUseCase: SettleBalanceUseCase,
    private val simplifyPreferences: SimplifyPreferences
) : ViewModel() {

    private val _personSummary = MutableStateFlow<PersonSummary?>(null)
    val personSummary: StateFlow<PersonSummary?> = _personSummary.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _uiEvents = MutableSharedFlow<UiEvent>()
    val uiEvents: SharedFlow<UiEvent> = _uiEvents.asSharedFlow()

    init {
        // Re-load personSummary whenever simplify preference changes
        viewModelScope.launch {
            simplifyPreferences.isSimplifyOn.collectLatest { isSimplified ->
                updatePersonSummary(isSimplified)
            }
        }
    }

    fun onSettleClicked(suggestion: SettlementSuggestion) {
        viewModelScope.launch {
            settleBalanceUseCase(suggestion)
            _uiEvents.emit(UiEvent.ShowSnackbar("Settled!"))
            // Reload with current simplify state
            val isSimplified = simplifyPreferences.isSimplifyOn.first()
            updatePersonSummary(isSimplified)
        }
    }

    private suspend fun updatePersonSummary(
        isSimplified: Boolean
    ) {
        _isLoading.value = true
        _personSummary.value = getPersonSummaryUseCase(userId, isSimplified)
        _isLoading.value = false
    }
}
