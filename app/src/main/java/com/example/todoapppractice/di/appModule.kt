package com.example.todoapppractice.di

import androidx.room.Room
import com.example.todoapppractice.data.ApiClient
import com.example.todoapppractice.db.NewsDB
import com.example.todoapppractice.domain.NewsRepository
import com.example.todoapppractice.ui.NewsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single<ApiClient> {
        ApiClient()
    }
    single<NewsDB> {
        Room.databaseBuilder(
            get(),
            NewsDB::class.java,
            "news_database"
        ).build()
    }
    viewModel<NewsViewModel> {
        NewsViewModel(NewsRepository(get<ApiClient>().api, get<NewsDB>().getDao()))
    }
}