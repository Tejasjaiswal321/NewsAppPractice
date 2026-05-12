package com.example.todoapppractice.data

import com.example.todoapppractice.ui.data.Article

sealed interface NetworkResult {

    data class Success(val data: List<Article>) : NetworkResult

    data class ApiError(
        val code: Int,
        val message: String?
    ) : NetworkResult

    data class NetworkError(
        val exception: Throwable
    ) : NetworkResult

    object OtherError : NetworkResult
}