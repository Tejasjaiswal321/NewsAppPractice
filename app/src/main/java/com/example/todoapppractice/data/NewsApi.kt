package com.example.todoapppractice.data

import retrofit2.Response
import retrofit2.http.GET

interface NewsApi {
    @GET("posts")
    suspend fun getPosts(): Response<List<PostDto>>
}