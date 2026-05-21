package com.example.todoapppractice.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp

val SplitBlue = Color(0xFFBFD4F6)

@Composable
fun SplitwiseTopBar(
    showBack: Boolean = false,
    onBack: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(SplitBlue)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (showBack) {
            Text(
                text = "←",
                modifier = Modifier
                    .clickable { onBack?.invoke() }
                    .padding(end = 16.dp),
                style = MaterialTheme.typography.headlineMedium
            )
        }
        Text(
            text = "SPLITWISE",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.weight(1f),
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun TabItem(
    title: String,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .border(width = 1.dp, color = Color.Gray)
            .clickable { onClick() }
            .padding(vertical = 14.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = title,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            textDecoration = if (isSelected) TextDecoration.Underline else null
        )
    }
}
