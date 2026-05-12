package com.example.todoapppractice.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.todoapppractice.ui.data.Article


@Database(entities = [Article::class], version = 1)
abstract class NewsDB : RoomDatabase() {
    abstract fun getDao(): NewsDao
}