package com.example.todoapppractice.ui.screen.history

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.todoapppractice.domain.model.HistoryItem
import com.example.todoapppractice.ui.util.CurrencyFormatter
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun HistoryScreen(
    history: List<HistoryItem>,
    onDeleteItem: (HistoryItem) -> Unit
) {
    when {
        history.isEmpty() -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("No history yet")
            }

        }

        else -> {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(
                    items = history,
                    key = { item ->
                        when (item) {
                            is HistoryItem.ExpenseHistoryItem -> "expense_${item.expenseId}"
                            is HistoryItem.SettlementHistoryItem -> "settlement_${item.settlementId}"
                        }
                    }
                ) { item ->
                    when (item) {
                        is HistoryItem.ExpenseHistoryItem -> ExpenseCard(item, onDeleteItem)
                        is HistoryItem.SettlementHistoryItem -> SettlementCard(item, onDeleteItem)
                    }
                }
            }
        }
    }


}

@Composable
private fun ExpenseCard(
    item: HistoryItem.ExpenseHistoryItem,
    onDelete: (HistoryItem) -> Unit
) {
    HistoryCard(
        cardColor = Color(0xFFF5F5F5),
        onDelete = { onDelete(item) }
    ) {
        Text(
            text = item.title,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = "${CurrencyFormatter.formatAmount(item.totalAmountPaise)} paid by ${item.paidByName}",
            fontSize = 14.sp,
            color = Color.DarkGray
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Participants: ${item.participantNames.joinToString(", ")}",
            fontSize = 12.sp,
            color = Color.Gray
        )
        Text(
            text = formatTimestamp(item.createdAt),
            fontSize = 11.sp,
            color = Color.Gray
        )
    }


}

@Composable
private fun SettlementCard(
    item: HistoryItem.SettlementHistoryItem,
    onDelete: (HistoryItem) -> Unit
) {
    HistoryCard(
        cardColor = Color(0xFFE8F5E9),
        onDelete = { onDelete(item) }
    ) {
        Text(
            text = "${item.fromName} paid ${CurrencyFormatter.formatAmount(item.amountPaise)} to ${item.toName}",
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
        )
        Spacer(Modifier.size(4.dp))
        Text(
            text = formatTimestamp(item.createdAt),
            fontSize = 11.sp,
            color = Color.Gray
        )
    }
}


@Composable
private fun HistoryCard(
    cardColor: Color,
    onDelete: () -> Unit,
    columnContent: @Composable () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = cardColor)
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f)) {
                columnContent()
            }
            CrossButton { onDelete() }
        }
    }
}


@Composable
private fun CrossButton(
    onDelete: () -> Unit,
) {
    IconButton(
        onClick = { onDelete() },
        modifier = Modifier
            .clip(CircleShape)
            .size(36.dp)
            .background(Color(0xFFFFCDD2))
    ) {
        Text("X", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.Red)
    }
}


private val formatter = DateTimeFormatter.ofPattern(
    "MMM dd, hh:mm:ss a",
    Locale.getDefault()
)

private fun formatTimestamp(millis: Long): String {
    return Instant.ofEpochMilli(millis)
        .atZone(ZoneId.systemDefault())
        .format(formatter)
}
