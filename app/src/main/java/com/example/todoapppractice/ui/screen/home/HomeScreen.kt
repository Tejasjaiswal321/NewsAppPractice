package com.example.todoapppractice.ui.screen.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.todoapppractice.ui.component.SplitBlue
import com.example.todoapppractice.ui.component.TabItem
import com.example.todoapppractice.ui.screen.add.AddExpenseScreen
import com.example.todoapppractice.ui.screen.add.AddExpenseViewModel
import com.example.todoapppractice.ui.screen.balances.BalanceScreen
import com.example.todoapppractice.ui.screen.balances.BalancesViewModel
import com.example.todoapppractice.ui.screen.history.HistoryScreen
import com.example.todoapppractice.ui.screen.history.HistoryViewModel
import com.example.todoapppractice.ui.state.UiEvent
import org.koin.androidx.compose.koinViewModel

@Composable
fun HomeScreen(
    onNavigateToPerson: (Long) -> Unit,
    snackbarHostState: SnackbarHostState,
    addExpenseViewModel: AddExpenseViewModel = koinViewModel(),
    balancesViewModel: BalancesViewModel = koinViewModel(),
    historyViewModel: HistoryViewModel = koinViewModel()
) {
    var selectedTab by rememberSaveable { mutableIntStateOf(0) }

    // Collect states
    val addState by addExpenseViewModel.state.collectAsStateWithLifecycle()
    val balances by balancesViewModel.balances.collectAsStateWithLifecycle()
    val history by historyViewModel.history.collectAsStateWithLifecycle()

    // Collect UI events from all VMs
    LaunchedEffect(Unit) {
        addExpenseViewModel.uiEvents.collect { event ->
            when (event) {
                is UiEvent.ShowSnackbar -> snackbarHostState.showSnackbar(event.message)
                is UiEvent.ExpenseAdded -> { /* stay on add tab or switch */ }
                else -> {}
            }
        }
    }

    LaunchedEffect(Unit) {
        balancesViewModel.uiEvents.collect { event ->
            when (event) {
                is UiEvent.ShowSnackbar -> snackbarHostState.showSnackbar(event.message)
                is UiEvent.NavigateToPerson -> onNavigateToPerson(event.userId)
                else -> {}
            }
        }
    }

    LaunchedEffect(Unit) {
        historyViewModel.uiEvents.collect { event ->
            when (event) {
                is UiEvent.ShowSnackbar -> snackbarHostState.showSnackbar(event.message)
                else -> {}
            }
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {

        // ── Top Bar ──
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(SplitBlue)
                .padding(vertical = 18.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "SPLITWISE",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }

        // ── Tabs ──
        Row(modifier = Modifier.fillMaxWidth()) {
            TabItem(
                title = "ADD",
                isSelected = selectedTab == 0,
                modifier = Modifier.weight(1f)
            ) { selectedTab = 0 }

            TabItem(
                title = "Balances",
                isSelected = selectedTab == 1,
                modifier = Modifier.weight(1f)
            ) { selectedTab = 1 }

            TabItem(
                title = "History",
                isSelected = selectedTab == 2,
                modifier = Modifier.weight(1f)
            ) { selectedTab = 2 }
        }

        // ── Tab Content ──
        when (selectedTab) {
            0 -> AddExpenseScreen(
                state = addState,
                onExpenseNameChange = addExpenseViewModel::onExpenseNameChange,
                onTotalChange = addExpenseViewModel::onTotalChange,
                onPaidByChange = addExpenseViewModel::onPaidByChange,
                onAddParticipant = addExpenseViewModel::addParticipant,
                onParticipantChange = addExpenseViewModel::onParticipantChange,
                onAddExpense = addExpenseViewModel::onAddExpenseClicked
            )

            1 -> BalanceScreen(
                balances = balances,
                onPersonClicked = balancesViewModel::onPersonClicked,
                onSimplifyAll = balancesViewModel::onSimplifyAllClicked
            )

            2 -> HistoryScreen(
                history = history,
                onDeleteItem = historyViewModel::onDeleteItem
            )
        }
    }
}
