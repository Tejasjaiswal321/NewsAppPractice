package com.example.todoapppractice.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todoapppractice.data.NetworkResult
import com.example.todoapppractice.domain.NewsRepository
import com.example.todoapppractice.ui.data.Article
import com.example.todoapppractice.ui.data.ListUIState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class NewsViewModel(
    private val repository: NewsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<ListUIState>(ListUIState.Loading)
    val uiState: StateFlow<ListUIState> = _uiState

    private val _selectedUIItem = MutableStateFlow<Article?>(null)
    val selectedUIItem: StateFlow<Article?> = _selectedUIItem

    init {
        fetchNews()
    }

    private fun fetchNews() {
        Log.d("NewsRepository", " NewsViewModel fetchNews")

        viewModelScope.launch(Dispatchers.IO) {
            _uiState.update {
                when (val result = repository.fetchNews()) {
                    is NetworkResult.ApiError,
                    is NetworkResult.OtherError,
                    is NetworkResult.NetworkError -> {
                        ListUIState.Error
                    }

                    is NetworkResult.Success -> {
                        ListUIState.Success(result.data)
                    }
                }
            }
        }
    }

    fun updateSelectedUIItem(article: Article) {
        _selectedUIItem.value = article
    }

}