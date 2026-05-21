package com.example.todoapppractice.ui.screen.add

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.todoapppractice.ui.state.UiEvent
import org.koin.androidx.compose.koinViewModel

/**
 * Self-contained tab that owns its own ViewModel and event handling.
 * HomeScreen simply places this composable — no VM leaking upward.
 */
@Composable
fun AddExpenseTab(
    snackbarHostState: SnackbarHostState,
    viewModel: AddExpenseViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.uiEvents.collect { event ->
            when (event) {
                is UiEvent.ShowSnackbar -> snackbarHostState.showSnackbar(event.message)
                is UiEvent.ExpenseAdded -> { /* stay on add tab */ }
                else -> {}
            }
        }
    }

    AddExpenseScreen(
        state = state,
        onExpenseNameChange = viewModel::onExpenseNameChange,
        onTotalChange = viewModel::onTotalChange,
        onPaidByChange = viewModel::onPaidByChange,
        onAddParticipant = viewModel::addParticipant,
        onParticipantChange = viewModel::onParticipantChange,
        onAddExpense = viewModel::onAddExpenseClicked
    )
}
