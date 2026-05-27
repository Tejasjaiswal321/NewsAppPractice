package com.example.todoapppractice.ui.state

/**
 * One-shot UI events emitted via SharedFlow.
 * These are consumed once and never replayed on recomposition.
 */
sealed interface UiEvent {
    data class ShowSnackbar(val message: String) : UiEvent
    data class NavigateToPerson(val userId: Long) : UiEvent
    data object NavigateBack : UiEvent
    data object ExpenseAdded : UiEvent
}
