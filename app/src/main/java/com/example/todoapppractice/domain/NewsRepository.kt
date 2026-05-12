package com.example.todoapppractice.domain

import com.example.todoapppractice.data.NetworkResult
import com.example.todoapppractice.data.NewsApi
import com.example.todoapppractice.db.NewsDao
import com.example.todoapppractice.ui.data.Article

class NewsRepository(
    private val api: NewsApi,
    private val dao: NewsDao
) {

    suspend fun fetchNews(): NetworkResult {

        return try {

            val response = api.getPosts()

            if (response.isSuccessful) {

                val body = response.body() ?: emptyList()

                val articles = body
                    .take(15)
                    .map { dto ->
                        Article(
                            id = dto.id,
                            title = dto.title,
                            description = dto.body,
                            imageUrl = "https://picsum.photos/200/300?random=${dto.id}"
                        )
                    }
                dao.updateList(articles)

                NetworkResult.Success(articles)

            } else {
                val dbList = dao.getNewsList()
                if (dbList.isNotEmpty()) {
                    NetworkResult.Success(dbList)
                } else {
                    NetworkResult.ApiError(
                        code = response.code(),
                        message = response.message()
                    )
                }

            }

        } catch (e: Exception) {
            val dbList = dao.getNewsList()
            if (dbList.isNotEmpty()) {
                NetworkResult.Success(dbList)
            } else {
                NetworkResult.NetworkError(e)
            }
        }
    }
}