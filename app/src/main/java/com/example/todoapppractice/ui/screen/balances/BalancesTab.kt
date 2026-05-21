package com.example.todoapppractice.ui.screen.balances

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.todoapppractice.ui.state.UiEvent
import org.koin.androidx.compose.koinViewModel

/**
 * Self-contained tab that owns its own ViewModel and event handling.
 * Navigates to person detail via [onNavigateToPerson] callback.
 */
@Composable
fun BalancesTab(
    snackbarHostState: SnackbarHostState,
    onNavigateToPerson: (Long) -> Unit,
    viewModel: BalancesViewModel = koinViewModel()
) {
    val balances by viewModel.balances.collectAsStateWithLifecycle()
    val isSimplifyOn by viewModel.isSimplifyOn.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.uiEvents.collect { event ->
            when (event) {
                is UiEvent.ShowSnackbar -> snackbarHostState.showSnackbar(event.message)
                is UiEvent.NavigateToPerson -> onNavigateToPerson(event.userId)
                else -> {}
            }
        }
    }

    BalanceScreen(
        balances = balances,
        isSimplifyOn = isSimplifyOn,
        onPersonClicked = viewModel::onPersonClicked,
        onSimplifyToggle = viewModel::onSimplifyToggleClicked
    )
}
