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
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
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
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun PersonDetailScreen(
    userId: Long,
    onBack: () -> Unit,
    onSnackbar: (String) -> Unit,
    viewModel: PersonDetailViewModel = koinViewModel { parametersOf(userId) }
) {
    val summary by viewModel.summary.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.uiEvents.collect { event ->
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

        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
            return
        }

        val person = summary
        if (person == null) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("Person not found")
            }
            return
        }

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
                text = person.formattedBalance,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(30.dp))

        // Settlement suggestions
        if (person.settlements.isEmpty()) {
            Text("All settled up!")
        } else {
            person.settlements.forEach { suggestion ->
                SettlementRow(
                    suggestion = suggestion,
                    currentUserId = person.userId,
                    onSettleClick = { viewModel.onSettleClicked(suggestion) }
                )
                Spacer(modifier = Modifier.height(16.dp))
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
    val label = if (isCreditor) {
        "Will get ${suggestion.formattedAmount} from"
    } else {
        "Will pay ${suggestion.formattedAmount} to"
    }
    val otherPersonName = if (isCreditor) suggestion.fromName else suggestion.toName

    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(label)
        Spacer(modifier = Modifier.width(12.dp))
        Box(
            modifier = Modifier
                .background(SplitBlue)
                .padding(horizontal = 24.dp, vertical = 10.dp)
        ) {
            Text(otherPersonName)
        }
        Spacer(modifier = Modifier.width(16.dp))
        Button(onClick = onSettleClick) {
            Text("SETTLE")
        }
    }
}
