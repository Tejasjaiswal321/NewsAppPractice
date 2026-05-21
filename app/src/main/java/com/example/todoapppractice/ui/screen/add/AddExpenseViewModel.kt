package com.example.todoapppractice.ui.screen.add

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todoapppractice.domain.usecase.AddExpenseUseCase
import com.example.todoapppractice.ui.state.UiEvent
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AddExpenseUiState(
    val expenseName: String = "",
    val total: String = "",
    val paidBy: String = "",
    val participants: List<String> = listOf("", ""),
    val isLoading: Boolean = false
)

class AddExpenseViewModel(
    private val addExpenseUseCase: AddExpenseUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(AddExpenseUiState())
    val state: StateFlow<AddExpenseUiState> = _state.asStateFlow()

    private val _uiEvents = MutableSharedFlow<UiEvent>()
    val uiEvents: SharedFlow<UiEvent> = _uiEvents.asSharedFlow()

    fun onExpenseNameChange(value: String) {
        _state.update { it.copy(expenseName = value) }
    }

    fun onTotalChange(value: String) {
        // Only allow digits
        if (value.isEmpty() || value.all { it.isDigit() }) {
            _state.update { it.copy(total = value) }
        }
    }

    fun onPaidByChange(value: String) {
        _state.update { it.copy(paidBy = value) }
    }

    fun addParticipant() {
        _state.update { it.copy(participants = it.participants + "") }
    }

    fun onParticipantChange(index: Int, value: String) {
        _state.update { current ->
            val updated = current.participants.toMutableList()
            if (index in updated.indices) {
                updated[index] = value
            }
            current.copy(participants = updated)
        }
    }

    fun onAddExpenseClicked() {
        val current = _state.value
        val totalInt = current.total.toIntOrNull() ?: 0

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            val result = addExpenseUseCase(
                title = current.expenseName,
                totalAmountRupees = totalInt,
                paidByName = current.paidBy,
                participantNames = current.participants
            )

            _state.update { it.copy(isLoading = false) }

            when (result) {
                is AddExpenseUseCase.Result.Success -> {
                    _state.update { AddExpenseUiState() }
                    _uiEvents.emit(UiEvent.ShowSnackbar("Expense added!"))
                    _uiEvents.emit(UiEvent.ExpenseAdded)
                }

                is AddExpenseUseCase.Result.Error -> {
                    _uiEvents.emit(UiEvent.ShowSnackbar(result.message))
                }
            }
        }
    }
}
