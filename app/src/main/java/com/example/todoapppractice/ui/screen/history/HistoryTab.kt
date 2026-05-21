package com.example.todoapppractice.ui.screen.history

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.todoapppractice.ui.state.UiEvent
import org.koin.androidx.compose.koinViewModel

/**
 * Self-contained tab that owns its own ViewModel and event handling.
 */
@Composable
fun HistoryTab(
    snackbarHostState: SnackbarHostState,
    viewModel: HistoryViewModel = koinViewModel()
) {
    val history by viewModel.history.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.uiEvents.collect { event ->
            when (event) {
                is UiEvent.ShowSnackbar -> snackbarHostState.showSnackbar(event.message)
                else -> {}
            }
        }
    }

    HistoryScreen(
        history = history,
        onDeleteItem = viewModel::onDeleteItem
    )
}
