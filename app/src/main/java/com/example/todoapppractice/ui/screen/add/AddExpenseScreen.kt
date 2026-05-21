package com.example.todoapppractice.ui.screen.add

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@Composable
fun AddExpenseScreen(
    state: AddExpenseUiState,
    onExpenseNameChange: (String) -> Unit,
    onTotalChange: (String) -> Unit,
    onPaidByChange: (String) -> Unit,
    onAddParticipant: () -> Unit,
    onParticipantChange: (Int, String) -> Unit,
    onAddExpense: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {
        LabelText("Expense")
        OutlinedTextField(
            value = state.expenseName,
            onValueChange = onExpenseNameChange,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        LabelText("Total")
        OutlinedTextField(
            value = state.total,
            onValueChange = onTotalChange,
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            placeholder = { Text("0") }
        )

        Spacer(modifier = Modifier.height(24.dp))

        LabelText("Paid by")
        OutlinedTextField(
            value = state.paidBy,
            onValueChange = onPaidByChange,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            LabelText("Participants")
            Spacer(modifier = Modifier.width(12.dp))
            Button(
                onClick = onAddParticipant,
                contentPadding = PaddingValues(0.dp),
                modifier = Modifier.size(36.dp)
            ) {
                Text("+")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        state.participants.forEachIndexed { index, item ->
            OutlinedTextField(
                value = item,
                onValueChange = { onParticipantChange(index, it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 10.dp)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = onAddExpense,
            modifier = Modifier.fillMaxWidth(),
            enabled = !state.isLoading
        ) {
            Text(if (state.isLoading) "Adding..." else "ADD")
        }
    }
}

@Composable
private fun LabelText(text: String) {
    Text(
        text = text,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier.padding(bottom = 6.dp)
    )
}
