package com.example.todoapppractice.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.todoapppractice.ui.NewsViewModel
import com.example.todoapppractice.ui.data.Article
import com.example.todoapppractice.ui.data.ListUIState

@Composable
fun ListScreen(
    viewModel: NewsViewModel,
    onItemClick: (Article) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LazyColumn(
        Modifier
            .wrapContentSize()
            .fillMaxSize()
    ) {
        when (uiState) {
            ListUIState.Error -> {
                item {
                    Text("Error please retry")
                }
            }

            ListUIState.Loading -> {
                item {
                    Box(
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

            }

            is ListUIState.Success -> {
                val articles = (uiState as ListUIState.Success).data
                itemsIndexed(articles, key = { _, item -> item.id }) { _, article ->

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                            .clickable { onItemClick(article) },
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp)
                        ) {

                            AsyncImage(
                                model = article.imageUrl,
                                contentDescription = null,
                                modifier = Modifier
                                    .size(80.dp)
                            )

                            Spacer(Modifier.width(12.dp))

                            Column {
                                Text(article.title, fontWeight = FontWeight.Bold)
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    article.description,
                                    maxLines = 2
                                )
                            }
                        }
                    }
                }
            }
        }


    }

}