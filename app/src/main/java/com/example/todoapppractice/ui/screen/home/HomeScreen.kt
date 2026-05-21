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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.todoapppractice.ui.component.SplitBlue
import com.example.todoapppractice.ui.component.TabItem
import com.example.todoapppractice.ui.screen.add.AddExpenseTab
import com.example.todoapppractice.ui.screen.balances.BalancesTab
import com.example.todoapppractice.ui.screen.history.HistoryTab

/**
 * HomeScreen is now a thin shell:
 * - Manages tab selection only
 * - Each tab composable owns its own ViewModel and event collection
 * - No VM references leak into this composable
 */
@Composable
fun HomeScreen(
    onNavigateToPerson: (Long) -> Unit,
    snackbarHostState: SnackbarHostState
) {
    var selectedTab by rememberSaveable { mutableIntStateOf(0) }

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
        // Each tab owns its own ViewModel + event handling
        when (selectedTab) {
            0 -> AddExpenseTab(snackbarHostState = snackbarHostState)
            1 -> BalancesTab(
                snackbarHostState = snackbarHostState,
                onNavigateToPerson = onNavigateToPerson
            )
            2 -> HistoryTab(snackbarHostState = snackbarHostState)
        }
    }
}
