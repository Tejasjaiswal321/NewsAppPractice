package com.example.todoapppractice.ui.screen.person

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.todoapppractice.domain.model.SettlementSuggestion
import com.example.todoapppractice.ui.component.SplitBlue
import com.example.todoapppractice.ui.component.SplitwiseTopBar
import com.example.todoapppractice.ui.state.UiEvent
import com.example.todoapppractice.ui.util.CurrencyFormatter
import kotlinx.coroutines.flow.collectLatest
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun PersonDetailScreen(
    userId: Long,
    onBack: () -> Unit,
    onSnackbar: (String) -> Unit,
    viewModel: PersonDetailViewModel = koinViewModel { parametersOf(userId) }
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.uiEvents.collectLatest { event ->
            when (event) {
                is UiEvent.ShowSnackbar -> onSnackbar(event.message)
                is UiEvent.NavigateBack -> onBack()
                else -> {}
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        SplitwiseTopBar(showBack = true, onBack = onBack)

        Spacer(modifier = Modifier.height(20.dp))

        when (val state = uiState) {
            is PersonUiState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            is PersonUiState.Empty -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Person not found")
                }
            }

            is PersonUiState.Error -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = state.message,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }

            is PersonUiState.Success -> {
                val person = state.person

                // Person header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .width(180.dp)
                            .background(SplitBlue)
                            .padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = person.displayName)
                    }
                    Text(
                        text = CurrencyFormatter.formatBalance(person.netBalancePaise),
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(30.dp))

                // Settlement suggestions
                if (person.settlements.isEmpty()) {
                    Text("All settled up!")
                } else {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        items(
                            items = person.settlements,
                            key = { "${it.fromUserId}_${it.toUserId}" }
                        ) { suggestion ->
                            SettlementRow(
                                suggestion = suggestion,
                                currentUserId = person.userId,
                                onSettleClick = { viewModel.onSettleClicked(suggestion) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SettlementRow(
    suggestion: SettlementSuggestion,
    currentUserId: Long,
    onSettleClick: () -> Unit
) {
    val isCreditor = suggestion.toUserId == currentUserId
    val formattedAmount = CurrencyFormatter.formatAmount(suggestion.amountPaise)
    val label = if (isCreditor) {
        "Will get $formattedAmount from"
    } else {
        "Will pay $formattedAmount to"
    }
    val otherPersonName = if (isCreditor) suggestion.fromName else suggestion.toName

    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(label)
        Spacer(modifier = Modifier.width(12.dp))
        Box(
            modifier = Modifier
                .background(SplitBlue)
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Text(otherPersonName)
        }
        Spacer(modifier = Modifier.weight(1f))
        Button(onClick = onSettleClick) {
            Text("Settle")
        }
    }
}
