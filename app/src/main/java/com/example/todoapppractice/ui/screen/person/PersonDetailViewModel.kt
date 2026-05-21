package com.example.todoapppractice.ui.screen.person

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
import kotlinx.coroutines.launch

class PersonDetailViewModel(
    private val userId: Long,
    private val getPersonSummaryUseCase: GetPersonSummaryUseCase,
    private val settleBalanceUseCase: SettleBalanceUseCase
) : ViewModel() {

    private val _summary = MutableStateFlow<PersonSummary?>(null)
    val summary: StateFlow<PersonSummary?> = _summary.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _uiEvents = MutableSharedFlow<UiEvent>()
    val uiEvents: SharedFlow<UiEvent> = _uiEvents.asSharedFlow()

    init {
        loadSummary()
    }

    fun loadSummary() {
        viewModelScope.launch {
            _isLoading.value = true
            _summary.value = getPersonSummaryUseCase(userId)
            _isLoading.value = false
        }
    }

    fun onSettleClicked(suggestion: SettlementSuggestion) {
        viewModelScope.launch {
            settleBalanceUseCase(suggestion)
            _uiEvents.emit(UiEvent.ShowSnackbar("Settled!"))
            loadSummary()
        }
    }
}
