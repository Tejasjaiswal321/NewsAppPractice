package com.example.todoapppractice.ui.screen.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todoapppractice.domain.model.HistoryItem
import com.example.todoapppractice.domain.usecase.DeleteExpenseUseCase
import com.example.todoapppractice.domain.usecase.DeleteSettlementUseCase
import com.example.todoapppractice.domain.usecase.GetHistoryUseCase
import com.example.todoapppractice.ui.state.UiEvent
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HistoryViewModel(
    getHistoryUseCase: GetHistoryUseCase,
    private val deleteExpenseUseCase: DeleteExpenseUseCase,
    private val deleteSettlementUseCase: DeleteSettlementUseCase
) : ViewModel() {

    val history: StateFlow<List<HistoryItem>> = getHistoryUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _uiEvents = MutableSharedFlow<UiEvent>()
    val uiEvents: SharedFlow<UiEvent> = _uiEvents.asSharedFlow()

    fun onDeleteItem(item: HistoryItem) {
        viewModelScope.launch {
            when (item) {
                is HistoryItem.ExpenseHistoryItem -> {
                    deleteExpenseUseCase(item.expenseId)
                    _uiEvents.emit(UiEvent.ShowSnackbar("Expense deleted"))
                }

                is HistoryItem.SettlementHistoryItem -> {
                    deleteSettlementUseCase(item.settlementId)
                    _uiEvents.emit(UiEvent.ShowSnackbar("Settlement deleted"))
                }
            }
        }
    }
}
