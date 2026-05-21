package com.example.todoapppractice.ui.screen.balances

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.todoapppractice.domain.model.UserBalance
import com.example.todoapppractice.ui.component.SplitBlue
import com.example.todoapppractice.ui.util.CurrencyFormatter

@Composable
fun BalanceScreen(
    balances: List<UserBalance>,
    isSimplifyOn: Boolean,
    onPersonClicked: (UserBalance) -> Unit,
    onSimplifyToggle: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {
        if (balances.isEmpty()) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text("No expenses yet")
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f)
            ) {
                items(
                    items = balances,
                    key = { it.userId }
                ) { item ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Box(
                            modifier = Modifier
                                .width(180.dp)
                                .background(SplitBlue)
                                .clickable { onPersonClicked(item) }
                                .padding(vertical = 12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = item.displayName)
                        }
                        Text(
                            text = CurrencyFormatter.formatBalance(item.balancePaise),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        SimplifyToggle(isSimplifyOn, onSimplifyToggle)
    }
}

@Composable
private fun SimplifyToggle(isSimplifyOn: Boolean, onSimplifyToggle: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 12.dp, end = 12.dp, top = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {

        Column(
            modifier = Modifier.weight(1f)
        ) {

            Text(
                text = "Simplify Expenses",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = if (isSimplifyOn) {
                    "Showing minimized transactions"
                } else {
                    "Showing original balances"
                },
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
        }

        Switch(
            checked = isSimplifyOn,
            onCheckedChange = {
                onSimplifyToggle()
            }
        )
    }
}
