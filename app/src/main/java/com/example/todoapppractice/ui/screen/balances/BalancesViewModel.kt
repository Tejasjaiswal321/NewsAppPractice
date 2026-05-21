package com.example.todoapppractice.ui.screen.balances

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todoapppractice.domain.model.UserBalance
import com.example.todoapppractice.domain.usecase.GetBalancesUseCase
import com.example.todoapppractice.domain.usecase.SimplifyBalancesUseCase
import com.example.todoapppractice.ui.state.UiEvent
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class BalancesViewModel(
    getBalancesUseCase: GetBalancesUseCase,
    private val simplifyBalancesUseCase: SimplifyBalancesUseCase
) : ViewModel() {

    val balances: StateFlow<List<UserBalance>> = getBalancesUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _uiEvents = MutableSharedFlow<UiEvent>()
    val uiEvents: SharedFlow<UiEvent> = _uiEvents.asSharedFlow()

    fun onPersonClicked(userBalance: UserBalance) {
        viewModelScope.launch {
            _uiEvents.emit(UiEvent.NavigateToPerson(userBalance.userId))
        }
    }

    fun onSimplifyAllClicked() {
        viewModelScope.launch {
            val count = simplifyBalancesUseCase.execute()
            if (count > 0) {
                _uiEvents.emit(UiEvent.ShowSnackbar("Created $count settlements"))
            } else {
                _uiEvents.emit(UiEvent.ShowSnackbar("All balances already settled"))
            }
        }
    }
}
