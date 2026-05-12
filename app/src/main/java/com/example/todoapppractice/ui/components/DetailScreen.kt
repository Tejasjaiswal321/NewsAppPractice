package com.example.todoapppractice.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.todoapppractice.ui.NewsViewModel

@Composable
fun DetailsScreen(
    viewModel: NewsViewModel
) {
    val article by viewModel.selectedUIItem.collectAsStateWithLifecycle()

    Column(Modifier.padding(16.dp)) {
        AsyncImage(
            model = article?.imageUrl,
            contentDescription = null,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(12.dp))
        Text(article?.title ?: "", fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))
        Text(article?.description ?: "")
    }
}