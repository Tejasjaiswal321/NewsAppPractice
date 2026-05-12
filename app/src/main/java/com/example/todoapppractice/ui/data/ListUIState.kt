package com.example.todoapppractice.ui.data

sealed interface ListUIState {
    class Success(val data: List<Article>) : ListUIState
    object Loading : ListUIState
    object Error : ListUIState
}